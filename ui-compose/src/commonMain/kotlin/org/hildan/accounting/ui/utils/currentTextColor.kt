package org.hildan.accounting.ui.utils

import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.takeOrElse

@Composable
internal fun currentTextColor(): Color = LocalTextStyle.current.color.takeOrElse { LocalContentColor.current }