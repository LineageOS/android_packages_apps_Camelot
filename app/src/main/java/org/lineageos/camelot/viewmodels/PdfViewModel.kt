/*
 * SPDX-FileCopyrightText: 2024-2025 The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.lineageos.camelot.viewmodels

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

class PdfViewModel(application: Application) : AndroidViewModel(application) {
    private val _pdfName = MutableStateFlow<String?>(null)
    val pdfName = _pdfName.asStateFlow()

    private val _immersiveMode = MutableStateFlow(false)
    val immersiveMode = _immersiveMode.asStateFlow()

    private val _toolbarHeight = MutableStateFlow(0)
    val toolbarHeight = _toolbarHeight.asStateFlow()

    fun setPdfName(pdfName: String?) {
        _pdfName.value = pdfName
    }

    fun setImmersiveMode(immersiveMode: Boolean) {
        _immersiveMode.value = immersiveMode
    }

    fun setToolbarHeight(toolbarHeight: Int) {
        _toolbarHeight.value = toolbarHeight
    }

    suspend fun copyPdf(sourceUri: Uri?, destinationUri: Uri?) = withContext(Dispatchers.IO) {
        val contentResolver = getApplication<Application>().contentResolver

        val source = sourceUri ?: return@withContext false
        val destination = destinationUri ?: return@withContext false

        runCatching {
            contentResolver.openInputStream(source)?.use { input ->
                contentResolver.openOutputStream(destination)?.use { output ->
                    input.copyTo(output)
                    true
                }
            }
        }.getOrNull() ?: false
    }
}
