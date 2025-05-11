package com.spring.restaurant.backend.service.impl;

import com.spring.restaurant.backend.entity.*;
import com.spring.restaurant.backend.repository.BillRepository;
import com.spring.restaurant.backend.repository.PurchaseRepository;
import com.spring.restaurant.backend.repository.UserRepository;
import com.spring.restaurant.backend.service.BillService;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.xml.bind.ValidationException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public void createPdfOfBill(List<Purchase> purchases, Long invoiceId, LocalDateTime paidAt, String reservationStartedAt, String servedTables,  ApplicationUser user) {

        LOGGER.info("creating invoice");
        String pdfName = "invoice.pdf";

        Document document = new Document();
        String date = paidAt.toString().substring(0, 10);

        try {
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(pdfName));
            document.open();

            //Fonts
            Font addressFont = FontFactory.getFont(FontFactory.HELVETICA, 10);
            Font tableHeaderFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
            Font tableContentFont = FontFactory.getFont(FontFactory.HELVETICA, 11);
            Font taxTableContentFont = FontFactory.getFont(FontFactory.HELVETICA, 10);
            Font footerFont = FontFactory.getFont(FontFactory.HELVETICA, 10);

            // Initialize an image and add it with para restaurant name
            Image image = Image.getInstance("src/main/resources/image/leaf.png");
            image.scaleAbsolute(15, 15);
            PdfPCell logoParaTextCell = new PdfPCell();
            logoParaTextCell.setBorder(Rectangle.NO_BORDER);
            Paragraph p = new Paragraph();
            p.add(new Chunk(image, 0, 0));
            p.add(new Phrase("Spring Kitchen", FontFactory.getFont(FontFactory.COURIER, 16, BaseColor.BLACK)));
            logoParaTextCell.addElement(p);

            // Initialize a table for address, date, invoice nr., restaurant name and logo
            PdfPTable headerTable = new PdfPTable(2);
            headerTable.setWidthPercentage(100);
            headerTable.setWidths(new float[]{3.3f,1});
            headerTable.getDefaultCell().setBorder(Rectangle.NO_BORDER);
            headerTable.addCell(" ");
            headerTable.addCell(new Phrase("Spring Kitchen GmbH", addressFont));
            headerTable.addCell(" ");
            headerTable.addCell(new Phrase("Frühlingsstraße 76", addressFont));
            headerTable.addCell(" ");
            headerTable.addCell(new Phrase("www.springkitchen.at", addressFont));
            headerTable.addCell(" ");
            headerTable.addCell(new Phrase("springkitchen@mail.com", addressFont));
            headerTable.addCell(" ");
            headerTable.addCell(new Phrase("Tel/Fax +43(0)1/1234567", addressFont));
            headerTable.addCell(" ");
            headerTable.addCell(new Phrase("VATIN: ATU87654321", addressFont));
            headerTable.addCell(logoParaTextCell);
            headerTable.addCell(new Phrase(" "));
            headerTable.addCell(" ");
            headerTable.addCell(new Phrase("Date: " + date, addressFont));
            headerTable.addCell(" ");
            if( null != reservationStartedAt) {
                headerTable.addCell(new Phrase("Reservation started at: " + reservationStartedAt, addressFont));
                headerTable.addCell(" ");
            }
            if( null != servedTables && !("".equals(servedTables))){
                headerTable.addCell(new Phrase("Served tables: " + servedTables, addressFont));
                headerTable.addCell(" ");
            }
            headerTable.addCell(new Phrase("Invoice number: " + invoiceId, addressFont));

            // Main invoice table
            // Header row of main invoice table
            PdfPTable invoiceTable = new PdfPTable(3);
            invoiceTable.getDefaultCell().setBorder(Rectangle.NO_BORDER);
            //invoiceTable.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
            invoiceTable.setSpacingBefore(10f);
            PdfPCell QTYCell = new PdfPCell();
            QTYCell.setBorder(Rectangle.BOTTOM);
            QTYCell.addElement(new Phrase("QTY", tableHeaderFont));
            invoiceTable.addCell(QTYCell);
            PdfPCell nameCell = new PdfPCell();
            nameCell.setBorder(Rectangle.BOTTOM);
            nameCell.addElement(new Phrase("Name", tableHeaderFont));
            invoiceTable.addCell(nameCell);
            PdfPCell priceCell = new PdfPCell();
            priceCell.setBorder(Rectangle.BOTTOM);
            priceCell.addElement(new Phrase("Price", tableHeaderFont));
            invoiceTable.addCell(priceCell);

            // Map to count QTY for each dish
            Map<Dish, Integer> count = new HashMap<Dish, Integer>();
            for (int i = 0; i < purchases.size(); i++) {
                if (purchases.get(i) != null) {
                    if (!count.containsKey(purchases.get(i).getDish())) {
                        count.put(purchases.get(i).getDish(), 1);
                    } else {
                        count.put(purchases.get(i).getDish(), count.get(purchases.get(i).getDish()) + 1);
                    }
                }
            }

            // Content cells of main invoice table
            double foodPrice = 0;
            double drinkPrice = 0;
            if (!count.isEmpty()) {
                for (Dish d : count.keySet()) {
                    invoiceTable.addCell(new Phrase(count.get(d).toString(), tableContentFont));
                    invoiceTable.addCell(new Phrase(d.getName(), tableContentFont));
                    invoiceTable.addCell(new Phrase(String.format("%.2f",d.getPrice() * count.get(d) / 100.0) + " €", tableContentFont));

                    if (d.getCategory().equals(Category.FOOD)) {
                        foodPrice += (d.getPrice() * count.get(d));
                    } else {
                        drinkPrice += (d.getPrice() * count.get(d));
                    }

                }
            }

            double foodPriceRounded = round(foodPrice/100, 2);
            double drinkPriceRounded = round(drinkPrice/100, 2);
            double total = round(foodPriceRounded + drinkPriceRounded,2);
            invoiceTable.setSpacingAfter(13f);

            // Tax table
            double foodTax = round(foodPriceRounded * 10 / 110,2);
            double drinkTax = round(drinkPriceRounded * 20 / 120,2);
            double foodNetPrice = round(foodPriceRounded - foodTax,2);
            double drinkNetPrice = round(drinkPriceRounded - drinkTax,2);
            PdfPTable taxTable = new PdfPTable(4);
            taxTable.getDefaultCell().setBorder(Rectangle.NO_BORDER);
            taxTable.setWidthPercentage(52);
            taxTable.addCell(new Phrase("VAT", taxTableContentFont));
            taxTable.addCell(new Phrase("Net", taxTableContentFont));
            taxTable.addCell(new Phrase("Tax", taxTableContentFont));
            taxTable.addCell(new Phrase("Gross", taxTableContentFont));
            taxTable.addCell(new Phrase("10.00%", taxTableContentFont));
            taxTable.addCell(new Phrase(String.format("%.2f", foodNetPrice), taxTableContentFont));
            taxTable.addCell(new Phrase(String.format("%.2f", foodTax), taxTableContentFont));
            taxTable.addCell(new Phrase(String.format("%.2f", foodPriceRounded), taxTableContentFont));
            taxTable.addCell(new Phrase("20.00%", taxTableContentFont));
            taxTable.addCell(new Phrase(String.format("%.2f", drinkNetPrice), taxTableContentFont));
            taxTable.addCell(new Phrase(String.format("%.2f", drinkTax), taxTableContentFont));
            taxTable.addCell(new Phrase(String.format("%.2f", drinkPriceRounded), taxTableContentFont));
            PdfPCell totalPriceTextCell = new PdfPCell(new Phrase("Total price: ", tableHeaderFont));
            totalPriceTextCell.setBorder(Rectangle.TOP);
            totalPriceTextCell.setColspan(3);
            taxTable.addCell(totalPriceTextCell);
            PdfPCell totalPriceCell = new PdfPCell(new Phrase(String.format("%.2f", total) + " EUR", tableHeaderFont));
            totalPriceCell.setBorder(Rectangle.TOP);
            totalPriceCell.setHorizontalAlignment(Element.ALIGN_MIDDLE);
            totalPriceCell.setColspan(1);
            taxTable.addCell(totalPriceCell);

            // Draw a footer line
            PdfContentByte canvas = writer.getDirectContent();
            canvas.moveTo(80, document.bottom() + 10);
            canvas.lineTo(520, document.bottom() + 10);
            canvas.closePathStroke();
            // Add footer content
            Rectangle page = document.getPageSize();
            PdfPTable footerTable = new PdfPTable(1);
            footerTable.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
            footerTable.getDefaultCell().setBorder(Rectangle.NO_BORDER);
            footerTable.addCell(new Phrase("Thank you for your visit!", footerFont));
            footerTable.addCell(new Phrase("You have been served by " + user.getFirstName() + " " + user.getLastName(), footerFont));
            footerTable.setTotalWidth(page.getWidth() - document.leftMargin() - document.rightMargin());
            footerTable.writeSelectedRows(0, -1, 40 , document.bottomMargin() +5, writer.getDirectContent());


            Paragraph space = new Paragraph("\n" + "\n");
            document.add(headerTable);
            document.add(space);
            document.add(space);
            document.add(invoiceTable);
            document.add(space);
            document.add(taxTable);
            document.close();
            saveBill(pdfName, invoiceId, paidAt, total, reservationStartedAt, servedTables);

        } catch (IOException | DocumentException e) {
            LOGGER.error("Error creating invoice PDF: {}", e.getMessage());
        }
    }

    private double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(Double.toString(value));
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    @Override
    @Transactional
    public void saveBill(String filename, Long invoiceId, LocalDateTime paidAt, double price, String reservationStartedAt, String servedTables) throws IOException {

        byte[] input_file = Files.readAllBytes(Paths.get(filename));
        byte[] encodedBytes = Base64.getEncoder().encode(input_file);
        Bill bill = new Bill(invoiceId, encodedBytes, paidAt, price);
        bill.setReservationStartedAt(reservationStartedAt);
        bill.setServedTables(servedTables);

        if (billRepository.findByInvoiceId(invoiceId) == null) {
            LOGGER.info("Billing");
            billRepository.save(bill);
        }
        File file = new File(filename);
        if (file.delete()) {
            LOGGER.info("Deleted bill");
        } else {
            LOGGER.info("Not deleted successfully!");
        }
    }

    @Override
    public List<Bill> getAllBills() {
        LOGGER.debug("Get all bills");
        return billRepository.findAll();
    }

    @Override
    public Bill getBillByInvoiceId(Long invoiceId) {
        LOGGER.info("Finding Bill for invoice id {}", invoiceId);
        return billRepository.findByInvoiceId(invoiceId);
    }
}
