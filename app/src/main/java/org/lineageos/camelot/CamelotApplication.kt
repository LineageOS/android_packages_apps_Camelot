/*
 * SPDX-FileCopyrightText: 2024 The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.lineageos.camelot

import android.app.Application
import com.google.android.material.color.DynamicColors

class CamelotApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // Observe dynamic colors changes
        DynamicColors.applyToActivitiesIfAvailable(this)
    }
}
