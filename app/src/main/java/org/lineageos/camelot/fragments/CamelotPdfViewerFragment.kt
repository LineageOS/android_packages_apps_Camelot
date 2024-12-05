/*
 * SPDX-FileCopyrightText: 2024 The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.lineageos.camelot.fragments

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresExtension
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.activityViewModels
import androidx.pdf.viewer.fragment.PdfViewerFragment
import androidx.pdf.viewer.fragment.pdfName
import org.lineageos.camelot.ext.updateMargin
import org.lineageos.camelot.ext.updatePadding
import org.lineageos.camelot.viewmodels.PdfViewModel

@RequiresExtension(extension = Build.VERSION_CODES.S, version = 13)
class CamelotPdfViewerFragment : PdfViewerFragment() {
    private val pdfViewModel by activityViewModels<PdfViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ViewCompat.setOnApplyWindowInsetsListener(
            view.findViewById(androidx.pdf.R.id.pdf_view)
        ) { v, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.statusBars())

            if (!pdfViewModel.immersiveMode.value) {
                v.updateMargin(
                    insets,
                    top = true,
                )
            }

            windowInsets
        }

        ViewCompat.setOnApplyWindowInsetsListener(
            view.findViewById(androidx.pdf.R.id.edit_fab)
        ) { v, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())

            v.updatePadding(
                insets,
                bottom = true,
            )

            windowInsets
        }
    }

    override fun onLoadDocumentSuccess() {
        super.onLoadDocumentSuccess()

        pdfViewModel.setPdfName(pdfName)
    }

    override fun onLoadDocumentError(error: Throwable) {
        super.onLoadDocumentError(error)

        pdfViewModel.setPdfName(null)
    }

    override fun onRequestImmersiveMode(enterImmersive: Boolean) {
        super.onRequestImmersiveMode(enterImmersive)

        pdfViewModel.setImmersiveMode(enterImmersive)
    }
}
