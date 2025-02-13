package com.example.composemaps.mapspage

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.composemaps.R
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.MapsComposeExperimentalApi
import com.google.maps.android.compose.clustering.Clustering
import kotlinx.coroutines.launch

private const val CameraOnMarkerClickZoom = 15f
const val StoresCameraAnimation = 1000
const val BoundariesZoomLevel = 40

@OptIn(ExperimentalPermissionsApi::class, MapsComposeExperimentalApi::class)
@Composable
fun MapsPage(
    modifier: Modifier = Modifier,
    viewModel: MapsViewModel = viewModel()
) {
    val locationPermission = rememberPermissionState(android.Manifest.permission.ACCESS_FINE_LOCATION)
    val properties = remember(locationPermission.status.isGranted) { MapProperties(isMyLocationEnabled = locationPermission.status.isGranted) }
    val uiSettings = remember { MapUiSettings(mapToolbarEnabled = false, zoomControlsEnabled = false, myLocationButtonEnabled = false) }
    val cameraPositionState = viewModel.cameraPosition
    val scope = rememberCoroutineScope()
    val state = viewModel.state.collectAsStateWithLifecycle().value

    Box(modifier = modifier) {
        GoogleMap(
            properties = properties,
            uiSettings = uiSettings,
            cameraPositionState = cameraPositionState,
            onMapClick = {},
            modifier = modifier,
        ) {
            Clustering(
                items = state.places,
                clusterContent = { cluster ->
                    Box(
                        modifier = Modifier
                            .defaultMinSize(minHeight = 32.dp, minWidth = 32.dp)
                            .background(shape = CircleShape, color = Color(0xFF4ACF08))
                    ) {
                        Text(
                            text = cluster.size.toString(),
                            color = Color(0xFFFFFFFF),
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                },
                onClusterClick = {
                    scope.launch {
                        val boundsBuilder = LatLngBounds.Builder()
                        it.items.forEach { boundsBuilder.include(LatLng(it.location.latitude, it.location.longitude)) }
                        cameraPositionState.animate(
                            CameraUpdateFactory.newLatLngBounds(boundsBuilder.build(), BoundariesZoomLevel),
                            StoresCameraAnimation
                        )
                    }
                    true
                },
                onClusterItemClick = {
                    scope.launch {
                        cameraPositionState.animate(
                            CameraUpdateFactory.newLatLngZoom(it.position, CameraOnMarkerClickZoom),
                            StoresCameraAnimation
                        )
                    }
                    false
                }
            )
        }
        FloatingActionButton(
            onClick = {},
            containerColor = Color(0xFF000000),
            shape = CircleShape,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(
                    end = 16.dp,
                    bottom = 16.dp
                )
        ) {
            Icon(
                painter = painterResource(R.drawable.icn_location),
                contentDescription = null,
                tint = Color(0xFFFFFFFF),
                modifier = Modifier.size(size = 24.dp)
            )
        }
    }
}
