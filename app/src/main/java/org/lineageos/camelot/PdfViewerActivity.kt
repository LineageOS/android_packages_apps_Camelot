/*
 * SPDX-FileCopyrightText: 2024 The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.lineageos.camelot

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresExtension
import androidx.appcompat.app.AppCompatActivity
import androidx.core.util.Consumer
import com.google.android.material.appbar.MaterialToolbar
import org.lineageos.camelot.fragments.CamelotPdfViewerFragment

@RequiresExtension(extension = Build.VERSION_CODES.S, version = 13)
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
        intent.data?.let { uri ->
            pdfViewerFragment.documentUri = uri
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setSupportActionBar(toolbar)
        setTitle(R.string.app_name)

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

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_find_in_page -> {
            pdfViewerFragment.isTextSearchActive = true
            true
        }

        R.id.action_send -> {
            startActivity(
                Intent.createChooser(
                    Intent(Intent.ACTION_SEND).apply {
                        flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                        putExtra(Intent.EXTRA_STREAM, pdfViewerFragment.documentUri)
                        type = MIME_TYPE_PDF
                    },
                    getString(R.string.send)
                )
            )
            true
        }

        R.id.action_open_with -> {
            startActivity(
                Intent.createChooser(
                    Intent(Intent.ACTION_VIEW).apply {
                        flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                        setDataAndType(pdfViewerFragment.documentUri, MIME_TYPE_PDF)
                    },
                    getString(R.string.open_with)
                )
            )
            true
        }

        R.id.action_download -> {
            startActivity(
                Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
                    addCategory(Intent.CATEGORY_OPENABLE)
                    type = MIME_TYPE_PDF
                }
            )
            true
        }

        R.id.action_print -> {
            // TODO: Implement printing
            true
        }

        else -> super.onOptionsItemSelected(item)
    }

    companion object {
        private const val MIME_TYPE_PDF = "application/pdf"
    }
}
