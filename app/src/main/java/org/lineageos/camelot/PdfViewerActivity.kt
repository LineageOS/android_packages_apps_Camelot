/*
 * SPDX-FileCopyrightText: 2024 The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.lineageos.camelot

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.util.Consumer
import com.google.android.material.appbar.MaterialToolbar
import org.lineageos.camelot.fragments.CamelotPdfViewerFragment

class PdfViewerActivity : AppCompatActivity(R.layout.activity_main) {
    // Views
    private val toolbar by lazy { findViewById<MaterialToolbar>(R.id.toolbar) }

    // Fragment
    private val pdfViewerFragment by lazy {
        supportFragmentManager.findFragmentById(
            R.id.fragmentContainerView
        ) as CamelotPdfViewerFragment
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

        setSupportActionBar(toolbar)
        toolbar.setTitle(R.string.app_name)

        addOnNewIntentListener(intentListener)
        intentListener.accept(intent)
    }

    override fun onDestroy() {
        removeOnNewIntentListener(intentListener)

        super.onDestroy()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.pdf_viewer_menu, menu)
        return true
    }
}
