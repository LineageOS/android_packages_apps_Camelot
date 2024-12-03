/*
 * SPDX-FileCopyrightText: 2024 The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package androidx.pdf.viewer.fragment

import androidx.pdf.data.DisplayData

@Suppress("RestrictedApi")
private val PdfViewerFragment.fileData: DisplayData?
    get() = PdfViewerFragment::class.java.getDeclaredField("fileData").apply {
        isAccessible = true
    }.get(this) as? DisplayData

@Suppress("RestrictedApi")
val PdfViewerFragment.pdfName: String?
    get() = fileData?.name
