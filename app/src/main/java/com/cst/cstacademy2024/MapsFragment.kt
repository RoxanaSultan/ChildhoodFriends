package com.cst.cstacademy2024

import android.Manifest
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import java.io.IOException
import java.util.Locale

class MapsFragment : Fragment(), OnMapReadyCallback {

    private lateinit var gMap: GoogleMap
    private var currentLocation: Location? = null
    private var currentLocationMarker: Marker? = null
    private var otherLocationMarker: Marker? = null
    private lateinit var fusedClient: FusedLocationProviderClient
    private val REQUEST_CODE = 101
    private lateinit var searchView: SearchView
    private lateinit var placesClient: PlacesClient

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_maps, container, false)

        Places.initialize(requireActivity(), getString(R.string.google_maps_key))
        placesClient = Places.createClient(requireActivity())

        searchView = view.findViewById(R.id.search)
        searchView.clearFocus()

        fusedClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        requestLocationPermission()

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val supportMapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        supportMapFragment?.getMapAsync(this)
        setupSearchView()
    }

    private fun setupSearchView() {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                val loc = searchView.query.toString()
                if (loc.isEmpty()) {
                    Toast.makeText(requireActivity(), "Location Not Found", Toast.LENGTH_SHORT).show()
                } else {
                    // Use Places API to search for places
                    val fields = listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG)
                    val request = FindAutocompletePredictionsRequest.builder()
                        .setQuery(loc)
                        .build()

                    placesClient.findAutocompletePredictions(request)
                        .addOnSuccessListener { response ->
                            val prediction = response.autocompletePredictions.firstOrNull()
                            prediction?.let {
                                val placeId = it.placeId
                                val placeFieldsRequest = FetchPlaceRequest.builder(placeId, fields).build()

                                placesClient.fetchPlace(placeFieldsRequest)
                                    .addOnSuccessListener { fetchPlaceResponse ->
                                        val place = fetchPlaceResponse.place
                                        val latLng = place.latLng
                                        val placeName = place.name
                                        val placeAddress = place.address

                                        if (latLng != null) {
                                            otherLocationMarker?.remove()
                                            val markerOptions = MarkerOptions().position(latLng).title(placeName).snippet(placeName)
                                            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))

                                            gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18f), object : GoogleMap.CancelableCallback {
                                                override fun onFinish() {
                                                    otherLocationMarker = gMap.addMarker(markerOptions)
                                                }

                                                override fun onCancel() {}
                                            })
                                        } else {
                                            Toast.makeText(requireActivity(), "Location Not Found", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                    .addOnFailureListener { exception ->
                                        Toast.makeText(requireActivity(), "Failed to fetch place details: ${exception.message}", Toast.LENGTH_SHORT).show()
                                    }
                            }
                        }
                        .addOnFailureListener { exception ->
                            Toast.makeText(requireActivity(), "Failed to search for places: ${exception.message}", Toast.LENGTH_SHORT).show()
                        }
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })
    }

    private fun requestLocationPermission() {
        if (ActivityCompat.checkSelfPermission(
                requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(
                requireActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), REQUEST_CODE)
        } else {
            getLocation()
        }
    }

    private fun getLocation() {
        if (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d("LocationPermission", "Requesting Location Permissions")
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), REQUEST_CODE)
            return
        }

        Log.d("LocationStatus", "Permissions granted, fetching location...")
        fusedClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                Log.d("LocationStatus", "Location retrieved: Lat ${location.latitude}, Long ${location.longitude}")
                currentLocation = location
                if (this::gMap.isInitialized) {
                    updateMapLocation()
                }
            } else {
                Log.d("LocationStatus", "Location is null")
            }
        }.addOnFailureListener { exception ->
            Log.e("LocationError", "Failed to get location: ${exception.message}")
        }
    }


    private fun updateMapLocation() {
        if (currentLocation != null) {
            val latLng = LatLng(currentLocation!!.latitude, currentLocation!!.longitude)
            val geocoder = Geocoder(requireActivity(), Locale.getDefault())
            val addressList: List<Address>?
            var addressText = "My Current Location"
            try {
                addressList = geocoder.getFromLocation(currentLocation!!.latitude, currentLocation!!.longitude, 1)
                if (addressList != null && addressList.isNotEmpty()) {
                    addressText = addressList[0].getAddressLine(0)
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }

            val markerOptions = MarkerOptions().position(latLng).title(addressText).snippet(addressText)
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
            currentLocationMarker?.remove()
            currentLocationMarker = gMap.addMarker(markerOptions)
            gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18f))
        }
    }


    override fun onMapReady(googleMap: GoogleMap) {
        gMap = googleMap
        if (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            gMap.isMyLocationEnabled = true
            gMap.uiSettings.isMyLocationButtonEnabled = true
            fusedClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    currentLocation = location
                    updateMapLocation()
                }
            }
        }

        gMap.setOnMarkerClickListener { marker ->
            // Retrieve the title and snippet of the clicked marker
            val title = marker.title
            val snippet = marker.snippet

            // Display the title and snippet in a custom info window
            marker.showInfoWindow()

            // Return true to indicate that the click event has been handled
            true
        }

        gMap.setOnMapClickListener { clickedLatLng ->
            val geocoder = Geocoder(requireActivity(), Locale.getDefault())
            try {
                val addressList: List<Address>? = geocoder.getFromLocation(clickedLatLng.latitude, clickedLatLng.longitude, 1)
                if (addressList != null && addressList.isNotEmpty()) {
                    val address = addressList[0]
                    val addressText = address.getAddressLine(0)
                    otherLocationMarker?.remove()
                    val markerOptions = MarkerOptions().position(clickedLatLng).title(addressText).snippet(addressText)
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))

                    gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(clickedLatLng, 18f), object : GoogleMap.CancelableCallback {
                        override fun onFinish() {
                            otherLocationMarker = gMap.addMarker(markerOptions)
                        }

                        override fun onCancel() {}
                    })

                    Toast.makeText(requireActivity(), addressText, Toast.LENGTH_SHORT).show()
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getLocation()
        }
    }
}
