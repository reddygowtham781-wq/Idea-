package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = BentoPrimaryGreen,
    onPrimary = BentoWhite,
    secondary = BentoSecondaryLightGreen,
    onSecondary = BentoDarkGreenText,
    tertiary = BentoAlabasterGreen,
    background = BentoBackground,
    onBackground = BentoTextDark,
    surface = BentoWhite,
    onSurface = BentoTextDark,
    surfaceVariant = BentoAlabasterGreen,
    onSurfaceVariant = BentoSubduedText,
    outline = BentoBorder,
    error = BentoErrorBrand,
    errorContainer = BentoErrorLightBg,
    onErrorContainer = BentoErrorTextDark
)

// Consistent warm/light bento aesthetic. We can keep it uniform for high identity.
private val DarkColorScheme = darkColorScheme(
    primary = BentoSecondaryLightGreen,
    onPrimary = BentoDarkGreenText,
    secondary = BentoPrimaryGreen,
    onSecondary = BentoWhite,
    tertiary = BentoAlabasterGreen,
    background = BentoTextDark,
    onBackground = BentoBackground,
    surface = Color(0xFF222622),
    onSurface = BentoBackground,
    surfaceVariant = Color(0xFF2B302A),
    onSurfaceVariant = BentoBorder,
    outline = BentoSubduedText,
    error = BentoErrorLightBg,
    errorContainer = BentoErrorTextDark,
    onErrorContainer = BentoErrorLightBg
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Disable to force Bento design scheme
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
