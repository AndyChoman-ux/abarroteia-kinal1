package main.java.com.vyorg.abarroteria.kinal.util;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import main.java.com.vyorg.abarroteria.kinal.model.Venta;

import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class ReporteVentasGenerator {

    private static final Color VERDE = new Color(46, 125, 50);
    private static final SimpleDateFormat FORMATO_FECHA = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    private ReporteVentasGenerator() {
    }

    public static File generarPdf(List<Venta> ventas) throws Exception {
        Document doc = new Document(PageSize.A4.rotate(), 30, 30, 30, 30);

        File carpeta = new File(System.getProperty("user.home"), "Reportes_Kinal");
        if (!carpeta.exists()) carpeta.mkdirs();
        File archivo = new File(carpeta, "reporte_ventas_" + UUID.randomUUID().toString().substring(0, 8) + ".pdf");

        PdfWriter.getInstance(doc, new FileOutputStream(archivo));
        doc.open();

        Font fTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, VERDE);
        Paragraph titulo = new Paragraph("Reporte de Ventas - Abarroteria Kinal", fTitulo);
        titulo.setAlignment(Element.ALIGN_CENTER);
        doc.add(titulo);

        Font fSub = FontFactory.getFont(FontFactory.HELVETICA, 10, Color.DARK_GRAY);
        Paragraph generado = new Paragraph("Generado: " + FORMATO_FECHA.format(new java.util.Date())
                + "   |   Total de registros: " + ventas.size(), fSub);
        generado.setAlignment(Element.ALIGN_CENTER);
        generado.setSpacingAfter(15);
        doc.add(generado);

        PdfPTable tabla = new PdfPTable(new float[]{1.2f, 1.6f, 2f, 1.5f, 1.2f, 1.2f, 1.5f});
        tabla.setWidthPercentage(100);

        Font fEncabezado = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Color.WHITE);
        String[] encabezados = {"Factura", "Fecha", "Cliente", "Metodo pago", "Total (Q)", "Estado", "Vendedor"};
        for (String texto : encabezados) {
            PdfPCell celda = new PdfPCell(new Phrase(texto, fEncabezado));
            celda.setBackgroundColor(VERDE);
            celda.setPadding(6);
            tabla.addCell(celda);
        }

        Font fCelda = FontFactory.getFont(FontFactory.HELVETICA, 9, Color.BLACK);
        double totalGeneral = 0;
        for (Venta venta : ventas) {
            tabla.addCell(new Phrase(venta.getNumeroFactura(), fCelda));
            tabla.addCell(new Phrase(venta.getFecha() == null ? "" : FORMATO_FECHA.format(venta.getFecha()), fCelda));
            tabla.addCell(new Phrase(venta.getCliente(), fCelda));
            tabla.addCell(new Phrase(venta.getMetodoPago(), fCelda));
            tabla.addCell(new Phrase(String.format(Locale.US, "Q%.2f", venta.getTotal()), fCelda));
            tabla.addCell(new Phrase(venta.getEstado(), fCelda));
            tabla.addCell(new Phrase(venta.getVendedor(), fCelda));
            totalGeneral += venta.getTotal();
        }
        doc.add(tabla);

        Font fTotal = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 13, VERDE);
        Paragraph total = new Paragraph("\nTotal general: Q" + String.format(Locale.US, "%.2f", totalGeneral), fTotal);
        total.setAlignment(Element.ALIGN_RIGHT);
        doc.add(total);

        doc.close();
        return archivo;
    }

    public static File generarCsv(List<Venta> ventas) throws IOException {
        File carpeta = new File(System.getProperty("user.home"), "Reportes_Kinal");
        if (!carpeta.exists()) carpeta.mkdirs();
        File archivo = new File(carpeta, "reporte_ventas_" + UUID.randomUUID().toString().substring(0, 8) + ".csv");

        try (PrintWriter writer = new PrintWriter(new FileWriter(archivo))) {
            writer.println("Factura,Fecha,Cliente,Metodo de pago,Total,Estado,Vendedor");
            for (Venta venta : ventas) {
                writer.println(
                        escapar(venta.getNumeroFactura()) + "," +
                        escapar(venta.getFecha() == null ? "" : FORMATO_FECHA.format(venta.getFecha())) + "," +
                        escapar(venta.getCliente()) + "," +
                        escapar(venta.getMetodoPago()) + "," +
                        venta.getTotal() + "," +
                        escapar(venta.getEstado()) + "," +
                        escapar(venta.getVendedor())
                );
            }
        }
        return archivo;
    }

    private static String escapar(String valor) {
        if (valor == null) return "";
        if (valor.contains(",") || valor.contains("\"")) {
            return "\"" + valor.replace("\"", "\"\"") + "\"";
        }
        return valor;
    }
}