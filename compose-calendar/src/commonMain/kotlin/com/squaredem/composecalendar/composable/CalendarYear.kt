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

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import com.squaredem.composecalendar.utils.LogCompositions

@Composable
internal fun CalendarYear(
    year: Int,
    isSelectedYear: Boolean,
    setSelectedYear: (Int) -> Unit
) {
    LogCompositions("CalendarYear")

    if (isSelectedYear) {
        Button(onClick = { setSelectedYear(year) }) {
            Text("$year", maxLines = 1)
        }
    } else {
        TextButton(onClick = { setSelectedYear(year) }) {
            Text("$year", maxLines = 1)
        }
    }
}
