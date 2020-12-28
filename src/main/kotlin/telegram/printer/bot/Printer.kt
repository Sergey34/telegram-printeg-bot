package telegram.printer.bot

import java.awt.print.Pageable
import java.awt.print.Printable
import java.awt.print.PrinterJob

abstract class Printer(
    val printJob: PrinterJob = PrinterJob.getPrinterJob()
) {
    fun print(printable: Printable) {
        val printJob = PrinterJob.getPrinterJob()
        printJob.setPrintable(printable)
        printJob.print()
    }
    fun print(pageable: Pageable) {
        val printJob = PrinterJob.getPrinterJob()
        printJob.setPageable(pageable)
        printJob.print()
    }

    abstract fun print(byteArray: ByteArray)

}
