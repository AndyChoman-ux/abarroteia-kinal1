package main.java.com.vyorg.abarroteria.kinal.util;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import main.java.com.vyorg.abarroteria.kinal.model.CarritoItem;

import java.awt.Color;
import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class FacturaPdfGenerator {

    private static final Color VERDE = new Color(46, 125, 50);   // #2E7D32
    private static final Color NARANJA = new Color(230, 81, 0);  // #E65100

    public static File generar(String cliente, List<CarritoItem> carrito, BigDecimal total) throws Exception {
        Document doc = new Document(PageSize.A4, 40, 40, 40, 40);

        File carpeta = new File(System.getProperty("user.home"), "Facturas_Kinal");
        if (!carpeta.exists()) carpeta.mkdirs();
        File archivo = new File(carpeta, "factura_" + System.currentTimeMillis() + ".pdf");

        PdfWriter.getInstance(doc, new FileOutputStream(archivo));
        doc.open();

        Font fTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, VERDE);
        Paragraph titulo = new Paragraph("Abarroteria Kinal", fTitulo);
        titulo.setAlignment(Element.ALIGN_CENTER);
        doc.add(titulo);

        Font fSub = FontFactory.getFont(FontFactory.HELVETICA, 10, Color.DARK_GRAY);
        Paragraph datosEmpresa = new Paragraph(
                "Abarroteria Kinal, S.A.  |  NIT: 1234567-8\n" +
                "Calzada Mateo Flores 15-71, Zona 7, Guatemala\n" +
                "Tel: PBX (502) 2200-0000  |  servicio@abarroteriakinal.com", fSub);
        datosEmpresa.setAlignment(Element.ALIGN_CENTER);
        doc.add(datosEmpresa);
        doc.add(new Paragraph(" "));

        PdfPTable barra = new PdfPTable(1);
        barra.setWidthPercentage(100);
        PdfPCell celdaBarra = new PdfPCell(new Phrase("FACTURA ELECTRONICA",
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, Color.WHITE)));
        celdaBarra.setBackgroundColor(VERDE);
        celdaBarra.setHorizontalAlignment(Element.ALIGN_CENTER);
        celdaBarra.setPadding(8);
        barra.addCell(celdaBarra);
        doc.add(barra);
        doc.add(new Paragraph(" "));

        String serie = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String numero = String.valueOf(System.currentTimeMillis());
        String fecha = new SimpleDateFormat("dd/MM/yyyy").format(new Date());

        PdfPTable datosFactura = new PdfPTable(3);
        datosFactura.setWidthPercentage(100);
        agregarCeldaInfo(datosFactura, "Serie", serie);
        agregarCeldaInfo(datosFactura, "Numero", numero);
        agregarCeldaInfo(datosFactura, "Fecha", fecha);
        doc.add(datosFactura);
        doc.add(new Paragraph(" "));

        doc.add(new Paragraph("DATOS DEL CLIENTE", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, VERDE)));
        doc.add(new Paragraph("Cliente: " + cliente, fSub));
        doc.add(new Paragraph(" "));

        PdfPTable tabla = new PdfPTable(4);
        tabla.setWidthPercentage(100);
        tabla.setWidths(new float[]{4, 1.2f, 1.5f, 1.5f});
        agregarEncabezadoTabla(tabla, "DESCRIPCION");
        agregarEncabezadoTabla(tabla, "CANT.");
        agregarEncabezadoTabla(tabla, "PRECIO U.");
        agregarEncabezadoTabla(tabla, "SUBTOTAL");

        Font fCelda = FontFactory.getFont(FontFactory.HELVETICA, 10);
        for (CarritoItem item : carrito) {
            tabla.addCell(new Phrase(item.getProducto().getNombreProducto(), fCelda));

            PdfPCell cCant = new PdfPCell(new Phrase(String.valueOf(item.getCantidad()), fCelda));
            cCant.setHorizontalAlignment(Element.ALIGN_CENTER);
            tabla.addCell(cCant);

            PdfPCell cPrecio = new PdfPCell(new Phrase("Q" + item.getProducto().getPrecio().setScale(2, RoundingMode.HALF_UP), fCelda));
            cPrecio.setHorizontalAlignment(Element.ALIGN_RIGHT);
            tabla.addCell(cPrecio);

            PdfPCell cSub = new PdfPCell(new Phrase("Q" + item.getSubtotal().setScale(2, RoundingMode.HALF_UP), fCelda));
            cSub.setHorizontalAlignment(Element.ALIGN_RIGHT);
            tabla.addCell(cSub);
        }
        doc.add(tabla);
        doc.add(new Paragraph(" "));

        BigDecimal subtotal = total.divide(new BigDecimal("1.12"), 2, RoundingMode.HALF_UP);
        BigDecimal iva = total.subtract(subtotal);

        PdfPTable totales = new PdfPTable(2);
        totales.setWidthPercentage(45);
        totales.setHorizontalAlignment(Element.ALIGN_RIGHT);
        agregarFilaTotal(totales, "Subtotal:", "Q" + subtotal, false);
        agregarFilaTotal(totales, "IVA (12% incl.):", "Q" + iva, false);
        agregarFilaTotal(totales, "TOTAL:", "Q" + total.setScale(2, RoundingMode.HALF_UP), true);
        doc.add(totales);
        doc.add(new Paragraph(" "));

        Paragraph pie = new Paragraph(
                "Documento Tributario Electronico (DTE) generado desde Punto de Venta - Abarroteria Kinal\nGracias por su preferencia",
                FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 8, Color.GRAY));
        pie.setAlignment(Element.ALIGN_CENTER);
        doc.add(pie);

        doc.close();

        try {
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(archivo);
            }
        } catch (Exception ignored) { }

        return archivo;
    }

    private static void agregarCeldaInfo(PdfPTable tabla, String etiqueta, String valor) {
        Phrase p = new Phrase();
        p.add(new Chunk(etiqueta + ": ", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, Color.DARK_GRAY)));
        p.add(new Chunk(valor, FontFactory.getFont(FontFactory.HELVETICA, 9, Color.BLACK)));
        PdfPCell celda = new PdfPCell(p);
        celda.setBorder(Rectangle.NO_BORDER);
        tabla.addCell(celda);
    }

    private static void agregarEncabezadoTabla(PdfPTable tabla, String texto) {
        PdfPCell celda = new PdfPCell(new Phrase(texto, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Color.WHITE)));
        celda.setBackgroundColor(NARANJA);
        celda.setPadding(6);
        celda.setHorizontalAlignment(Element.ALIGN_CENTER);
        tabla.addCell(celda);
    }

    private static void agregarFilaTotal(PdfPTable tabla, String etiqueta, String valor, boolean destacado) {
        Font f = destacado
                ? FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, VERDE)
                : FontFactory.getFont(FontFactory.HELVETICA, 10, Color.BLACK);
        PdfPCell cEtiqueta = new PdfPCell(new Phrase(etiqueta, f));
        cEtiqueta.setBorder(Rectangle.NO_BORDER);
        cEtiqueta.setHorizontalAlignment(Element.ALIGN_RIGHT);
        PdfPCell cValor = new PdfPCell(new Phrase(valor, f));
        cValor.setBorder(Rectangle.NO_BORDER);
        cValor.setHorizontalAlignment(Element.ALIGN_RIGHT);
        tabla.addCell(cEtiqueta);
        tabla.addCell(cValor);
    }
}