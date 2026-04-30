package paige.navic.ui.screens.nowPlaying

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import com.kyant.capsule.ContinuousCapsule
import com.kyant.capsule.ContinuousRoundedRectangle
import org.koin.compose.viewmodel.koinViewModel
import paige.navic.shared.MediaPlayerViewModel
import paige.navic.utils.rememberDraggableListState
import kotlin.math.floor

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun PlaybackSpeedScreen(
	player: MediaPlayerViewModel = koinViewModel<MediaPlayerViewModel>(),
) {
	val lazyListState = LazyListState()
	val haptic = LocalHapticFeedback.current

	val draggableState = rememberDraggableListState(lazyListState) { from, to ->
		player.moveQueueItem(from, to)
		haptic.performHapticFeedback(HapticFeedbackType.SegmentFrequentTick)
	}

	var selectedSpeed by remember { mutableStateOf(1.0f) }
	val playbackSpeeds = listOf(
		1.0f,
		1.25f,
		1.5f,
		1.75f,
		2.0f
	)

	LazyColumn(
		modifier = Modifier
			.padding(horizontal = 12.dp, vertical = 4.dp)
			.fillMaxSize()
			.clip(ContinuousRoundedRectangle(topStart = 16.dp, topEnd = 16.dp)),
		state = draggableState.listState
	) {
		item {
			Row(
				modifier = Modifier.fillMaxWidth(),
				verticalAlignment = Alignment.CenterVertically
			) {
				Slider(
					value = selectedSpeed,
					onValueChange = {
						val intValue = floor(it).toInt()
						val decPart = floor((it - intValue) * 100).toInt()
						val value = "$intValue.$decPart".toFloat()

						selectedSpeed = value
						player.setPlaybackSpeed(selectedSpeed)
					},
					valueRange = 0.5f..2.0f,
					modifier = Modifier.weight(1f)
				)
			}

			Spacer(Modifier.height(8.dp))
		}

		item {
			Column(
				modifier = Modifier.fillMaxWidth(),
				horizontalAlignment = Alignment.CenterHorizontally,
				verticalArrangement = Arrangement.Center
			) {
				Text("${selectedSpeed}x")

				Row(
					verticalAlignment = Alignment.CenterVertically,
					horizontalArrangement = Arrangement.Center
				) {
					(0..4).forEach { button ->
						SurfaceButton(
							modifier = Modifier.weight(1f),
							onClick = {
								selectedSpeed = playbackSpeeds[button]
								player.setPlaybackSpeed(selectedSpeed)
							},
							text = "${playbackSpeeds[button]}"
						)
					}
				}
			}
		}
	}
}

@Composable
fun SurfaceButton(
	modifier: Modifier,
	onClick: () -> Unit,
	text: String
) {
	Surface(
		modifier = modifier.padding(4.dp),
		shape = ContinuousCapsule,
		onClick = onClick,
		color = MaterialTheme.colorScheme.surfaceContainerHigh,
		contentColor = MaterialTheme.colorScheme.onSurface
	) {
		Column(
			modifier = Modifier.padding(8.dp),
			horizontalAlignment = Alignment.CenterHorizontally,
			verticalArrangement = Arrangement.Center
		) {
			Text(text)
		}
	}
}
