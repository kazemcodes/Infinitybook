package ireader.presentation.ui.video.component

import android.text.format.DateUtils
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.systemGestureExclusion
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import ireader.presentation.ui.component.components.Toolbar
import ireader.presentation.ui.component.reusable_composable.AppIconButton
import ireader.presentation.ui.component.reusable_composable.MidSizeTextComposable
import ireader.presentation.ui.video.component.core.MediaState
import ireader.presentation.ui.video.component.core.TimeBar
import ireader.presentation.ui.video.component.core.TimeBarProgress
import ireader.presentation.ui.video.component.core.TimeBarScrubber
import ireader.presentation.ui.video.component.core.rememberControllerState
import kotlinx.coroutines.delay
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * A simple controller, which consists of a play/pause button and a time bar.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleController(
    title: String,
    mediaState: MediaState,
    modifier: Modifier = Modifier,
    onShowMenu:() -> Unit
) {
    val maxDuration = remember {
        mutableStateOf("")
    }
    Crossfade(targetState = mediaState.isControllerShowing, modifier) { isShowing ->
        if (isShowing) {
            val controllerState = rememberControllerState(mediaState)
            var scrubbing by remember { mutableStateOf(false) }
            val hideWhenTimeout = !mediaState.shouldShowControllerIndefinitely && !scrubbing
            var hideEffectReset by remember { mutableStateOf(0) }
            LaunchedEffect(hideWhenTimeout, hideEffectReset) {
                if (hideWhenTimeout) {
                    // hide after 3s
                    delay(3000)
                    mediaState.isControllerShowing = false
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0x98000000))
            ) {
                Toolbar(title = {
                    MidSizeTextComposable(text = title)
                }, backgroundColor = Color.Transparent, contentColor = Color.White, actions = {
                    AppIconButton(imageVector = Icons.Default.Menu, onClick = onShowMenu, tint = Color.White)
                })



                Row(modifier = Modifier.align(Alignment.Center)) {
                    AppIconButton(
                        imageVector = Icons.Default.FastRewind, modifier = Modifier
                            .size(52.dp), onClick = {
                            mediaState.player?.currentPosition?.let { position ->
                                controllerState.seekTo(position.minus(15000L))
                            }

                        }, tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Image(
                        imageVector =
                        if (controllerState.showPause) Icons.Default.Pause
                        else Icons.Default.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier
                            .size(52.dp)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                            ) {
                                hideEffectReset++
                                controllerState.playOrPause()
                            },
                        colorFilter = ColorFilter.tint(Color.White)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    AppIconButton(
                        imageVector = Icons.Default.FastForward, modifier = Modifier
                            .size(52.dp), onClick = {
                            mediaState.player?.currentPosition?.let { position ->
                                controllerState.seekTo(position.plus(15000L))
                            }
                        }, tint = Color.White
                    )
                }


                LaunchedEffect(Unit) {
                    while (true) {
                        delay(200)
                        controllerState.triggerPositionUpdate()
                    }
                }
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(vertical = 16.dp)
                        .fillMaxWidth()

                ) {
                    MidSizeTextComposable(
                        modifier = Modifier
                            .padding(horizontal = 10.dp),
                        text = formatDuration(
                            controllerState.positionMs,
                        ).plus("/" + getMaxDuration(controllerState.durationMs, maxDuration)),
                        color = Color.White
                    )
                    TimeBar(
                        controllerState.durationMs,
                        controllerState.positionMs,
                        controllerState.bufferedPositionMs,
                        modifier = Modifier
                            .systemGestureExclusion()
                            .fillMaxWidth()
                            .height(28.dp),
                        contentPadding = PaddingValues(12.dp),
                        scrubberCenterAsAnchor = true,
                        onScrubStart = { scrubbing = true },
                        onScrubStop = { positionMs ->
                            scrubbing = false
                            controllerState.seekTo(positionMs)
                        },
                        scrubber = { enabled, scrubbing ->
                            TimeBarScrubber(
                                enabled,
                                scrubbing,
                                draggedSize = 20.dp,
                                color = Color.Red
                            )
                        },
                        progress = { current, _, buffered ->
                            TimeBarProgress(current, buffered, playedColor = Color.Red)
                        }
                    )
                }
            }
        }
    }
}

fun getMaxDuration(time: Long, lastTime: MutableState<String>): String {
    if (lastTime.value != "") return lastTime.value
    val hour = String.format("%02d", TimeUnit.MILLISECONDS.toHours(time)).takeIf { it != "00" }
    val min = String.format("%02d", TimeUnit.MILLISECONDS.toMinutes(time))
    val sec = String.format("%02d", TimeUnit.MILLISECONDS.toSeconds(time))

    var maxDuration = ""
    if (hour != null) {
        maxDuration += "$hour:"
    }
    maxDuration += "$min:"
    maxDuration += sec

    return maxDuration
}


private fun formatDuration(totalSecsInMillis: Long): String {
    return DateUtils.formatElapsedTime(totalSecsInMillis / 1000)
}