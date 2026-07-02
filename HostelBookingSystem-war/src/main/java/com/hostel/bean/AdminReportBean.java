package com.hostel.bean;

import com.hostel.service.ReportService;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.DefaultStreamedContent;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Named
@ViewScoped
public class AdminReportBean implements Serializable {
    @Inject private ReportService reportService;
    private Map<String, Long> bookingStatusSummary;
    private Map<String, Long> roomStatusSummary;
    private BigDecimal totalRevenue;
    private long totalRooms;
    private long totalBookings;
    private long pendingBookings;
    private double occupancyRate;

    @PostConstruct
    public void init() {
        bookingStatusSummary = reportService.getBookingStatusSummary();
        roomStatusSummary    = reportService.getRoomStatusSummary();
        totalRevenue         = reportService.getTotalRevenue();
        totalRooms           = reportService.getTotalRooms();
        totalBookings        = reportService.getTotalBookings();
        pendingBookings      = reportService.getPendingBookings();
        occupancyRate        = reportService.getOccupancyRate();
    }

    public Map<String, Long> getBookingStatusSummary() { return bookingStatusSummary; }
    public Map<String, Long> getRoomStatusSummary() { return roomStatusSummary; }
    public BigDecimal getTotalRevenue() { return totalRevenue; }
    public long getTotalRooms() { return totalRooms; }
    public long getTotalBookings() { return totalBookings; }
    public long getPendingBookings() { return pendingBookings; }
    public double getOccupancyRate() { return occupancyRate; }

    public StreamedContent exportReportPdf() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 40, 40, 50, 40);

        try {
            PdfWriter.getInstance(document, baos);
            document.open();

            Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD, BaseColor.DARK_GRAY);
            Font sectionFont = new Font(Font.FontFamily.HELVETICA, 13, Font.BOLD, new BaseColor(37, 99, 235));
            Font labelFont = new Font(Font.FontFamily.HELVETICA, 11, Font.NORMAL);
            Font valueFont = new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD);
            Font smallFont = new Font(Font.FontFamily.HELVETICA, 9, Font.ITALIC, BaseColor.GRAY);

            Paragraph title = new Paragraph("Hostel Room Booking System - Report", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(4);
            document.add(title);

            Paragraph generated = new Paragraph(
                    "Generated: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")),
                    smallFont);
            generated.setAlignment(Element.ALIGN_CENTER);
            generated.setSpacingAfter(20);
            document.add(generated);

            // Summary section
            document.add(new Paragraph("Summary", sectionFont));
            document.add(Chunk.NEWLINE);

            PdfPTable summaryTable = new PdfPTable(2);
            summaryTable.setWidthPercentage(100);
            summaryTable.setSpacingAfter(20);
            addRow(summaryTable, "Total Rooms", String.valueOf(totalRooms), labelFont, valueFont);
            addRow(summaryTable, "Total Bookings", String.valueOf(totalBookings), labelFont, valueFont);
            addRow(summaryTable, "Total Revenue", "RM " + totalRevenue, labelFont, valueFont);
            addRow(summaryTable, "Occupancy Rate", String.format("%.1f%%", occupancyRate), labelFont, valueFont);
            addRow(summaryTable, "Pending Bookings", String.valueOf(pendingBookings), labelFont, valueFont);
            document.add(summaryTable);

            // Booking status summary
            document.add(new Paragraph("Booking Status Summary", sectionFont));
            document.add(Chunk.NEWLINE);
            document.add(buildStatusTable(bookingStatusSummary, labelFont, valueFont));
            document.add(Chunk.NEWLINE);

            // Room status summary
            document.add(new Paragraph("Room Availability Summary", sectionFont));
            document.add(Chunk.NEWLINE);
            document.add(buildStatusTable(roomStatusSummary, labelFont, valueFont));

            document.close();
        } catch (DocumentException e) {
            throw new RuntimeException("Failed to generate PDF report", e);
        }

        byte[] bytes = baos.toByteArray();
        String fileName = "hostel_report_" +
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmm")) + ".pdf";

        return DefaultStreamedContent.builder()
                .name(fileName)
                .contentType("application/pdf")
                .stream(() -> new ByteArrayInputStream(bytes))
                .build();
    }

    private void addRow(PdfPTable table, String label, String value, Font labelFont, Font valueFont) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label, labelFont));
        labelCell.setBorder(Rectangle.BOTTOM);
        labelCell.setPaddingBottom(6);
        labelCell.setPaddingTop(6);

        PdfPCell valueCell = new PdfPCell(new Phrase(value, valueFont));
        valueCell.setBorder(Rectangle.BOTTOM);
        valueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        valueCell.setPaddingBottom(6);
        valueCell.setPaddingTop(6);

        table.addCell(labelCell);
        table.addCell(valueCell);
    }

    private PdfPTable buildStatusTable(Map<String, Long> data, Font labelFont, Font valueFont) {
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);

        PdfPCell header1 = new PdfPCell(new Phrase("Status", valueFont));
        header1.setBackgroundColor(new BaseColor(241, 245, 249));
        header1.setPadding(6);
        PdfPCell header2 = new PdfPCell(new Phrase("Count", valueFont));
        header2.setBackgroundColor(new BaseColor(241, 245, 249));
        header2.setPadding(6);
        header2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell(header1);
        table.addCell(header2);

        if (data != null) {
            for (Map.Entry<String, Long> entry : data.entrySet()) {
                addRow(table, entry.getKey(), String.valueOf(entry.getValue()), labelFont, labelFont);
            }
        }
        return table;
    }
}