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

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.squaredem.composecalendar.utils.LogCompositions
import com.squaredem.composecalendar.utils.headlineFormat
import kotlinx.datetime.LocalDate

@Composable
internal fun CalendarTopBar(
    title: @Composable () -> Unit,
    selectedDate: LocalDate,
) {
    Column(Modifier.padding(horizontal = 12.dp)) {
        CompositionLocalProvider(
            LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant,
            LocalTextStyle provides MaterialTheme.typography.labelMedium,
        ) {
            title()
        }

        Spacer(Modifier.height(36.dp))

        Row(
            modifier = Modifier
                .height(40.dp),
        ) {
            LogCompositions("CalendarTopBar")

            Text(
                modifier = Modifier.weight(1f),
                text = selectedDate.headlineFormat(),
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.width(8.dp))
            IconButton(
                onClick = {}
            ) {
                Icon(Icons.Default.Edit, null)
            }
        }

        Spacer(Modifier.height(12.dp))
    }
}
