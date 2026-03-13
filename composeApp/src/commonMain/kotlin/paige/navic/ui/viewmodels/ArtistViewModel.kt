package paige.navic.ui.viewmodels

import androidx.compose.foundation.ScrollState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.zt64.subsonic.api.model.Artist
import dev.zt64.subsonic.api.model.ArtistInfo
import dev.zt64.subsonic.api.model.Song
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import paige.navic.data.session.SessionManager
import paige.navic.utils.UiState

data class ArtistState(
	val artist: Artist,
	val topSongs: List<Song>,
	val info: ArtistInfo,
	val similarArtists: List<Artist>
)

class ArtistViewModel(
	private val artistId: String
) : ViewModel() {
	private val _artistState = MutableStateFlow<UiState<ArtistState>>(UiState.Loading)
	val artistState = _artistState.asStateFlow()

	val scrollState = ScrollState(initial = 0)

	init {
		viewModelScope.launch {
			try {
				val artist = SessionManager.api.getArtist(artistId)
				val topSongs = SessionManager.api.getTopSongs(artist)
				val artistInfo = SessionManager.api.getArtistInfo(artist)
				val similarArtists = artistInfo.similarArtists.map {
					// albumCount is 0 unless you do getArtist on each one
					SessionManager.api.getArtist(it.id)
				}
				_artistState.value = UiState.Success(ArtistState(
					artist,
					topSongs,
					artistInfo,
					similarArtists
				))
			} catch (e: Exception) {
				_artistState.value = UiState.Error(e)
			}
		}
	}
}