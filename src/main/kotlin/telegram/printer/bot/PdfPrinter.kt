package telegram.printer.bot

import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.printing.PDFPageable

class PdfPrinter: Printer() {
    override fun print(byteArray: ByteArray) {
        val document = PDDocument.load(byteArray)
        print(PDFPageable(document))
    }
}
