/*
 * Copyright 2022 Matteo Miceli
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.squaredem.composecalendar.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.squaredem.composecalendar.model.DateWrapper
import com.squaredem.composecalendar.utils.LogCompositions
import kotlinx.datetime.LocalDate

@Composable
internal fun CalendarDay(
    date: DateWrapper,
    onSelected: (LocalDate) -> Unit,
    showCurrentMonthOnly: Boolean,
) {
    LogCompositions("CalendarDay")

    var currentModifier = Modifier
        .size(40.dp)
        .clip(CircleShape)

    if (!date.isCurrentMonth && showCurrentMonthOnly) {
        Box(modifier = currentModifier)
        return
    }

    currentModifier = when {
        date.isSelectedDay -> {
            currentModifier
                .background(
                    color = MaterialTheme.colorScheme.primary,
                    shape = CircleShape
                )
        }
        date.isCurrentDay -> {
            currentModifier
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.primary,
                    shape = CircleShape
                )
        }
        else -> currentModifier
    }

    if (date.isInDateRange) {
        currentModifier = currentModifier.clickable {
            onSelected(date.localDate)
        }
    }

    val textColor = when {
        !date.isInDateRange -> {
            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38F)
        }
        date.isSelectedDay -> {
            MaterialTheme.colorScheme.onPrimary
        }
        date.isCurrentDay -> {
            MaterialTheme.colorScheme.primary
        }
        else -> MaterialTheme.colorScheme.onSurface
    }

    val text = "${date.localDate.dayOfMonth}"

    Box(
        modifier = currentModifier,
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = textColor,
            style = MaterialTheme.typography.bodySmall,
        )
    }
}
