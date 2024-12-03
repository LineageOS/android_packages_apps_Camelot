/*
 * SPDX-FileCopyrightText: 2024 The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.lineageos.camelot

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.print.PrintAttributes
import android.print.PrintManager
import android.view.Menu
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresExtension
import androidx.appcompat.app.AppCompatActivity
import androidx.core.util.Consumer
import com.google.android.material.appbar.MaterialToolbar
import org.lineageos.camelot.fragments.CamelotPdfViewerFragment
import org.lineageos.camelot.print.CamelotPdfDocumentAdapter
import kotlin.reflect.cast

@RequiresExtension(extension = Build.VERSION_CODES.S, version = 13)
class PdfViewerActivity : AppCompatActivity(R.layout.activity_main) {
    // Views
    private val toolbar by lazy { findViewById<MaterialToolbar>(R.id.toolbar) }

    // Fragment
    private val pdfViewerFragment by lazy {
        CamelotPdfViewerFragment::class.cast(
            supportFragmentManager.findFragmentById(
                R.id.fragmentContainerView
            )
        )
    }

    // Document URI
    private var pdfUri: Uri?
        get() = pdfViewerFragment.documentUri
        set(value) {
            pdfViewerFragment.documentUri = value
        }

    // Intents
    private val intentListener = Consumer<Intent> { intent ->
        intent.data?.let {
            pdfUri = it
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setSupportActionBar(toolbar)

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
                        setDataAndType(pdfUri, MIME_TYPE_PDF)
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
                        setDataAndType(pdfUri, MIME_TYPE_PDF)
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
            pdfUri?.let {
                contentResolver.openFileDescriptor(it, "r")?.use { fileDescriptor ->
                    val printManager = getSystemService(PrintManager::class.java)

                    val printDocumentAdapter = CamelotPdfDocumentAdapter(fileDescriptor)
                    printManager.print(
                        "PDF Document",
                        printDocumentAdapter,
                        PrintAttributes.Builder().build()
                    )
                }
            }
            true
        }

        else -> super.onOptionsItemSelected(item)
    }

    companion object {
        private const val MIME_TYPE_PDF = "application/pdf"
    }
}
