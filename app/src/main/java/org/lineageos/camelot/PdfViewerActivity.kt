/*
 * SPDX-FileCopyrightText: 2024 The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.lineageos.camelot

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.util.Consumer
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.pdf.viewer.fragment.PdfViewerFragment

class PdfViewerActivity : AppCompatActivity(R.layout.activity_main) {
    // Fragment
    @SuppressLint("NewApi")
    private val pdfViewerFragment = PdfViewerFragment().apply {
        isTextSearchActive = true
    }

    // Intents
    private val intentListener = Consumer<Intent> { intent ->
        intent.data?.let {
            @SuppressLint("NewApi")
            pdfViewerFragment.documentUri = it
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, pdfViewerFragment)
            .commit()

        addOnNewIntentListener(intentListener)
        intentListener.accept(intent)
    }

    override fun onDestroy() {
        removeOnNewIntentListener(intentListener)

        super.onDestroy()
    }
}
