package com.cineplan.service;

import com.cineplan.model.Marathon;
import com.cineplan.model.Movie;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class PDFExportService {

    public static void exportMarathon(Marathon marathon, File file) throws IOException, DocumentException {
        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream(file));
        document.open();

        // Fonts
        Font titleFont = new Font(Font.HELVETICA, 24, Font.BOLD, new Color(229, 9, 20)); // Netflix red
        Font subtitleFont = new Font(Font.HELVETICA, 12, Font.ITALIC, Color.GRAY);
        Font sectionFont = new Font(Font.HELVETICA, 14, Font.BOLD, Color.BLACK);
        Font bodyFont = new Font(Font.HELVETICA, 10, Font.NORMAL, Color.BLACK);
        Font headerFont = new Font(Font.HELVETICA, 10, Font.BOLD, Color.WHITE);

        // Header Title
        Paragraph title = new Paragraph("CinePlan - Marathon Report", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);

        Paragraph subtitle = new Paragraph("Your Smart Movie Marathon Schedule", subtitleFont);
        subtitle.setAlignment(Element.ALIGN_CENTER);
        subtitle.setSpacingAfter(20);
        document.add(subtitle);

        // Marathon Details
        Paragraph detailHeading = new Paragraph("Marathon Details", sectionFont);
        detailHeading.setSpacingAfter(8);
        document.add(detailHeading);

        document.add(new Paragraph("Marathon Name: " + marathon.getMarathonName(), bodyFont));
        document.add(new Paragraph("Mood: " + marathon.getMood(), bodyFont));
        document.add(new Paragraph("Total Runtime: " + marathon.getTotalRuntime() + " minutes", bodyFont));
        document.add(new Paragraph("Total Entertainment Score: " + marathon.getTotalScore(), bodyFont));
        document.add(new Paragraph("Created At: " + marathon.getCreatedAt(), bodyFont));

        Paragraph spacer = new Paragraph(" ");
        spacer.setSpacingAfter(15);
        document.add(spacer);

        // Movie Table Header
        Paragraph moviesHeading = new Paragraph("Scheduled Movies", sectionFont);
        moviesHeading.setSpacingAfter(8);
        document.add(moviesHeading);

        PdfPTable table = new PdfPTable(5); // 5 columns
        table.setWidthPercentage(100);
        table.setWidths(new float[]{3f, 1.5f, 1.5f, 1.2f, 1.2f}); // column relative widths

        // Headers
        String[] headers = {"Title", "Genre", "Runtime", "Rating", "Score"};
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
            cell.setBackgroundColor(new Color(229, 9, 20)); // Red background
            cell.setPadding(8);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
        }

        // Table Rows
        boolean alternate = false;
        Color altColor = new Color(245, 245, 245);
        for (Movie m : marathon.getMovies()) {
            Color rowColor = alternate ? altColor : Color.WHITE;

            PdfPCell cell1 = new PdfPCell(new Phrase(m.getTitle(), bodyFont));
            cell1.setBackgroundColor(rowColor);
            cell1.setPadding(6);
            table.addCell(cell1);

            PdfPCell cell2 = new PdfPCell(new Phrase(m.getGenre(), bodyFont));
            cell2.setBackgroundColor(rowColor);
            cell2.setPadding(6);
            cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell2);

            PdfPCell cell3 = new PdfPCell(new Phrase(m.getRuntime() + " min", bodyFont));
            cell3.setBackgroundColor(rowColor);
            cell3.setPadding(6);
            cell3.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell3);

            PdfPCell cell4 = new PdfPCell(new Phrase(String.format("%.1f", m.getRating()), bodyFont));
            cell4.setBackgroundColor(rowColor);
            cell4.setPadding(6);
            cell4.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell4);

            PdfPCell cell5 = new PdfPCell(new Phrase(String.valueOf(m.getEntertainmentScore()), bodyFont));
            cell5.setBackgroundColor(rowColor);
            cell5.setPadding(6);
            cell5.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell5);

            alternate = !alternate;
        }

        document.add(table);

        // Footer note
        Paragraph footerSpacer = new Paragraph(" ");
        footerSpacer.setSpacingBefore(30);
        document.add(footerSpacer);

        Paragraph footer = new Paragraph("Generated by CinePlan App. Enjoy your movie marathon!", subtitleFont);
        footer.setAlignment(Element.ALIGN_CENTER);
        document.add(footer);

        document.close();
    }
}
