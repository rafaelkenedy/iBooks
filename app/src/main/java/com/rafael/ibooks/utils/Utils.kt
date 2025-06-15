package com.rafael.ibooks.utils

import android.text.Spanned
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle

@Composable
fun spannedToAnnotatedString(spanned: Spanned, baseStyle: TextStyle = MaterialTheme.typography.bodyMedium): AnnotatedString {
    return buildAnnotatedString {
        withStyle(style = baseStyle.toSpanStyle()) {
            append(spanned.toString())
        }

        val spans = spanned.getSpans(0, spanned.length, Any::class.java)
        spans.forEach { span ->
            val start = spanned.getSpanStart(span)
            val end = spanned.getSpanEnd(span)

            when (span) {
                is StyleSpan -> {
                    when (span.style) {
                        android.graphics.Typeface.BOLD -> addStyle(
                            SpanStyle(fontWeight = FontWeight.Bold), start, end
                        )
                        android.graphics.Typeface.ITALIC -> addStyle(
                            SpanStyle(fontStyle = FontStyle.Italic), start, end
                        )
                    }
                }

                is UnderlineSpan -> addStyle(
                    SpanStyle(textDecoration = TextDecoration.Underline), start, end
                )
            }
        }
    }
}

