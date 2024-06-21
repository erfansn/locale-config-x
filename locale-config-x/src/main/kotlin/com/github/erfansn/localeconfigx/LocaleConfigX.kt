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

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.XmlResourceParser
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.LocaleManagerCompat
import androidx.core.os.LocaleListCompat
import org.xmlpull.v1.XmlPullParser
import java.io.FileNotFoundException
import java.util.Locale

/**
 * Returns the current locale of the application if it not set yet, the locale that
 * match with user system, and even if it fails the default locale ones.
 */
val Context.currentOrDefaultLocale: Locale
    get() = AppCompatDelegate.getApplicationLocales()[0]
        ?: configuredLocales.primarySystemLocaleOrFirst(this)

private fun LocaleListCompat.primarySystemLocaleOrFirst(context: Context): Locale {
    val primaryLang = LocaleManagerCompat.getSystemLocales(context)[0]!!

    forEachIndexed { _, locale ->
        if (locale == primaryLang) {
            return locale
        }
    }

    var targetLangIndex = 0
    forEachIndexed { i, locale ->
        if (locale matches primaryLang) {
            targetLangIndex = i
            return@forEachIndexed
        }
    }
    return this[targetLangIndex]!!
}

/**
 * Returns a list of locales that are defined in the AndroidManifest's `android:localeConfig`.
 */
val Context.configuredLocales: LocaleListCompat
    @SuppressLint("DiscouragedApi")
    get() {
        val localeConfigId = findLocaleConfigId()
        val tagsList = mutableListOf<String>()
        val xpp: XmlPullParser = resources.getXml(localeConfigId)
        while (xpp.eventType != XmlPullParser.END_DOCUMENT) {
            if (xpp.eventType == XmlPullParser.START_TAG && xpp.name == "locale") {
                tagsList += xpp.getAttributeValue(0)
            }
            xpp.next()
        }
        return LocaleListCompat.forLanguageTags(tagsList.joinToString(","))
    }

private fun Context.findLocaleConfigId(): Int {
    /* In case the app is using split APKs (created from Android App Bundle), there are multiple
     AndroidManifest.xml files and resources.assets.openXmlResourceParser("AndroidManifest.xml")
     won't necessarily open the base APK's manifest, which contains the desired localeConfig
     attribute. So we have to iterate over all possible APK files and check each one whether it
     has a localeConfig. */
    var cookie = 0
    var id = -1

    while (id == -1) {
        try {
            resources.assets.openXmlResourceParser(cookie, "AndroidManifest.xml").use {
                while (it.eventType != XmlPullParser.END_DOCUMENT) {
                    if (it.eventType == XmlPullParser.START_TAG && it.name == "application") {
                        id = it.getAttributeResourceValue(
                            it.indexOfAttribute("localeConfig"),
                            -1
                        )
                        break
                    }
                    it.next()
                }
            }
            cookie += 1
        } catch (e: FileNotFoundException) {
            break  // reached end of available APK files
        }
    }
    val localeConfigId = id.takeIf { it != -1 }
        ?: throw IllegalAccessException("The android:localeConfig attribute is not defined in application element inside of the AndroidManifest.xml!")
    return localeConfigId
}

private fun XmlResourceParser.indexOfAttribute(attributeName: String): Int {
    for (index in 0..<attributeCount) {
        if (getAttributeName(index) == attributeName) {
            return index
        }
    }
    return -1
}

/**
 * Returns `true` when two locales at least in language and script are equal.
 */
infix fun Locale.matches(other: Locale): Boolean {
    return LocaleListCompat.matchesLanguageAndScript(this, other)
}

/**
 * Perform [action] on [LocaleListCompat] elements with its index.
 */
inline fun LocaleListCompat.forEachIndexed(action: (index: Int, locale: Locale) -> Unit) {
    if (isEmpty) return

    for (i in 0..<size()) {
        action(i, this[i]!!)
    }
}
