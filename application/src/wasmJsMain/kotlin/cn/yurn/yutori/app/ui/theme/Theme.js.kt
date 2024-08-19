@file:OptIn(ExperimentalResourceApi::class)

package cn.yurn.yutori.app.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.platform.Font
import cn.yurn.yutori.app.MainViewModel
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.getFontResourceBytes
import org.jetbrains.compose.resources.getSystemResourceEnvironment
import yutori.application.generated.resources.MiSans_Regular
import yutori.application.generated.resources.Res

@Composable
actual fun platformColorScheme(viewModel: MainViewModel): ColorScheme {
    return when {
        viewModel.darkMode -> darkScheme
        else -> lightScheme
    }
}

@OptIn(ExperimentalResourceApi::class)
@Composable
actual fun platformTypography(): Typography {
    val defaultTypography = MaterialTheme.typography
    var typography by remember { mutableStateOf<Typography?>(null) }
    LaunchedEffect(Unit) {
        val fontFamily = FontFamily(
            Font(
                identity = "MiSans-Regular",
                data = getFontResourceBytes(getSystemResourceEnvironment(), Res.font.MiSans_Regular)
            )
        )
        typography = Typography(
            defaultTypography.displayLarge.copy(fontFamily = fontFamily),
            defaultTypography.displayMedium.copy(fontFamily = fontFamily),
            defaultTypography.displaySmall.copy(fontFamily = fontFamily),
            defaultTypography.headlineLarge.copy(fontFamily = fontFamily),
            defaultTypography.headlineMedium.copy(fontFamily = fontFamily),
            defaultTypography.headlineSmall.copy(fontFamily = fontFamily),
            defaultTypography.titleLarge.copy(fontFamily = fontFamily),
            defaultTypography.titleMedium.copy(fontFamily = fontFamily),
            defaultTypography.titleSmall.copy(fontFamily = fontFamily),
            defaultTypography.bodyLarge.copy(fontFamily = fontFamily),
            defaultTypography.bodyMedium.copy(fontFamily = fontFamily),
            defaultTypography.bodySmall.copy(fontFamily = fontFamily),
            defaultTypography.labelLarge.copy(fontFamily = fontFamily),
            defaultTypography.labelMedium.copy(fontFamily = fontFamily),
            defaultTypography.labelSmall.copy(fontFamily = fontFamily)
        )
    }
    return typography ?: defaultTypography
}