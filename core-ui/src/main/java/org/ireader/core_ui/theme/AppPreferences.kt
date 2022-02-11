package org.ireader.domain.ui

import org.ireader.core.prefs.Preference
import org.ireader.core.prefs.PreferenceStore
import org.ireader.core.prefs.getEnum
import org.ireader.core_ui.theme.OrientationMode
import org.ireader.core_ui.theme.ThemeMode

class AppPreferences(
    private val preferenceStore: PreferenceStore,
) {
    companion object PreferenceKeys {
        const val SAVED_FONT_SIZE_PREFERENCES = "reader_font_size"
        const val SAVED_FONT_PREFERENCES = "reader_font_family"
        const val SAVED_BRIGHTNESS_PREFERENCES = "reader_brightness"

        const val SAVED_LIBRARY_LAYOUT_KEY = "library_layout_type"
        const val SAVED_BROWSE_LAYOUT_KEY = "browser_layout_type"
        const val SAVED_BACkGROUND_COLOR = "background_color"
        const val SAVED_FONT_HEIGHT = "font_height"
        const val SAVED_PARAGRAPH_DISTANCE = "paragraph_distance"
        const val SAVED_PARAGRAPH_INDENT = "paragraph_indent"
        const val SAVED_ORIENTATION = "orientation_reader"
        const val SORT_LIBRARY_SCREEN = "sort_library_screen"
        const val FILTER_LIBRARY_SCREEN = "filter_library_screen"

        /** Services **/
        const val Last_UPDATE_CHECK = "last_update_check"

        /** Setting Pref**/
        const val SAVED_DOH_KEY = "SAVED_DOH_KEY"
        const val THEME_MODE_KEY = "theme_mode_key"
        const val LIGHT_MODE_KEY = "theme_light"
        const val NIGHT_MODE_KEY = "theme_dark"
    }

    fun brightness(): Preference<Float> {
        return preferenceStore.getFloat(SAVED_BRIGHTNESS_PREFERENCES, .5F)
    }

    fun fontSize(): Preference<Int> {
        return preferenceStore.getInt(SAVED_FONT_SIZE_PREFERENCES, 18)
    }

    fun font(): Preference<Int> {
        return preferenceStore.getInt(SAVED_FONT_PREFERENCES, 0)
    }

    fun libraryLayoutType(): Preference<Int> {
        return preferenceStore.getInt(SAVED_LIBRARY_LAYOUT_KEY, 0)
    }

    fun exploreLayoutType(): Preference<Int> {
        return preferenceStore.getInt(SAVED_BROWSE_LAYOUT_KEY, 0)
    }

    fun dohStateKey(): Preference<Int> {
        return preferenceStore.getInt(SAVED_DOH_KEY, 0)
    }

    fun backgroundColorIndex(): Preference<Int> {
        return preferenceStore.getInt(SAVED_BACkGROUND_COLOR, 0)
    }

    fun lineHeight(): Preference<Int> {
        return preferenceStore.getInt(SAVED_FONT_HEIGHT, 25)
    }

    fun paragraphDistance(): Preference<Int> {
        return preferenceStore.getInt(SAVED_PARAGRAPH_DISTANCE, 2)
    }

    fun orientation(): Preference<OrientationMode> {
        return preferenceStore.getEnum(SAVED_ORIENTATION, OrientationMode.Portrait)
    }

    fun paragraphIndent(): Preference<Int> {
        return preferenceStore.getInt(SAVED_PARAGRAPH_INDENT, 8)
    }

    fun sortLibraryScreen(): Preference<Int> {
        return preferenceStore.getInt(SORT_LIBRARY_SCREEN, 0)
    }

    fun filterLibraryScreen(): Preference<Int> {
        return preferenceStore.getInt(FILTER_LIBRARY_SCREEN, 0)
    }

    fun lastUpdateCheck(): Preference<Long> {
        return preferenceStore.getLong(Last_UPDATE_CHECK, 0)
    }

}


class UiPreferences(private val preferenceStore: PreferenceStore) {

    fun themeMode(): Preference<ThemeMode> {
        return preferenceStore.getEnum("theme_mode", ThemeMode.System)
    }

    fun lightTheme(): Preference<Int> {
        return preferenceStore.getInt("theme_light", 6)
    }

    fun darkTheme(): Preference<Int> {
        return preferenceStore.getInt("theme_dark", 5)
    }

    fun colorPrimaryLight(): Preference<Int> {
        return preferenceStore.getInt("color_primary_light", 0)
    }

    fun colorPrimaryDark(): Preference<Int> {
        return preferenceStore.getInt("color_primary_dark", 0)
    }

    fun colorSecondaryLight(): Preference<Int> {
        return preferenceStore.getInt("color_secondary_light", 0)
    }

    fun colorSecondaryDark(): Preference<Int> {
        return preferenceStore.getInt("color_secondary_dark", 0)
    }

    fun colorBarsLight(): Preference<Int> {
        return preferenceStore.getInt("color_bar_light", 0)
    }

    fun colorBarsDark(): Preference<Int> {
        return preferenceStore.getInt("color_bar_dark", 0)
    }

    fun confirmExit(): Preference<Boolean> {
        return preferenceStore.getBoolean("confirm_exit", false)
    }

    fun hideBottomBarOnScroll(): Preference<Boolean> {
        return preferenceStore.getBoolean("hide_bottom_bar_on_scroll", true)
    }

    fun language(): Preference<String> {
        return preferenceStore.getString("language", "")
    }

    fun dateFormat(): Preference<String> {
        return preferenceStore.getString("date_format", "")
    }

    fun downloadedOnly(): Preference<Boolean> {
        return preferenceStore.getBoolean("downloaded_only", false)
    }

    fun incognitoMode(): Preference<Boolean> {
        return preferenceStore.getBoolean("incognito_mode", false)
    }

}
