package telegram.printer.bot

import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.PDPage
import org.apache.pdfbox.pdmodel.PDPageContentStream
import org.apache.pdfbox.pdmodel.PDPageContentStream.AppendMode.PREPEND
import org.apache.pdfbox.pdmodel.common.PDRectangle
import org.apache.pdfbox.printing.PDFPrintable
import org.apache.pdfbox.printing.Scaling
import org.apache.pdfbox.util.Matrix
import java.awt.print.Book
import java.awt.print.PageFormat
import java.awt.print.Paper


class PdfPrinter : Printer() {
    private val width: Float = 612.0F
    private val height: Float = 792.0F
    private val x: Double = 0.0
    private val y: Double = 0.0

    override fun print(byteArray: ByteArray) {
        val document = PDDocument.load(byteArray)
        print(document)
    }

    fun print(document: PDDocument) {
        document.documentCatalog.pages.forEach { page ->
            if (page.mediaBox.width > width || page.mediaBox.height > height) {
                val factor: Float = getFactor(page)
                val contentStream = PDPageContentStream(document, page, PREPEND, false)
                contentStream.transform(Matrix.getScaleInstance(factor, factor))
                contentStream.close()
                page.mediaBox = PDRectangle.LETTER
            }
        }
        val paper = Paper()
        paper.setSize(width.toDouble(), height.toDouble())
        paper.setImageableArea(x, y, width.toDouble(), height.toDouble())
        val pageFormat = PageFormat()
        pageFormat.paper = paper
        val book = Book()
        book.append(PDFPrintable(document, Scaling.SHRINK_TO_FIT), pageFormat, document.numberOfPages)
        print(book)
    }

    private fun getFactor(page: PDPage): Float {
        val fWidth = width / page.mediaBox.width
        val fHeight = height / page.mediaBox.height
        return if (fWidth > fHeight) {
            fHeight
        } else {
            fWidth
        }
    }
}
