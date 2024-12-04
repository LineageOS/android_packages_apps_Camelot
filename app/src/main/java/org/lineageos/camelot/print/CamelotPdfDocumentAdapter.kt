/*
 * SPDX-FileCopyrightText: 2024 The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.lineageos.camelot.print

import android.content.ContentResolver
import android.net.Uri
import android.os.ParcelFileDescriptor
import android.print.PrintAttributes
import android.print.PrintDocumentAdapter
import android.print.PrintDocumentInfo

class CamelotPdfDocumentAdapter(
    private val contentResolver: ContentResolver,
    private val uri: Uri,
    val name: String
) : PrintDocumentAdapter() {
    override fun onLayout(
        oldAttributes: PrintAttributes?,
        newAttributes: PrintAttributes?,
        cancellationSignal: android.os.CancellationSignal?,
        callback: LayoutResultCallback?,
        extras: android.os.Bundle?
    ) {
        if (cancellationSignal?.isCanceled == true) {
            callback?.onLayoutCancelled()
            return
        }

        val info = PrintDocumentInfo.Builder(name)
            .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
            .setPageCount(PrintDocumentInfo.PAGE_COUNT_UNKNOWN)
            .build()

        callback?.onLayoutFinished(info, true)
    }

    override fun onWrite(
        pages: Array<out android.print.PageRange>?,
        destination: ParcelFileDescriptor?,
        cancellationSignal: android.os.CancellationSignal?,
        callback: WriteResultCallback?
    ) {
        ParcelFileDescriptor.AutoCloseInputStream(
            contentResolver.openFileDescriptor(uri, "r")
        ).use { input ->
            ParcelFileDescriptor.AutoCloseOutputStream(destination).use { output ->
                input.copyTo(output)
            }

            callback?.onWriteFinished(arrayOf(android.print.PageRange.ALL_PAGES))
        }
    }
}
