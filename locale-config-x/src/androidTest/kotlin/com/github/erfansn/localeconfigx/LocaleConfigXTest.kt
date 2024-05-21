/*
 * Copyright (c) 2024 Erfan Sn
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.github.erfansn.localeconfigx

import android.app.LocaleManager
import android.content.Context
import android.os.LocaleList
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.getSystemService
import androidx.core.os.LocaleListCompat
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.Locale

@RunWith(AndroidJUnit4::class)
class LocaleConfigXTest {

    @get:Rule
    val grantPermissionsRule = GrantPermissionRule.grant(
        "android.permission.CHANGE_CONFIGURATION",
        "android.permission.WRITE_SETTINGS"
    )!!

    @Test
    fun configuredLocalesReturnsAnyLocalDefinedInReferencedXmlFileOfLocaleConfigInManifest() {
        val context = InstrumentationRegistry.getInstrumentation().context

        val configuredLocales = context.configuredLocales

        assertEquals(configuredLocales, LocaleListCompat.forLanguageTags("en,fa"))
    }

    @Test
    fun currentOrDefaultLocaleReturnsTheApplicationLocaleThatSet() {
        ActivityScenario.launch(AppCompatActivity::class.java).use {
            it.onActivity { activity ->
                AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags("fa"))

                val currentLocale = activity.currentOrDefaultLocale

                assertEquals(currentLocale, Locale("fa"))
            }
        }
    }

    @Test
    fun currentOrDefaultLocaleReturnsTheBestMatchedLocaleWithSystemWhenUserDoesNotSetAppLocale() {
        ActivityScenario.launch(AppCompatActivity::class.java).use {
            it.onActivity { activity ->
                AppCompatDelegate.setApplicationLocales(LocaleListCompat.getEmptyLocaleList())

                val currentLocale = activity.withSystemLocales(LocaleList.forLanguageTags("fa-IR")) {
                    activity.currentOrDefaultLocale
                }

                assertEquals(currentLocale, Locale("fa"))
            }
        }
    }

    @Test
    fun currentOrDefaultLocaleReturnsTheFirstLocaleOfConfiguredListWhenThereIsNoMatchWithSystem() {
        ActivityScenario.launch(AppCompatActivity::class.java).use {
            it.onActivity { activity ->
                AppCompatDelegate.setApplicationLocales(LocaleListCompat.getEmptyLocaleList())

                val currentLocale = activity.withSystemLocales(LocaleList.forLanguageTags("fr-CA")) {
                    activity.currentOrDefaultLocale
                }

                assertEquals(currentLocale, Locale("en"))
            }
        }
    }

    // Only works on emulator
    private fun <T> Context.withSystemLocales(localeList: LocaleList, block: () -> T): T {
        val localeManager = getSystemService<LocaleManager>()!!
        val previousSystemLocales = localeManager.systemLocales
        val setSystemLocales =
            localeManager::class.java.getMethod("setSystemLocales", LocaleList::class.java)

        setSystemLocales(localeManager, localeList)
        val result = block()
        setSystemLocales(localeManager, previousSystemLocales)
        return result
    }
}
