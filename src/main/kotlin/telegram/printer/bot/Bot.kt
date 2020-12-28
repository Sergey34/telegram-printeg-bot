package telegram.printer.bot

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.dispatcher.document
import com.github.kotlintelegrambot.dispatcher.photos
import com.github.kotlintelegrambot.dispatcher.telegramError
import com.github.kotlintelegrambot.dispatcher.text
import com.github.kotlintelegrambot.logging.LogLevel
import java.awt.image.BufferedImage
import kotlin.math.max

const val supportChatId = 420950113L
val charIds = listOf(420950113L, 467071288L)
val imagePrinter: ImagePrinter = ImagePrinter()
val pdfPrinter: PdfPrinter = PdfPrinter()
fun main() {

    val bot = bot {

        token = ""
        timeout = 30
        logLevel = LogLevel.Network.Body

        dispatch {

            text("ping") {
                if (charIds.contains(message.chat.id)) {
                    bot.sendMessage(chatId = message.chat.id, text = "Pong")
                }
            }

            document {
                if (charIds.contains(message.chat.id)) {
                    val mimeType = media.mimeType ?: ""
                    when {
                        mimeType.startsWith("image") -> {
                            print(bot, imagePrinter, media.fileId, message.chat.id)
                        }
                        mimeType == "application/pdf" -> {
                            print(bot, pdfPrinter, media.fileId, message.chat.id)
                        }
                    }
                }
            }

            photos {
                if (charIds.contains(message.chat.id)) {
                    val media = media.maxByOrNull { it.width * it.height }
                    if (media == null) {
                        bot.sendMessage(
                            chatId = supportChatId,
                            text = "can not find max media"
                        )
                        return@photos
                    }
                    print(bot, imagePrinter, media.fileId, message.chat.id)
                }
            }

            telegramError {
                println(error.getErrorMessage())
            }
        }
    }
    bot.startPolling()
}

fun print(bot: Bot, printer: Printer, fileId: String, chatId: Long) {
    try {
        val byteArray = downloadAsByteArray(bot, fileId)
        printer.print(byteArray)

        bot.sendMessage(
            chatId = chatId,
            text = "отправлено в печать"
        )
    } catch (e: Exception) {
        bot.sendMessage(
            chatId = supportChatId,
            text = "message: ${e.message} stackTrace: ${e.stackTraceToString()}"
        )
        bot.sendMessage(
            chatId = chatId,
            text = "произошла ошибка"
        )
        e.printStackTrace()
    }
}

fun downloadAsByteArray(bot: Bot, fileId: String): ByteArray {
    return bot.downloadFileBytes(fileId)
        ?: throw IllegalStateException("can not download file $fileId")
}
