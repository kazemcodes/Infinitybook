package org.ireader.reader

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.DrawerValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberDrawerState
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.ireader.common_models.entities.Chapter
import org.ireader.components.components.ISnackBarHost
import org.ireader.core_api.source.Source
import org.ireader.reader.components.MainBottomSettingComposable
import org.ireader.reader.components.ReaderSettingComposable
import org.ireader.reader.reverse_swip_refresh.SwipeRefreshState
import org.ireader.reader.viewmodel.ReaderScreenViewModel

@ExperimentalAnimationApi
@OptIn(
    ExperimentalMaterialApi::class, com.google.accompanist.pager.ExperimentalPagerApi::class,
    dev.chrisbanes.snapper.ExperimentalSnapperApi::class
)
@Composable
fun ReadingScreen(
    modifier: Modifier = Modifier,
    vm: ReaderScreenViewModel,
    source: Source,
    scrollState: LazyListState,
    drawerScrollState: LazyListState,
    swipeState: SwipeRefreshState,
    onNext: () -> Unit,
    onPrev: (scrollToEnd: Boolean) -> Unit,
    onChapter: (Chapter) -> Unit,
    onSliderFinished: () -> Unit,
    onSliderChange: (index: Float) -> Unit,
    onDrawerRevereIcon: (Chapter?) -> Unit,
    onReaderRefresh: (Chapter?) -> Unit,
    onReaderWebView: (ModalBottomSheetState) -> Unit,
    onReaderBookmark: () -> Unit,
    onReaderBottomOnSetting: () -> Unit,
    onReaderPlay: () -> Unit,
    onFontSelected: (Int) -> Unit,
    onToggleScrollMode: (Boolean) -> Unit,
    onToggleAutoScroll: (Boolean) -> Unit,
    onToggleOrientation: (Boolean) -> Unit,
    onToggleImmersiveMode: (Boolean) -> Unit,
    onToggleSelectedMode: (Boolean) -> Unit,
    onFontSizeIncrease: (Boolean) -> Unit,
    onParagraphIndentIncrease: (Boolean) -> Unit,
    onParagraphDistanceIncrease: (Boolean) -> Unit,
    onLineHeightIncrease: (Boolean) -> Unit,
    onAutoscrollIntervalIncrease: (Boolean) -> Unit,
    onAutoscrollOffsetIncrease: (Boolean) -> Unit,
    onScrollIndicatorPaddingIncrease: (Boolean) -> Unit,
    onScrollIndicatorWidthIncrease: (Boolean) -> Unit,
    onToggleAutoBrightness: () -> Unit,
    onChangeBrightness: (Float) -> Unit,
    onBackgroundChange: (Int) -> Unit,
    onMap: (LazyListState) -> Unit,
    onPopBackStack: () -> Unit,
) {

    val chapters = vm.stateChapters
    val scaffoldState = rememberScaffoldState(rememberDrawerState(DrawerValue.Closed))
    val modalState =
        rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
    val scope = rememberCoroutineScope()

    val chapter = vm.stateChapter
    val context = LocalContext.current

    LaunchedEffect(key1 = scaffoldState.drawerState.targetValue) {
        if (chapter != null && scaffoldState.drawerState.targetValue == DrawerValue.Open && vm.stateChapters.isNotEmpty()) {
                val index = vm.stateChapters.indexOfFirst { it.id == chapter.id }
                if (index != -1) {
                    scope.launch {
                        drawerScrollState.scrollToItem(
                            index,
                            -drawerScrollState.layoutInfo.viewportEndOffset / 2
                        )
                    }
                }
            }
    }
    LaunchedEffect(key1 = vm.autoScrollMode) {
        while (vm.autoScrollInterval != 0L && vm.autoScrollMode) {
            scrollState.scrollBy(vm.autoScrollOffset.toFloat())
            delay(vm.autoScrollInterval)
        }
    }
    LaunchedEffect(key1 = modalState.currentValue) {
        when (modalState.currentValue) {
            ModalBottomSheetValue.Expanded -> vm.isReaderModeEnable = false
            ModalBottomSheetValue.Hidden -> vm.isReaderModeEnable = true
            else -> {}
        }
    }
    LaunchedEffect(key1 = vm.isReaderModeEnable) {
        when (vm.isReaderModeEnable) {
            false -> {
                scope.launch {
                    modalState.snapTo(ModalBottomSheetValue.Expanded)
                }
            }
            true -> {
                scope.launch {
                    modalState.snapTo(ModalBottomSheetValue.Hidden)
                }
            }
        }
    }
    LaunchedEffect(key1 = vm.autoBrightnessMode) {
        vm.prefFunc.apply {
            vm.readBrightness(context)
        }
    }
    LaunchedEffect(key1 = vm.initialized) {
        kotlin.runCatching {
            scrollState.scrollToItem(chapter?.progress ?: 1)
        }
    }


    LaunchedEffect(key1 = true) {

        vm.prefFunc.apply {
            vm.readOrientation(context)
            vm.readBrightness(context)
            vm.readImmersiveMode(context)
        }

        vm.eventFlow.collectLatest { event ->
            when (event) {
                is org.ireader.common_extensions.UiEvent.ShowSnackbar -> {
                    scaffoldState.snackbarHostState.showSnackbar(
                        event.uiText.asString(context)
                    )
                }
                else -> {}
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            ReaderScreenTopBar(
                isReaderModeEnable = vm.isReaderModeEnable,
                isLoaded = vm.isChapterLoaded.value,
                modalBottomSheetValue = modalState.targetValue,
                onRefresh = {
                    onReaderRefresh(chapter)
                },
                source = source,
                chapter = chapter,
                onWebView = {
                    onReaderWebView(modalState)
                },
                vm = vm,
                state = vm,
                scrollState = scrollState,
                onBookMark = onReaderBookmark,
                onPopBackStack = onPopBackStack
            )
        },
        scaffoldState = scaffoldState,
        snackbarHost = { ISnackBarHost(snackBarHostState = it) },
        bottomBar = {
            if (!vm.isReaderModeEnable && chapter != null) {
                ModalBottomSheetLayout(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(IntrinsicSize.Max)
                        .height(if (vm.isMainBottomModeEnable) 130.dp else 320.dp),
                    sheetBackgroundColor = MaterialTheme.colors.background,
                    sheetElevation = 8.dp,
                    sheetState = modalState,
                    sheetContent = {
                        Column(modifier.fillMaxSize()) {
                            Divider(
                                modifier = modifier.fillMaxWidth(),
                                color = MaterialTheme.colors.onBackground.copy(alpha = .2f),
                                thickness = 1.dp
                            )
                            Spacer(modifier = modifier.height(5.dp))
                            if (vm.isMainBottomModeEnable) {
                                MainBottomSettingComposable(
                                    scope = scope,
                                    scaffoldState = scaffoldState,
                                    scrollState = scrollState,
                                    chapter = chapter,
                                    chapters = vm.stateChapters,
                                    currentChapterIndex = vm.currentChapterIndex,
                                    onSetting = onReaderBottomOnSetting,
                                    source = source,
                                    onNext = onNext,
                                    onPrev = {
                                        onPrev(false)
                                    },
                                    onSliderChange = onSliderChange,
                                    onSliderFinished = onSliderFinished,
                                    onPlay = onReaderPlay
                                )
                            }
                            if (vm.isSettingModeEnable) {
                                ReaderSettingComposable(
                                    onFontSelected = onFontSelected,
                                    onAutoscrollIntervalIncrease = onAutoscrollIntervalIncrease,
                                    onAutoscrollOffsetIncrease = onAutoscrollOffsetIncrease,
                                    onFontSizeIncrease = onFontSizeIncrease,
                                    onLineHeightIncrease = onLineHeightIncrease,
                                    onParagraphDistanceIncrease = onParagraphDistanceIncrease,
                                    onParagraphIndentIncrease = onParagraphIndentIncrease,
                                    onScrollIndicatorPaddingIncrease = onScrollIndicatorPaddingIncrease,
                                    onScrollIndicatorWidthIncrease = onScrollIndicatorWidthIncrease,
                                    onToggleAutoScroll = onToggleAutoScroll,
                                    onToggleImmersiveMode = onToggleImmersiveMode,
                                    onToggleOrientation = onToggleOrientation,
                                    onToggleScrollMode = onToggleScrollMode,
                                    onToggleSelectedMode = onToggleSelectedMode,
                                    onChangeBrightness = onChangeBrightness,
                                    onToggleAutoBrightness = onToggleAutoBrightness,
                                    onBackgroundChange = onBackgroundChange,
                                    vm = vm
                                )
                            }
                        }
                    },
                    content = {}
                )
            }
        },
        drawerGesturesEnabled = true,
        drawerBackgroundColor = MaterialTheme.colors.background,
        drawerContent = {
            ReaderScreenDrawer(
                modifier = Modifier.statusBarsPadding(),
                onReverseIcon = {
                    onDrawerRevereIcon(chapter)
                },
                onChapter = onChapter,
                chapter = chapter,
                source = source,
                chapters = vm.drawerChapters.value,
                drawerScrollState = drawerScrollState,
                onMap = onMap,
            )
        }
    ) { padding ->
        ScrollIndicatorSetting(enable = vm.scrollIndicatorDialogShown, vm)
        if (chapter != null) {
            Box(modifier = modifier.fillMaxSize()) {
                if (!chapter.isEmpty() && !vm.isLoading) {
                    ReaderText(
                        vm = vm,
                        chapter = chapter,
                        onNext = onNext,
                        swipeState = swipeState,
                        onPrev = { onPrev(true) },
                        scrollState = scrollState,
                        modalState = modalState
                    )
                }

                if (vm.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = MaterialTheme.colors.primary
                    )
                }
            }
        }
    }
}