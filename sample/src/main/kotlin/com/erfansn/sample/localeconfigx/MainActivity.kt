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

package com.erfansn.sample.localeconfigx

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.LocaleManagerCompat
import androidx.core.os.LocaleListCompat
import com.erfansn.sample.localeconfigx.ui.theme.LocaleConfigXTheme
import com.github.erfansn.localeconfigx.configuredLocales
import com.github.erfansn.localeconfigx.currentOrDefaultLocale
import com.github.erfansn.localeconfigx.forEachIndexed
import com.github.erfansn.localeconfigx.matches
import java.util.Locale

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            LocaleConfigXTheme {
                MainScreen()
            }
        }
    }
}

@Composable
private fun MainScreen() {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
    ) { contentPadding ->
        SelectableLocaleList(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .consumeWindowInsets(contentPadding),
        )
    }
}

@Composable
fun SelectableLocaleList(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = stringResource(R.string.welcome),
            style = MaterialTheme.typography.displaySmall
        )
        Spacer(modifier = Modifier.height(32.dp))

        val currentLocale = currentOrDefaultLocale()
        var currentOrDefaultLocale by remember(currentLocale) {
            mutableStateOf(currentLocale)
        }
        configuredLocales().forEachIndexed { _, locale ->
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                RadioButton(
                    selected = locale matches currentOrDefaultLocale,
                    onClick = {
                        currentOrDefaultLocale = locale

                        val desiredLocale = LocaleListCompat.create(locale)
                        AppCompatDelegate.setApplicationLocales(desiredLocale)
                    }
                )
                Text(
                    text = displayName(locale),
                )
            }
        }
    }
}

@Composable
private fun configuredLocales(): LocaleListCompat =
    if (LocalInspectionMode.current) {
        LocaleListCompat.create(Locale.US, Locale("fa"))
    } else {
        LocalContext.current.configuredLocales
    }

@Composable
private fun currentOrDefaultLocale(): Locale =
    if (LocalInspectionMode.current) {
        configuredLocales().let {
            it.getFirstMatch(arrayOf(LocalConfiguration.current.locale.toLanguageTag())) ?: it[0]!!
        }
    } else {
        LocalContext.current.currentOrDefaultLocale
    }

@Composable
private fun displayName(locale: Locale): String =
    if (LocalInspectionMode.current) {
        locale.getDisplayName(currentOrDefaultLocale())
    } else {
        locale.displayName
    }

@Preview(showBackground = true, name = "System Default")
@Preview(showBackground = true, locale = "fa-ir", name = "System Persian")
@Preview(showBackground = true, locale = "fr-CA", name = "System French")
@Composable
fun MainScreenPreview() {
    LocaleConfigXTheme {
        MainScreen()
    }
}

