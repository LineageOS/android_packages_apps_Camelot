/*
 * SPDX-FileCopyrightText: 2024 The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.lineageos.camelot.fragments

import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresExtension
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.marginBottom
import androidx.core.view.marginLeft
import androidx.core.view.marginRight
import androidx.core.view.marginTop
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.activityViewModels
import androidx.pdf.viewer.fragment.PdfViewerFragment
import androidx.pdf.viewer.fragment.pdfName
import androidx.pdf.widget.FastScrollView
import androidx.pdf.widget.pageIndicatorExt
import androidx.pdf.widget.zoomView
import androidx.pdf.widget.zoomViewBasePadding
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.lineageos.camelot.viewmodels.PdfViewModel

@RequiresExtension(extension = Build.VERSION_CODES.S, version = 13)
class CamelotPdfViewerFragment : PdfViewerFragment() {
    private val pdfViewModel by activityViewModels<PdfViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ViewCompat.setOnApplyWindowInsetsListener(
            view.findViewById(androidx.pdf.R.id.fast_scroll_view)
        ) { v, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())

            if (!pdfViewModel.immersiveMode.value) {
                @Suppress("RestrictedApi")
                (v as FastScrollView).apply {
                    /**
                     * @see FastScrollView.onApplyWindowInsets
                     */
                    zoomView?.also { zoomView ->
                        zoomViewBasePadding.let {
                            zoomView.setPadding(
                                0,
                                it.top + insets.top + pdfViewModel.toolbarHeight.value,
                                0,
                                it.bottom + insets.bottom,
                            )
                        }

                        setScrollbarMarginTop(zoomView.paddingTop)
                        setScrollbarMarginRight(insets.right)
                        setScrollbarMarginBottom(zoomView.paddingBottom)
                    }

                    pageIndicatorExt.view.translationX = (-insets.right).toFloat()

                    forceLayout()
                }
            }

            windowInsets
        }

        view.findViewById<FloatingActionButton>(androidx.pdf.R.id.edit_fab).let {
            val originalMargins = Rect(
                it.marginLeft,
                it.marginTop,
                it.marginRight,
                it.marginBottom,
            )

            ViewCompat.setOnApplyWindowInsetsListener(it) { v, windowInsets ->
                val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())

                if (!pdfViewModel.immersiveMode.value) {
                    v.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                        bottomMargin = originalMargins.bottom + insets.bottom
                    }
                }

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
