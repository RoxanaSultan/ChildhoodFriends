package com.cst.cstacademy2024

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.tomtom.sdk.map.display.ui.MapView
import com.tomtom.sdk.map.display.camera.CameraPosition
import com.tomtom.sdk.map.display.camera.*
//import com.tomtom.sdk.map.display.location.
import com.tomtom.sdk.map.display.*
import com.tomtom.sdk.map.display.marker.Marker
import com.tomtom.sdk.map.display.marker.MarkerOptions
//import com.tomtom.sdk.map.display.model.Marker
//import com.tomtom.sdk.map.display.model.MarkerOptions
import com.tomtom.sdk.search.*
import com.tomtom.sdk.search.fuzzy.FuzzySearchOptions
import com.tomtom.sdk.search.fuzzy.FuzzySearchQuery

class MapsFragment : Fragment() {

    private lateinit var mapView: MapView
    private var currentLocation: Location? = null
    private var currentLocationMarker: Marker? = null
    private var otherLocationMarker: Marker? = null
    private lateinit var searchView: SearchView
    private lateinit var searchApi: Search

    private val REQUEST_CODE = 101

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.activity_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mapView = view.findViewById(R.id.map_view)
        searchView = view.findViewById(R.id.search_view)
        searchApi = SearchApi.create(requireContext())

        requestLocationPermission()
        initializeSearchView()
    }

    private fun initializeSearchView() {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { searchForLocation(it) }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })
    }

    private fun searchForLocation(query: String) {
        val options = FuzzySearchOptions.Builder().limit(1).build()
        val searchQuery = FuzzySearchQuery.Builder(query).withOptions(options).build()

        searchApi.performSearch(searchQuery) { result ->
            result.fold(onSuccess = { response ->
                response.results.firstOrNull()?.let {
                    val position = LatLng(it.position.latitude, it.position.longitude)
                    moveCameraToLocation(position, it.address.freeformAddress)
                }
            }, onFailure = { error ->
                Toast.makeText(context, "Search failed: ${error.message}", Toast.LENGTH_SHORT).show()
            })
        }
    }

    private fun moveCameraToLocation(latLng: LatLng, address: String?) {
        otherLocationMarker?.remove()
        val markerOptions = MarkerOptions.Builder()
            .position(latLng)
            .title(address ?: "Selected Location")
            .build()

        mapView.camera.update(CameraUpdateFactory.newCameraPosition(CameraPosition(latLng, 18f)))
        otherLocationMarker = mapView.addMarker(markerOptions)
    }

    private fun requestLocationPermission() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                REQUEST_CODE
            )
        } else {
            getCurrentLocation()
        }
    }

    private fun getCurrentLocation() {
        // This method should implement getting the current location using Location Services
        // After obtaining location, call setupCurrentLocationMarker()
    }

    private fun setupCurrentLocationMarker(latLng: LatLng, address: String) {
        currentLocationMarker?.remove()
        val markerOptions = MarkerOptions.Builder()
            .position(latLng)
            .title(address)
            .build()

        mapView.camera.update(CameraUpdateFactory.newCameraPosition(CameraPosition(latLng, 18f)))
        currentLocationMarker = mapView.addMarker(markerOptions)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocation()
        } else {
            Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT).show()
        }
    }
}
