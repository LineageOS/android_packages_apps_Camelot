/*
 * SPDX-FileCopyrightText: 2024 The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package androidx.pdf.widget

import android.graphics.Rect

@Suppress("RestrictedApi")
val FastScrollView.pageIndicatorExt: PageIndicator
    get() = this::class.java.getDeclaredField("mPageIndicator").apply {
        isAccessible = true
    }.get(this) as PageIndicator

@Suppress("RestrictedApi")
val FastScrollView.zoomView: ZoomView?
    get() = this::class.java.getDeclaredField("mZoomView").apply {
        isAccessible = true
    }.get(this) as? ZoomView

@Suppress("RestrictedApi")
val FastScrollView.zoomViewBasePadding: Rect
    get() = this::class.java.getDeclaredField("mZoomViewBasePadding").apply {
        isAccessible = true
    }.get(this) as Rect
