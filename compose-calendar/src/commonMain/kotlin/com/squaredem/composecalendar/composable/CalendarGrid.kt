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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.squaredem.composecalendar.daterange.DateRange
import com.squaredem.composecalendar.daterange.DateRangeStep
import com.squaredem.composecalendar.daterange.rangeTo
import com.squaredem.composecalendar.model.DateWrapper
import com.squaredem.composecalendar.utils.LogCompositions
import kotlinx.datetime.*

@Composable
internal fun CalendarGrid(
    pagerDate: LocalDate,
    dateRange: DateRange,
    selectedDate: LocalDate?,
    onSelected: (LocalDate) -> Unit,
    showCurrentMonthOnly: Boolean,
) {
    LogCompositions("CalendarGrid")

    val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date

    val firstWeekDayOfMonth = pagerDate.dayOfWeek
    val pagerMonth = pagerDate.month

    val gridStartDay = pagerDate
        .minus(firstWeekDayOfMonth.ordinal - 1, DateTimeUnit.DAY)
    val gridEndDay = gridStartDay.plus(41, DateTimeUnit.DAY)

    val dates = (gridStartDay.rangeTo(gridEndDay)).map {
        val isCurrentMonth = it.month == pagerMonth
        val isCurrentDay = it == today
        val isSelectedDay = it == selectedDate
        val isInDateRange = it in dateRange

        DateWrapper(
            it,
            isSelectedDay,
            isCurrentDay,
            isCurrentMonth,
            isInDateRange,
        )
    }

    LazyVerticalGrid(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp),
        columns = GridCells.Fixed(7),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        items(dates) {
            CalendarDay(
                date = it,
                onSelected = onSelected,
                showCurrentMonthOnly = showCurrentMonthOnly,
            )
        }
    }

}
