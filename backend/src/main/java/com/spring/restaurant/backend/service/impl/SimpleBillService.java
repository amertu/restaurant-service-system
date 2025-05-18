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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.xml.bind.ValidationException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@Service
public class SimpleBillService implements BillService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final UserRepository userRepository;
    private final PurchaseRepository purchaseRepository;
    private final BillRepository billRepository;

    @Autowired
    public SimpleBillService(UserRepository userRepository, PurchaseRepository purchaseRepository, BillRepository billRepository) {
        this.userRepository = userRepository;
        this.purchaseRepository = purchaseRepository;
        this.billRepository = billRepository;
    }


    @Override
    public List<Purchase> buyDishes(String email, Bill bill) throws ValidationException {
        LOGGER.info("Buy dishes by user with email: {}", email);
        LOGGER.info("Bill with dishes: " + bill);
        ApplicationUser user = userRepository.getUserByEmail(email);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime paidAt = LocalDateTime.parse(LocalDateTime.now().toString().replace('T', ' ').substring(0, 16), formatter);
        List<Dish> dishesList = bill.getDishes();
        if (!dishesList.isEmpty()) {
            for (Dish dish : dishesList) {
                Purchase temp = new Purchase();
                temp.setDish(dish);
                temp.setPaid(true);
                temp.setPaidAt(paidAt);
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
        createPdfOfBill(purchases, bill.getInvoiceId(), paidAt, bill.getReservationStartedAt(), bill.getServedTables(), user);
        return purchases;
    }

    @Override
    public void createPdfOfBill(List<Purchase> purchases, Long invoiceId, LocalDateTime paidAt,
                                String reservationStartedAt, String servedTables, ApplicationUser user) {

        LOGGER.info("Creating invoice PDF");
        final String pdfName = "invoice.pdf";
        final String date = paidAt.toLocalDate().toString();

        try (FileOutputStream fos = new FileOutputStream(pdfName)) {
            Document document = new Document();
            PdfWriter writer = PdfWriter.getInstance(document, fos);
            document.open();

            // Fonts
            Font addressFont = FontFactory.getFont(FontFactory.HELVETICA, 10);
            Font tableHeaderFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
            Font tableContentFont = FontFactory.getFont(FontFactory.HELVETICA, 11);
            Font taxTableContentFont = FontFactory.getFont(FontFactory.HELVETICA, 10);
            Font footerFont = FontFactory.getFont(FontFactory.HELVETICA, 10);

            // Header table
            PdfPTable headerTable = buildHeaderTable(date, reservationStartedAt, servedTables, invoiceId, addressFont);

            // Main invoice table
            Map<Dish, Integer> itemCounts = countPurchases(purchases);
            PdfPTable invoiceTable = buildInvoiceTable(itemCounts, tableHeaderFont, tableContentFont);

            // Tax table
            PdfPTable taxTable = buildTaxTable(itemCounts, tableHeaderFont, taxTableContentFont);
            double total = computeTotal(itemCounts);

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

            saveBill(pdfName, invoiceId, paidAt, total, reservationStartedAt, servedTables);

        } catch (IOException | DocumentException e) {
            LOGGER.error("Error creating invoice PDF: {}", e.getMessage(), e);
        }
    }

    private PdfPTable buildHeaderTable(String date, String reservationStartedAt, String servedTables,
                                       Long invoiceId, Font addressFont) throws IOException, DocumentException {
        Image logo = Image.getInstance("src/main/resources/image/leaf.png");
        logo.scaleAbsolute(15, 15);
        Paragraph logoParagraph = new Paragraph();
        logoParagraph.add(new Chunk(logo, 0, 0));
        logoParagraph.add(new Phrase("Spring Kitchen", FontFactory.getFont(FontFactory.COURIER, 16)));

        PdfPCell logoCell = new PdfPCell();
        logoCell.setBorder(Rectangle.NO_BORDER);
        logoCell.addElement(logoParagraph);

        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{3.3f, 1});
        table.getDefaultCell().setBorder(Rectangle.NO_BORDER);

        String[] lines = new String[]{
            "Spring Kitchen GmbH", "Frühlingsstraße 76", "www.springkitchen.at",
            "springkitchen@mail.com", "Tel/Fax +43(0)1/1234567", "VATIN: ATU87654321",
            "Date: " + date,
            reservationStartedAt != null ? "Reservation started at: " + reservationStartedAt : null,
            servedTables != null && !servedTables.isBlank() ? "Served tables: " + servedTables : null,
            "Invoice number: " + invoiceId
        };

        for (String line : lines) {
            if (line != null) {
                table.addCell(" ");
                table.addCell(new Phrase(line, addressFont));
            }
        }

        table.addCell(logoCell);
        table.addCell(" ");
        return table;
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

        double foodGross = round(foodPrice / 100, 2);
        double drinkGross = round(drinkPrice / 100, 2);
        double foodTax = round(foodGross * 10 / 110, 2);
        double drinkTax = round(drinkGross * 20 / 120, 2);
        double foodNet = round(foodGross - foodTax, 2);
        double drinkNet = round(drinkGross - drinkTax, 2);

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

        PdfPCell totalCell = new PdfPCell(new Phrase(String.format("%.2f EUR", round(foodGross + drinkGross, 2)), headerFont));
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

    private double round(double value, int places) {
        double scale = Math.pow(10, places);
        return Math.round(value * scale) / scale;
    }


    @Override
    @Transactional
    public void saveBill(String filename,
                         Long invoiceId,
                         LocalDateTime paidAt,
                         double price,
                         String reservationStartedAt,
                         String servedTables) throws IOException {

        byte[] pdfBytes = Files.readAllBytes(Paths.get(filename));

        Bill bill = new Bill(invoiceId, pdfBytes, paidAt, price);
        bill.setReservationStartedAt(reservationStartedAt);
        bill.setServedTables(servedTables);

        if (billRepository.findByInvoiceId(invoiceId) == null) {
            billRepository.save(bill);
        }

        Files.deleteIfExists(Paths.get(filename));
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
