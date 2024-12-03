/*
 * SPDX-FileCopyrightText: 2024 The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.lineageos.camelot.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class PdfViewModel(application: Application) : AndroidViewModel(application) {
    private val _pdfName = MutableStateFlow<String?>(null)
    val pdfName = _pdfName.asStateFlow()

    fun setPdfName(pdfName: String?) {
        _pdfName.value = pdfName
    }
}
