package cn.yurn.yutori.app.ui.theme

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import cafe.adriel.voyager.core.annotation.ExperimentalVoyagerApi
import cafe.adriel.voyager.jetpack.navigatorViewModel
import cn.yurn.yutori.app.MainViewModel

val lightScheme = lightColorScheme(
    primary = primaryLight,
    onPrimary = onPrimaryLight,
    primaryContainer = primaryContainerLight,
    onPrimaryContainer = onPrimaryContainerLight,
    secondary = secondaryLight,
    onSecondary = onSecondaryLight,
    secondaryContainer = secondaryContainerLight,
    onSecondaryContainer = onSecondaryContainerLight,
    tertiary = tertiaryLight,
    onTertiary = onTertiaryLight,
    tertiaryContainer = tertiaryContainerLight,
    onTertiaryContainer = onTertiaryContainerLight,
    error = errorLight,
    onError = onErrorLight,
    errorContainer = errorContainerLight,
    onErrorContainer = onErrorContainerLight,
    background = backgroundLight,
    onBackground = onBackgroundLight,
    surface = surfaceLight,
    onSurface = onSurfaceLight,
    surfaceVariant = surfaceVariantLight,
    onSurfaceVariant = onSurfaceVariantLight,
    outline = outlineLight,
    outlineVariant = outlineVariantLight,
    scrim = scrimLight,
    inverseSurface = inverseSurfaceLight,
    inverseOnSurface = inverseOnSurfaceLight,
    inversePrimary = inversePrimaryLight,
    surfaceDim = surfaceDimLight,
    surfaceBright = surfaceBrightLight,
    surfaceContainerLowest = surfaceContainerLowestLight,
    surfaceContainerLow = surfaceContainerLowLight,
    surfaceContainer = surfaceContainerLight,
    surfaceContainerHigh = surfaceContainerHighLight,
    surfaceContainerHighest = surfaceContainerHighestLight,
)

val darkScheme = darkColorScheme(
    primary = primaryDark,
    onPrimary = onPrimaryDark,
    primaryContainer = primaryContainerDark,
    onPrimaryContainer = onPrimaryContainerDark,
    secondary = secondaryDark,
    onSecondary = onSecondaryDark,
    secondaryContainer = secondaryContainerDark,
    onSecondaryContainer = onSecondaryContainerDark,
    tertiary = tertiaryDark,
    onTertiary = onTertiaryDark,
    tertiaryContainer = tertiaryContainerDark,
    onTertiaryContainer = onTertiaryContainerDark,
    error = errorDark,
    onError = onErrorDark,
    errorContainer = errorContainerDark,
    onErrorContainer = onErrorContainerDark,
    background = backgroundDark,
    onBackground = onBackgroundDark,
    surface = surfaceDark,
    onSurface = onSurfaceDark,
    surfaceVariant = surfaceVariantDark,
    onSurfaceVariant = onSurfaceVariantDark,
    outline = outlineDark,
    outlineVariant = outlineVariantDark,
    scrim = scrimDark,
    inverseSurface = inverseSurfaceDark,
    inverseOnSurface = inverseOnSurfaceDark,
    inversePrimary = inversePrimaryDark,
    surfaceDim = surfaceDimDark,
    surfaceBright = surfaceBrightDark,
    surfaceContainerLowest = surfaceContainerLowestDark,
    surfaceContainerLow = surfaceContainerLowDark,
    surfaceContainer = surfaceContainerDark,
    surfaceContainerHigh = surfaceContainerHighDark,
    surfaceContainerHighest = surfaceContainerHighestDark,
)

@OptIn(ExperimentalVoyagerApi::class)
@Composable
fun YutoriAPPTheme(content: @Composable () -> Unit) {
    val viewModel = navigatorViewModel { MainViewModel() }
    var init by remember { mutableStateOf(false) }
    if (!init) {
        init = true
        viewModel.darkMode = isSystemInDarkTheme()
    }

    val platformColorScheme = platformColorScheme(viewModel)
    val animationSpec = remember { tween<Color>(600) }

    val colorScheme = ColorScheme(
        animateColorAsState(platformColorScheme.primary, animationSpec).value,
        animateColorAsState(platformColorScheme.onPrimary, animationSpec).value,
        animateColorAsState(platformColorScheme.primaryContainer, animationSpec).value,
        animateColorAsState(platformColorScheme.onPrimaryContainer, animationSpec).value,
        animateColorAsState(platformColorScheme.inversePrimary, animationSpec).value,
        animateColorAsState(platformColorScheme.secondary, animationSpec).value,
        animateColorAsState(platformColorScheme.onSecondary, animationSpec).value,
        animateColorAsState(platformColorScheme.secondaryContainer, animationSpec).value,
        animateColorAsState(platformColorScheme.onSecondaryContainer, animationSpec).value,
        animateColorAsState(platformColorScheme.tertiary, animationSpec).value,
        animateColorAsState(platformColorScheme.onTertiary, animationSpec).value,
        animateColorAsState(platformColorScheme.tertiaryContainer, animationSpec).value,
        animateColorAsState(platformColorScheme.onTertiaryContainer, animationSpec).value,
        animateColorAsState(platformColorScheme.background, animationSpec).value,
        animateColorAsState(platformColorScheme.onBackground, animationSpec).value,
        animateColorAsState(platformColorScheme.surface, animationSpec).value,
        animateColorAsState(platformColorScheme.onSurface, animationSpec).value,
        animateColorAsState(platformColorScheme.surfaceVariant, animationSpec).value,
        animateColorAsState(platformColorScheme.onSurfaceVariant, animationSpec).value,
        animateColorAsState(platformColorScheme.surfaceTint, animationSpec).value,
        animateColorAsState(platformColorScheme.inverseSurface, animationSpec).value,
        animateColorAsState(platformColorScheme.inverseOnSurface, animationSpec).value,
        animateColorAsState(platformColorScheme.error, animationSpec).value,
        animateColorAsState(platformColorScheme.onError, animationSpec).value,
        animateColorAsState(platformColorScheme.errorContainer, animationSpec).value,
        animateColorAsState(platformColorScheme.onErrorContainer, animationSpec).value,
        animateColorAsState(platformColorScheme.outline, animationSpec).value,
        animateColorAsState(platformColorScheme.outlineVariant, animationSpec).value,
        animateColorAsState(platformColorScheme.scrim, animationSpec).value,
        animateColorAsState(platformColorScheme.surfaceBright, animationSpec).value,
        animateColorAsState(platformColorScheme.surfaceDim, animationSpec).value,
        animateColorAsState(platformColorScheme.surfaceContainer, animationSpec).value,
        animateColorAsState(platformColorScheme.surfaceContainerHigh, animationSpec).value,
        animateColorAsState(platformColorScheme.surfaceContainerHighest, animationSpec).value,
        animateColorAsState(platformColorScheme.surfaceContainerLow, animationSpec).value,
        animateColorAsState(platformColorScheme.surfaceContainerLowest, animationSpec).value
    )

    MaterialTheme(colorScheme = colorScheme, typography = platformTypography(), content = content)
}

@Composable
expect fun platformColorScheme(viewModel: MainViewModel): ColorScheme

@Composable
expect fun platformTypography(): Typography