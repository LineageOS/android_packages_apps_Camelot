/*
 * SPDX-FileCopyrightText: 2024 The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.lineageos.camelot.fragments

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.RequiresExtension
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.activityViewModels
import androidx.pdf.viewer.fragment.PdfViewerFragment
import androidx.pdf.viewer.fragment.pdfName
import org.lineageos.camelot.R
import org.lineageos.camelot.ext.updateMargin
import org.lineageos.camelot.viewmodels.PdfViewModel

@RequiresExtension(extension = Build.VERSION_CODES.S, version = 13)
class CamelotPdfViewerFragment : PdfViewerFragment() {
    private val pdfViewModel by activityViewModels<PdfViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = super.onCreateView(inflater, container, savedInstanceState) as FrameLayout
        val pdfView = inflater.inflate(R.layout.fragment_pdf, container, false) as FrameLayout

        pdfView.addView(view)

        return pdfView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sequenceOf<View>(
            view.findViewById(androidx.pdf.R.id.pdf_page_num),
            view.findViewById(androidx.pdf.R.id.edit_fab),
        ).forEach {
            ViewCompat.setOnApplyWindowInsetsListener(it) { v, windowInsets ->
                val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())

                v.updateMargin(
                    insets,
                    top = true,
                    bottom = true,
                )

                windowInsets
            }
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
