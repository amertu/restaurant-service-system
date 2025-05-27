package com.spring.restaurant.backend.service.impl;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.spring.restaurant.backend.entity.*;
import com.spring.restaurant.backend.repository.BillRepository;
import com.spring.restaurant.backend.repository.PurchaseRepository;
import com.spring.restaurant.backend.repository.UserRepository;
import com.spring.restaurant.backend.service.BillService;
import jakarta.xml.bind.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

@Service
public class SimpleBillService implements BillService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final UserRepository userRepository;
    private final PurchaseRepository purchaseRepository;
    private final BillRepository billRepository;
    private static final Path TEMP_DIR = Paths.get(
        Objects.requireNonNull(System.getProperty("java.io.tmpdir"),
            "System property java.io.tmpdir not set"));
    private final int MAX_PDF_SIZE = 10 * 1024 * 1024; // 10MB limit

    @Autowired
    public SimpleBillService(UserRepository userRepository, PurchaseRepository purchaseRepository, BillRepository billRepository) {
        this.userRepository = userRepository;
        this.purchaseRepository = purchaseRepository;
        this.billRepository = billRepository;
    }


    @Override
    public List<Purchase> buyDishes(Bill bill) throws ValidationException {
        LOGGER.info("Bill with dishes: {}", bill);

//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
//        LocalDateTime paidAt = LocalDateTime.parse(bill.getPaidAt(), formatter);
        List<Dish> dishesList = bill.getDishes();
        if (!dishesList.isEmpty()) {
            for (Dish dish : dishesList) {
                Purchase temp = new Purchase();
                temp.setDish(dish);
                temp.setPaid(true);
                temp.setPaidAt(bill.getPaidAt());
                temp.setInvoiceId(bill.getInvoiceId());
                purchaseRepository.save(temp);
            }
        } else {
            throw new ValidationException("At least one dish is needed to be bought");
        }

        List<Purchase> purchases = purchaseRepository.findAllByInvoiceId(bill.getInvoiceId());
        LOGGER.info(purchases.toString());
        LOGGER.info("reservationStartedAt: " + bill.getReservationStartedAt());
        LOGGER.info("servedTables: " + bill.getServedTables());
        // TODO maybe pass just purchases and bill?
        createPdfOfBill(purchases, bill);
        return purchases;
    }

    @Override
    public void createPdfOfBill(List<Purchase> purchases, Bill bill) throws ValidationException {
        ApplicationUser user = currentUser();
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Document document = new Document();
            PdfWriter writer = PdfWriter.getInstance(document, baos);
            document.open();

            // Fonts
            Font addressFont = FontFactory.getFont(FontFactory.HELVETICA, 10);
            Font tableHeaderFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
            Font tableContentFont = FontFactory.getFont(FontFactory.HELVETICA, 11);
            Font taxTableContentFont = FontFactory.getFont(FontFactory.HELVETICA, 10);
            Font footerFont = FontFactory.getFont(FontFactory.HELVETICA, 10);

            // Header table
            PdfPTable headerTable = buildHeaderTable(bill, addressFont);

            // Main invoice table
            Map<Dish, Integer> itemCounts = countPurchases(purchases);
            PdfPTable invoiceTable = buildInvoiceTable(itemCounts, tableHeaderFont, tableContentFont);

            // Tax table
            PdfPTable taxTable = buildTaxTable(itemCounts, tableHeaderFont, taxTableContentFont);

            // Footer
            drawFooter(document, writer, footerFont, user);

            // Final layout
            Paragraph space = new Paragraph("\n\n");
            document.add(headerTable);
            document.add(space);
            document.add(invoiceTable);
            document.add(space);
            document.add(taxTable);
            document.close();
            byte[] pdfBytes = baos.toByteArray();
            if (pdfBytes.length > MAX_PDF_SIZE) {
                throw new ValidationException("Generated PDF exceeds maximum size of 10MB");
            }
            LOGGER.info("PDF size: {} bytes", pdfBytes.length);
            LOGGER.info("PDF type: {}", baos.toByteArray() instanceof byte[] ? "byte[]" : "unknown");

            bill.setPdf(pdfBytes);
            bill.setTotalCost(computeTotal(itemCounts));
            billRepository.save(bill);

        } catch (IOException | DocumentException e) {
            throw new ValidationException("Failed to create PDF: " + e.getMessage());
        }

    }

    private ApplicationUser currentUser() throws ValidationException {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String usermail = principal.toString();
        if (principal instanceof UserDetails) {
            usermail = ((UserDetails) principal).getUsername();
        }
        ApplicationUser user = userRepository.getUserByEmail(usermail);
        if (user == null) {
            throw new ValidationException("User not found for email: " + usermail);
        }
        LOGGER.info("Current user: {}", user);
        return user;
    }

    private PdfPTable buildHeaderTable(Bill bill, Font addressFont) throws IOException, DocumentException {
        Image logo = Image.getInstance("src/main/resources/image/leaf.png");
        logo.scaleAbsolute(15, 15);

        // --- Left: Logo + Restaurant Name ---
        Chunk logoChunk = new Chunk(logo, 0, -3); // slight Y offset to align with text
        Chunk nameChunk = new Chunk(" Spring Kitchen", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14));

        Phrase leftHeaderPhrase = new Phrase();
        leftHeaderPhrase.add(logoChunk);
        leftHeaderPhrase.add(nameChunk);

        PdfPCell leftHeaderCell = new PdfPCell(leftHeaderPhrase);
        leftHeaderCell.setBorder(Rectangle.NO_BORDER);
        leftHeaderCell.setVerticalAlignment(Element.ALIGN_TOP);
        leftHeaderCell.setHorizontalAlignment(Element.ALIGN_LEFT);

        // Build address lines
        String[] lines = getStrings(bill);
        PdfPTable addressTable = new PdfPTable(1);
        addressTable.setWidthPercentage(100);
        for (String line : lines) {
            if (line != null) {
                PdfPCell lineCell = new PdfPCell(new Phrase(line, addressFont));
                lineCell.setBorder(Rectangle.NO_BORDER);
                addressTable.addCell(lineCell);
            }
        }

        // Put the addressTable into a single cell on the right
        PdfPCell addressCell = new PdfPCell(addressTable);
        addressCell.setBorder(Rectangle.NO_BORDER);
        addressCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        addressCell.setVerticalAlignment(Element.ALIGN_TOP); // ✅ top alignment

        // Create the outer header table (2 columns)
        PdfPTable headerTable = new PdfPTable(2);
        headerTable.setWidthPercentage(100);
        headerTable.setWidths(new float[]{3.3f, 1});

        headerTable.addCell(leftHeaderCell);
        headerTable.addCell(addressCell);

        return headerTable;
    }

    private String[] getStrings(Bill bill) {
        LocalDateTime paid = bill.getPaidAt();
        String reservationStartedAt = bill.getReservationStartedAt();
        String servedTables = bill.getServedTables();
        Long invoiceId = bill.getInvoiceId();

        return new String[]{
            "Spring Kitchen GmbH", "Frühlingsstraße 76", "www.springkitchen.at",
            "springkitchen@mail.com", "Tel/Fax +43(0)1/1234567", "VATIN: ATU87654321",
            "Date: " + paid,
            reservationStartedAt != null ? "Reservation started at: " + reservationStartedAt : null,
            servedTables != null && !servedTables.isBlank() ? "Served tables: " + servedTables : null,
            "Invoice number: " + invoiceId
        };
    }

    private Map<Dish, Integer> countPurchases(List<Purchase> purchases) {
        Map<Dish, Integer> count = new HashMap<>();
        for (Purchase p : purchases) {
            if (p != null && p.getDish() != null) {
                count.merge(p.getDish(), 1, Integer::sum);
            }
        }
        return count;
    }

    private PdfPTable buildInvoiceTable(Map<Dish, Integer> count, Font headerFont, Font contentFont) {
        PdfPTable table = new PdfPTable(3);
        table.setSpacingBefore(10f);
        table.getDefaultCell().setBorder(Rectangle.NO_BORDER);

        Stream.of("QTY", "Name", "Price").forEach(header -> {
            PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
            cell.setBorder(Rectangle.BOTTOM);
            table.addCell(cell);
        });

        count.forEach((dish, qty) -> {
            table.addCell(new Phrase(String.valueOf(qty), contentFont));
            table.addCell(new Phrase(dish.getName(), contentFont));
            double price = (dish.getPrice() * qty) / 100.0;
            table.addCell(new Phrase(String.format("%.2f €", price), contentFont));
        });

        table.setSpacingAfter(13f);
        return table;
    }

    private PdfPTable buildTaxTable(Map<Dish, Integer> count, Font headerFont, Font contentFont) {
        double foodPrice = 0, drinkPrice = 0;
        for (Map.Entry<Dish, Integer> entry : count.entrySet()) {
            double price = entry.getKey().getPrice() * entry.getValue();
            if (entry.getKey().getCategory() == Category.FOOD) foodPrice += price;
            else drinkPrice += price;
        }

        double foodGross = round(foodPrice / 100);
        double drinkGross = round(drinkPrice / 100);
        double foodTax = round(foodGross * 10 / 110);
        double drinkTax = round(drinkGross * 20 / 120);
        double foodNet = round(foodGross - foodTax);
        double drinkNet = round(drinkGross - drinkTax);

        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(52);
        table.getDefaultCell().setBorder(Rectangle.NO_BORDER);

        Stream.of("VAT", "Net", "Tax", "Gross").forEach(label ->
            table.addCell(new Phrase(label, contentFont)));

        table.addCell(new Phrase("10.00%", contentFont));
        table.addCell(new Phrase(String.format("%.2f", foodNet), contentFont));
        table.addCell(new Phrase(String.format("%.2f", foodTax), contentFont));
        table.addCell(new Phrase(String.format("%.2f", foodGross), contentFont));

        table.addCell(new Phrase("20.00%", contentFont));
        table.addCell(new Phrase(String.format("%.2f", drinkNet), contentFont));
        table.addCell(new Phrase(String.format("%.2f", drinkTax), contentFont));
        table.addCell(new Phrase(String.format("%.2f", drinkGross), contentFont));

        PdfPCell totalText = new PdfPCell(new Phrase("Total price:", headerFont));
        totalText.setColspan(3);
        totalText.setBorder(Rectangle.TOP);
        table.addCell(totalText);

        PdfPCell totalCell = new PdfPCell(new Phrase(String.format("%.2f EUR", round(foodGross + drinkGross)), headerFont));
        totalCell.setColspan(1);
        totalCell.setHorizontalAlignment(Element.ALIGN_MIDDLE);
        totalCell.setBorder(Rectangle.TOP);
        table.addCell(totalCell);

        return table;
    }

    private double computeTotal(Map<Dish, Integer> count) {
        return count.entrySet().stream()
            .mapToDouble(e -> e.getKey().getPrice() * e.getValue() / 100.0)
            .sum();
    }

    private void drawFooter(Document document, PdfWriter writer, Font font, ApplicationUser user) {
        PdfContentByte canvas = writer.getDirectContent();
        canvas.moveTo(80, document.bottom() + 10);
        canvas.lineTo(520, document.bottom() + 10);
        canvas.stroke();

        Rectangle page = document.getPageSize();
        PdfPTable footerTable = new PdfPTable(1);
        footerTable.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
        footerTable.getDefaultCell().setBorder(Rectangle.NO_BORDER);
        footerTable.addCell(new Phrase("Thank you for your visit!", font));
        footerTable.addCell(new Phrase("You have been served by " + user.getFirstName() + " " + user.getLastName(), font));
        footerTable.setTotalWidth(page.getWidth() - document.leftMargin() - document.rightMargin());
        footerTable.writeSelectedRows(0, -1, 40, document.bottomMargin() + 5, canvas);
    }

    private double round(double value) {
        double scale = Math.pow(10, 2);
        return Math.round(value * scale) / scale;
    }


    @Override
    public List<Bill> getAllBills() {
        LOGGER.debug("Get all bills");
        return billRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Bill getBillByInvoiceId(Long invoiceId) {
        LOGGER.info("Finding Bill for invoice id {}", invoiceId);
        return billRepository.findByInvoiceId(invoiceId);
    }
}
