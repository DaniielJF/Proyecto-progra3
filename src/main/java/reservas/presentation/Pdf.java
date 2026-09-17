package reservas.presentation;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;

import java.awt.*;
import java.io.File;
import java.util.List;

public class Pdf {

    public static void imprimir(String titulo, String[] encabezados, List<String[]> filas, String destino) {
        try {
            PdfWriter writer = new PdfWriter(destino);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);
            document.setMargins(20, 20, 20, 20);

            document.add(new Paragraph(titulo).setBold().setFontSize(16).setTextAlignment(TextAlignment.CENTER));
            document.add(new Paragraph(" "));

            Table table = new Table(encabezados.length);
            table.setWidth(UnitValue.createPercentValue(100));

            for (String encabezado : encabezados) {
                Cell celda = new Cell().add(new Paragraph(encabezado).setBold());
                celda.setBackgroundColor(ColorConstants.LIGHT_GRAY);
                table.addHeaderCell(celda);
            }

            for (String[] fila : filas) {
                for (String valor : fila) {
                    table.addCell(new Cell().add(new Paragraph(valor == null ? "" : valor)));
                }
            }

            document.add(table);
            document.close();
            abrirPdf(destino);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void abrirPdf(String path) {
        try {
            File pdfFile = new File(path);
            if (pdfFile.exists() && Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(pdfFile);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
