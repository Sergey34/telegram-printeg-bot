package telegram.printer.bot

import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.dispatcher.handlers.media.MediaHandlerEnvironment
import com.github.kotlintelegrambot.dispatcher.photos
import com.github.kotlintelegrambot.dispatcher.telegramError
import com.github.kotlintelegrambot.dispatcher.text
import com.github.kotlintelegrambot.entities.files.PhotoSize
import com.github.kotlintelegrambot.logging.LogLevel
import java.awt.image.BufferedImage
import java.awt.print.Printable
import java.awt.print.Printable.NO_SUCH_PAGE
import java.awt.print.Printable.PAGE_EXISTS
import java.awt.print.PrinterJob
import java.io.ByteArrayInputStream
import javax.imageio.ImageIO
import kotlin.math.ceil


fun main(args: Array<String>) {
    val supportChatId = 0L
    val charIds = listOf(0L)
    val bot = bot {

        token = ""
        timeout = 30
        logLevel = LogLevel.Network.Body

        dispatch {

            text("ping") {
                if (charIds.contains(message.chat.id)){
                    bot.sendMessage(chatId = message.chat.id, text = "Pong")
                }
            }

            photos {
                if (charIds.contains(message.chat.id)){
                    try {
                        val media = this.media.maxByOrNull { it.width * it.height }
                            ?: throw IllegalStateException("can not find max media")
                        val bufferedImage = downloadAsImage(media)
                        printImage(bufferedImage)
                        bot.sendMessage(
                            chatId = message.chat.id,
                            text = "отправлено в печать"
                        )
                    } catch (e: Exception) {
                        bot.sendMessage(
                            chatId = supportChatId,
                            text = "message: ${e.message} stackTrace: ${e.stackTraceToString()}"
                        )
                        bot.sendMessage(
                            chatId = message.chat.id,
                            text = "произошла ошибка"
                        )
                        e.printStackTrace()
                    }
                }
            }

            telegramError {
                println(error.getErrorMessage())
            }
        }
    }
    bot.startPolling()
}

private fun MediaHandlerEnvironment<List<PhotoSize>>.downloadAsImage(
    photoSize: PhotoSize
): BufferedImage {
    val downloadFileBytes = bot.downloadFileBytes(photoSize.fileId)
        ?: throw IllegalStateException("can not download file ${photoSize.fileId}")
    return ImageIO.read(ByteArrayInputStream(downloadFileBytes))
}

private fun printImage(image: BufferedImage) {
    val printJob = PrinterJob.getPrinterJob()
    printJob.setPrintable(Printable { graphics, pageFormat, pageIndex -> // Get the upper left corner that it printable
        val x = ceil(pageFormat.imageableX).toInt()
        val y = ceil(pageFormat.imageableY).toInt()
        if (pageIndex != 0) {
            return@Printable NO_SUCH_PAGE
        }
        graphics.drawImage(image, x, y, 420,594, null)
        PAGE_EXISTS
    })
    printJob.print()
}
