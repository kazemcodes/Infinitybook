package org.ireader.domain.use_cases.preferences.reader_preferences


import org.ireader.core_ui.theme.AppPreferences
import org.ireader.core_ui.theme.OrientationMode
import org.ireader.domain.models.FilterType
import org.ireader.domain.models.SortType
import javax.inject.Inject

class SaveOrientationUseCase @Inject constructor(
    private val appPreferences: AppPreferences,
) {
    operator fun invoke(orientation: OrientationMode) {
        appPreferences.orientation().set(orientation)
    }
}


class ReadOrientationUseCase @Inject constructor(
    private val appPreferences: AppPreferences,
) {
    operator fun invoke(): OrientationMode {
        return appPreferences.orientation().get()
    }
}

class SaveFiltersUseCase @Inject constructor(
    private val appPreferences: AppPreferences,
) {
    operator fun invoke(value: Int) {
        appPreferences.filterLibraryScreen().set(value)
    }
}

class ReadFilterUseCase @Inject constructor(
    private val appPreferences: AppPreferences,
) {
    operator fun invoke(): FilterType {
        return mapFilterType(appPreferences.filterLibraryScreen().get())
    }
}

class SaveSortersUseCase @Inject constructor(
    private val appPreferences: AppPreferences,
) {
    operator fun invoke(value: Int) {
        appPreferences.sortLibraryScreen().set(value)
    }
}

class ReadSortersUseCase @Inject constructor(
    private val appPreferences: AppPreferences,
) {
    operator fun invoke(): SortType {
        return mapSortType(appPreferences.sortLibraryScreen().get())
    }
}

fun mapSortType(input: Int): SortType {
    return when (input) {
        0 -> {
            SortType.DateAdded
        }
        1 -> {
            SortType.Alphabetically
        }
        2 -> {
            SortType.LastRead
        }
        else -> {
            SortType.TotalChapter
        }
    }
}


fun mapFilterType(input: Int): FilterType {
    return when (input) {
        0 -> {
            FilterType.Disable
        }
        else -> {
            FilterType.Unread
        }
    }
}


