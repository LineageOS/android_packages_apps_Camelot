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
import android.view.View.MeasureSpec
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresExtension
import androidx.appcompat.app.AppCompatActivity
import androidx.core.util.Consumer
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.appbar.MaterialToolbar
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.lineageos.camelot.fragments.CamelotPdfViewerFragment
import org.lineageos.camelot.print.CamelotPdfDocumentAdapter
import org.lineageos.camelot.viewmodels.PdfViewModel
import kotlin.reflect.cast

@RequiresExtension(extension = Build.VERSION_CODES.S, version = 13)
class PdfViewerActivity : AppCompatActivity(R.layout.activity_main) {
    // View models
    private val pdfViewModel by viewModels<PdfViewModel>()

    // Views
    private val toolbar by lazy { findViewById<MaterialToolbar>(R.id.toolbar) }

    // Insets
    private val windowInsetsController by lazy {
        WindowInsetsControllerCompat(window, window.decorView)
    }

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

        toolbar.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED)
        pdfViewModel.setToolbarHeight(toolbar.measuredHeight)

        windowInsetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        addOnNewIntentListener(intentListener)
        intentListener.accept(intent)

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                loadData()
            }
        }
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
        android.R.id.home -> {
            onBackPressedDispatcher.onBackPressed()
            true
        }

        R.id.action_find_in_page -> {
            pdfViewerFragment.isTextSearchActive = true
            true
        }

        R.id.action_send -> {
            startActivity(
                Intent.createChooser(
                    Intent(Intent.ACTION_SEND).apply {
                        flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                        putExtra(Intent.EXTRA_STREAM, pdfUri)
                        type = MIME_TYPE_PDF
                    },
                    getString(R.string.share)
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
                    putExtra(
                        Intent.EXTRA_TITLE,
                        pdfViewModel.pdfName.value ?: getString(R.string.pdf_document)
                    )
                }
            )
            true
        }

        R.id.action_print -> {
            pdfUri?.let {
                val printManager = getSystemService(PrintManager::class.java)
                val printDocumentAdapter = CamelotPdfDocumentAdapter(
                    contentResolver,
                    it,
                    pdfViewModel.pdfName.value ?: getString(R.string.pdf_document)
                )
                printManager.print(
                    printDocumentAdapter.name,
                    printDocumentAdapter,
                    PrintAttributes.Builder().build()
                )
            }
            true
        }

        else -> super.onOptionsItemSelected(item)
    }

    private suspend fun loadData() {
        coroutineScope {
            launch {
                pdfViewModel.pdfName.collectLatest {
                    it?.also { pdfName ->
                        title = pdfName
                    } ?: setTitle(R.string.app_name)
                }
            }

            launch {
                pdfViewModel.immersiveMode.collectLatest {
                    val systemBars = WindowInsetsCompat.Type.systemBars()
                    when (it) {
                        true -> windowInsetsController.hide(systemBars)
                        false -> windowInsetsController.show(systemBars)
                    }

                    supportActionBar?.apply {
                        when (it) {
                            true -> hide()
                            false -> show()
                        }
                    }
                }
            }
        }
    }

    companion object {
        private const val MIME_TYPE_PDF = "application/pdf"
    }
}
