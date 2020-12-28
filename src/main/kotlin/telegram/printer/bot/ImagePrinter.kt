package telegram.printer.bot

import java.awt.image.BufferedImage
import java.awt.print.Printable
import java.io.ByteArrayInputStream
import javax.imageio.ImageIO
import kotlin.math.ceil
import kotlin.math.max

class ImagePrinter : Printer() {
    companion object {
        // https://www.papersizes.org/a-sizes-in-pixels.htm
        const val width = 595
        const val height = 842
    }

    override fun print(byteArray: ByteArray) {
        val bufferedImage = ImageIO.read(ByteArrayInputStream(byteArray))
        val printable = Printable { graphics, pageFormat, pageIndex -> // Get the upper left corner that it printable
            val x = ceil(pageFormat.imageableX).toInt()
            val y = ceil(pageFormat.imageableY).toInt()
            if (pageIndex != 0) {
                return@Printable Printable.NO_SUCH_PAGE
            }
            val (width, height) = calculateSize(bufferedImage)
            graphics.drawImage(bufferedImage, x, y, width, height, null)
            Printable.PAGE_EXISTS
        }
        print(printable)
    }

    private fun calculateSize(image: BufferedImage): Pair<Int, Int> {
        return if (image.width <= width && image.height <= height) {
            image.width to image.height
        } else {
            val max = max(image.width - width, image.height - height)
            image.width - max to image.height - max
        }
    }
}
