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

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.unit.dp
import com.squaredem.composecalendar.daterange.DateRange
import com.squaredem.composecalendar.daterange.DateRangeStep
import com.squaredem.composecalendar.utils.LogCompositions
import com.squaredem.composecalendar.utils.getFirstLetter
import com.squaredem.composecalendar.utils.withDayOfMonth
import com.squaredem.composecalendar.utils.withYear
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn

@ExperimentalFoundationApi
@Composable
internal fun CalendarPicker(
    selectedDate: LocalDate?,
    minDate: LocalDate,
    maxDate: LocalDate,
    onSelected: (LocalDate) -> Unit,
) {
    LogCompositions("CalendarContent")

    val dateRange = DateRange(minDate, maxDate)
    val monthRange = DateRange(minDate.withDayOfMonth(1), maxDate.withDayOfMonth(1), DateRangeStep.MONTH)
    val yearRange = minDate.year..maxDate.year
    val monthPageCount = monthRange.count()
    val currentDate = Clock.System.todayIn(TimeZone.currentSystemDefault()).coerceIn(minDate, maxDate)

    val initialPage = remember { getStartPage(selectedDate ?: currentDate, monthRange, monthPageCount) }
    val pagerState = rememberPagerState(initialPage)
    // for display only, used in CalendarMonthYearSelector
    var currentPagerDate by remember { mutableStateOf(selectedDate ?: currentDate) }

    var isPickingYear by remember { mutableStateOf(false) }
    val gridState = with(yearRange.indexOfFirst { it == currentPagerDate.year }) {
        rememberLazyGridState(initialFirstVisibleItemIndex = this)
    }

    val coroutineScope = rememberCoroutineScope()

    if (!LocalInspectionMode.current) {
        LaunchedEffect(pagerState) {
            snapshotFlow { pagerState.currentPage }.collect { page ->
                getDateFromCurrentPage(page, monthRange)?.let {
                    currentPagerDate = it
                }
            }
        }
    }

    Column {
        Spacer(Modifier.height(16.dp))

        CalendarMonthYearSelector(
            currentPagerDate,
            onClick = { isPickingYear = !isPickingYear },
            hasPreviousMonth = pagerState.currentPage > 0,
            hasNextMonth = pagerState.currentPage < monthPageCount - 1,
            onNextMonth = {
                coroutineScope.launch {
                    try {
                        val newPage = pagerState.currentPage + 1
                        pagerState.animateScrollToPage(newPage)
                    } catch (e: Exception) {
                        // avoid IndexOutOfBounds and animation crashes
                    }
                }
            },
            onPreviousMonth = {
                coroutineScope.launch {
                    try {
                        val newPage = pagerState.currentPage - 1
                        pagerState.animateScrollToPage(newPage)
                    } catch (e: Exception) {
                        // avoid IndexOutOfBounds and animation crashes
                    }
                }
            },
            expanded = isPickingYear
        )

        Spacer(Modifier.height(16.dp).background(Color.Red))

        if (!isPickingYear) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                DayOfWeek.values().forEach {
                    Box(
                        modifier = Modifier.size(40.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = it.getFirstLetter(),
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                }
            }

            HorizontalPager(
                pageCount = monthPageCount,
                state = pagerState
            ) { page ->
                getDateFromCurrentPage(page, monthRange)?.let { pagerDate ->
                    // grid
                    CalendarGrid(
                        pagerDate = pagerDate.withDayOfMonth(1),
                        dateRange = dateRange,
                        selectedDate = selectedDate,
                        onSelected = { date ->
                            val index = yearRange.indexOfFirst { it == date.year }
                            if (index > 0) {
                                coroutineScope.launch {
                                    gridState.scrollToItem(index)
                                }
                            }
                            onSelected(date)
                        },
                        true,
                    )
                }
            }

        } else {

            CalendarYearGrid(
                gridState = gridState,
                dateRangeByYear = yearRange,
                selectedYear = selectedDate?.year,
                onYearClick = { year ->
                    currentPagerDate = currentPagerDate.withYear(year)
                    selectedDate?.let { onSelected(it.withYear(year)) }
                    isPickingYear = false
                    coroutineScope.launch {
                        val newPage = monthRange.indexOfFirst {
                            it.year == year && it.month == currentPagerDate.month
                        }
                        pagerState.scrollToPage(newPage)
                    }
                }
            )

            Divider()
        }
    }
}

private fun getStartPage(
    startDate: LocalDate,
    dateRange: DateRange,
    pageCount: Int
): Int {
    if (startDate <= dateRange.start) {
        return 0
    }
    if (startDate >= dateRange.endInclusive) {
        return pageCount
    }
    val indexOfRange = dateRange.indexOfFirst {
        it.year == startDate.year && it.monthNumber == startDate.monthNumber
    }
    return if (indexOfRange != -1) indexOfRange else pageCount / 2
}

private fun getDateFromCurrentPage(
    currentPage: Int,
    dateRange: DateRange,
): LocalDate? {
    return try {
        dateRange.elementAt(currentPage)
    } catch (e: Throwable) {
        null
    }
}

//@Preview(showBackground = true)
//@Composable
//private fun Preview() {
//    CalendarContent(
//        startDate = LocalDate.now(),
//        minDate = LocalDate.now(),
//        maxDate = LocalDate.MAX,
//        onSelected = {},
//    )
//}
