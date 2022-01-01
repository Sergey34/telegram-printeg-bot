package telegram.printer.bot

import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.PDPage
import org.apache.pdfbox.pdmodel.PDPageContentStream
import org.apache.pdfbox.pdmodel.PDPageTree
import org.apache.pdfbox.pdmodel.common.PDRectangle
import org.apache.pdfbox.printing.PDFPrintable
import org.apache.pdfbox.printing.Scaling
import org.apache.pdfbox.util.Matrix
import java.awt.print.Book
import java.awt.print.PageFormat
import java.awt.print.Paper
import java.awt.print.PrinterJob


class PdfPrinter : Printer() {
    override fun print(byteArray: ByteArray) {
        val document = PDDocument.load(byteArray)
        val job: PrinterJob = PrinterJob.getPrinterJob()
        val tree: PDPageTree = document.documentCatalog.pages
        val iterator: Iterator<PDPage> = tree.iterator()
        while (iterator.hasNext()) {
            val page = iterator.next()
            if (page.mediaBox.width > 612 || page.mediaBox.height > 792) {
                val fWidth = 612f / page.mediaBox.width
                val fHeight = 792f / page.mediaBox.height
                var factor = 0f
                factor = if (fWidth > fHeight) {
                    fHeight
                } else {
                    fWidth
                }
                val contentStream = PDPageContentStream(
                    document, page,
                    PDPageContentStream.AppendMode.PREPEND, false
                )
                contentStream.transform(Matrix.getScaleInstance(factor, factor))
                contentStream.close()
                page.mediaBox = PDRectangle.LETTER
            }
        }
        val paper = Paper()
        paper.setSize(612.0, 792.0)
        paper.setImageableArea(0.0, 0.0, 612.0, 792.0)
        val pageFormat = PageFormat()
        pageFormat.paper = paper
        val book = Book()
        book.append(PDFPrintable(document, Scaling.SHRINK_TO_FIT), pageFormat, document.numberOfPages)
        print(book)
    }
}
