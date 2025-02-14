package com.example.composemaps.mapspage

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.CameraPositionState.Companion.invoke
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class MapsViewModel : ViewModel() {

    private val _state = MutableStateFlow(UiState())
    val state = _state.asStateFlow()

    val cameraPosition by mutableStateOf(
        CameraPositionState(
            position = CameraPosition.fromLatLngZoom(LatLng(UK_LATITUDE, UK_LONGITUDE), UK_ZOOM)
        )
    )

    data class UiState(
        val places: List<Place> = listOf(
            Place(name = "Edinburgh", location = LatLng(55.953251, -3.188267)),
            Place(name = "Perth", location = LatLng(56.394993, -3.430838)),
            Place(name = "Aberdeen", location = LatLng(57.147480, -2.095400)),
            Place(name = "Inverness", location = LatLng(57.476688, -4.231450)),
            Place(name = "Stirling", location = LatLng(56.11753845214844, -3.936826705932617)),
            Place(name = "Dunfermline", location = LatLng(56.071381, -3.461620)),
            Place(name = "Glasgow", location = LatLng(55.861155, -4.2501687)),
            Place(name = "Dundee", location = LatLng(56.4605938, -2.97019)),
        )
    )

    data class Place(
        val name: String,
        val location: LatLng,
    ): ClusterItem {
        override fun getPosition(): LatLng = location

        override fun getTitle(): String = name

        override fun getSnippet(): String? = null

        override fun getZIndex(): Float? = null
    }

    private companion object {
        private const val UK_LATITUDE = 54.90347944287703
        private const val UK_LONGITUDE = -3.8702582241921735
        private const val UK_ZOOM = 5.5f
    }
}
