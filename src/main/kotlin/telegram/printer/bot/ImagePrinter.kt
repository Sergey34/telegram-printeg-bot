package telegram.printer.bot

import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.PDPage
import org.apache.pdfbox.pdmodel.PDPageContentStream
import org.apache.pdfbox.pdmodel.common.PDRectangle
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject
import java.io.ByteArrayInputStream
import javax.imageio.ImageIO

class ImagePrinter : Printer() {
    override fun print(byteArray: ByteArray) {
        val pdf = getPdf(byteArray)
        PdfPrinter().print(pdf)
    }

    private fun getPdf(byteArray: ByteArray): PDDocument {
        val bufferedImage = ImageIO.read(ByteArrayInputStream(byteArray))
        val document = PDDocument()

        val width = bufferedImage.width.toFloat()
        val height = bufferedImage.height.toFloat()
        val page = PDPage(PDRectangle(width, height))
        document.addPage(page)
        val img = PDImageXObject.createFromByteArray(document, byteArray, "img")
        val contentStream = PDPageContentStream(document, page)
        contentStream.drawImage(img, 0f, 0f)
        contentStream.close()

        return document
    }
}
