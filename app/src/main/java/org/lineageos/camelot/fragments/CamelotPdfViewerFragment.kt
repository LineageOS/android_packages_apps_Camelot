/*
 * SPDX-FileCopyrightText: 2024 The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.lineageos.camelot.fragments

import android.os.Build
import androidx.annotation.RequiresExtension
import androidx.fragment.app.activityViewModels
import androidx.pdf.viewer.fragment.PdfViewerFragment
import androidx.pdf.viewer.fragment.pdfName
import org.lineageos.camelot.viewmodels.PdfViewModel

@RequiresExtension(extension = Build.VERSION_CODES.S, version = 13)
class CamelotPdfViewerFragment : PdfViewerFragment() {
    private val pdfViewModel by activityViewModels<PdfViewModel>()

    override fun onLoadDocumentSuccess() {
        super.onLoadDocumentSuccess()

        pdfViewModel.setPdfName(pdfName)
    }

    override fun onLoadDocumentError(error: Throwable) {
        super.onLoadDocumentError(error)

        pdfViewModel.setPdfName(null)
    }
}
