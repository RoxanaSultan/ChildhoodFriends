//package com.cst.cstacademy2024
//
//import android.Manifest
//import android.content.pm.PackageManager
//import android.location.Address
//import android.location.Geocoder
//import android.location.Location
//import android.os.Bundle
//import android.widget.FrameLayout
//import android.widget.Toast
//import androidx.appcompat.widget.SearchView
//import androidx.core.app.ActivityCompat
//import androidx.fragment.app.FragmentActivity
//import com.cst.cstacademy2024.R
//import com.google.android.gms.location.FusedLocationProviderClient
//import com.google.android.gms.location.LocationServices
//import com.google.android.gms.maps.CameraUpdateFactory
//import com.google.android.gms.maps.GoogleMap
//import com.google.android.gms.maps.OnMapReadyCallback
//import com.google.android.gms.maps.SupportMapFragment
//import com.google.android.gms.maps.model.BitmapDescriptorFactory
//import com.google.android.gms.maps.model.LatLng
//import com.google.android.gms.maps.model.Marker
//import com.google.android.gms.maps.model.MarkerOptions
//import com.google.android.gms.tasks.OnSuccessListener
//import com.google.android.gms.tasks.Task
//import java.io.IOException
//import java.util.Locale
//import com.google.android.libraries.places.api.Places
//import com.google.android.libraries.places.api.model.Place
//import com.google.android.libraries.places.api.net.FetchPlaceRequest
//import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
//import com.google.android.libraries.places.api.net.PlacesClient
//
//class MapsActivity : FragmentActivity(), OnMapReadyCallback {
//
//    private lateinit var map: FrameLayout
//    private lateinit var gMap: GoogleMap
//    private var currentLocation: Location? = null
//    private var currentLocationMarker: Marker? = null
//    private var otherLocationMarker: Marker? = null
//    private lateinit var fusedClient: FusedLocationProviderClient
//    private val REQUEST_CODE = 101
//    private lateinit var searchView: SearchView
//    private lateinit var placesClient: PlacesClient
//
//
//    /**
//     * Called when the activity is starting. Sets up the UI and initializes location services.
//     */
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_maps)
//
//        Places.initialize(applicationContext, getString(R.string.google_maps_key))
//        placesClient = Places.createClient(this)
//
//        map = findViewById(R.id.map)
//        searchView = findViewById(R.id.search)
//        searchView.clearFocus()
//
//        fusedClient = LocationServices.getFusedLocationProviderClient(this)
//        requestLocationPermission()
//
//        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
//            override fun onQueryTextSubmit(query: String?): Boolean {
//                val loc = searchView.query.toString()
//                if (loc.isEmpty()) {
//                    Toast.makeText(this@MapsActivity, "Location Not Found", Toast.LENGTH_SHORT).show()
//                } else {
//                    // Use Places API to search for places
//                    val fields = listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG)
//                    val request = FindAutocompletePredictionsRequest.builder()
//                        .setQuery(loc)
//                        .build()
//
//                    placesClient.findAutocompletePredictions(request)
//                        .addOnSuccessListener { response ->
//                            val prediction = response.autocompletePredictions.firstOrNull()
//                            prediction?.let {
//                                val placeId = it.placeId
//                                val placeFieldsRequest = FetchPlaceRequest.builder(placeId, fields).build()
//
//                                placesClient.fetchPlace(placeFieldsRequest)
//                                    .addOnSuccessListener { fetchPlaceResponse ->
//                                        val place = fetchPlaceResponse.place
//                                        val latLng = place.latLng
//                                        val placeName = place.name
//                                        val placeAddress = place.address
//
//                                        if (latLng != null) {
//                                            otherLocationMarker?.remove()
//                                            val markerOptions = MarkerOptions().position(latLng).title(placeName).snippet(placeAddress)
//                                            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
//
//                                            gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18f), object : GoogleMap.CancelableCallback {
//                                                override fun onFinish() {
//                                                    otherLocationMarker = gMap.addMarker(markerOptions)
//                                                }
//
//                                                override fun onCancel() {}
//                                            })
//                                        } else {
//                                            Toast.makeText(this@MapsActivity, "Location Not Found", Toast.LENGTH_SHORT).show()
//                                        }
//                                    }
//                                    .addOnFailureListener { exception ->
//                                        Toast.makeText(this@MapsActivity, "Failed to fetch place details: ${exception.message}", Toast.LENGTH_SHORT).show()
//                                    }
//                            }
//                        }
//                        .addOnFailureListener { exception ->
//                            Toast.makeText(this@MapsActivity, "Failed to search for places: ${exception.message}", Toast.LENGTH_SHORT).show()
//                        }
//                }
//                return false
//            }
//
//            override fun onQueryTextChange(newText: String?): Boolean {
//                return false
//            }
//        })
//    }
//
//    /**
//     * Requests location permissions if not already granted.
//     */
//    private fun requestLocationPermission() {
//        if (ActivityCompat.checkSelfPermission(
//                this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
//            && ActivityCompat.checkSelfPermission(
//                this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), REQUEST_CODE)
//        } else {
//            getLocation()
//        }
//    }
//
//    /**
//     * Retrieves the current location of the device.
//     */
//    private fun getLocation() {
//        if (ActivityCompat.checkSelfPermission(
//                this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//            val task: Task<Location> = fusedClient.lastLocation
//
//            task.addOnSuccessListener(OnSuccessListener { location ->
//                if (location != null) {
//                    currentLocation = location
//                    val supportMapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
//                    supportMapFragment?.getMapAsync(this@MapsActivity)
//                }
//            })
//        }
//    }
//
//    /**
//     * Called when the map is ready to be used. Sets the map to the current location and adds a marker.
//     */
//    override fun onMapReady(googleMap: GoogleMap) {
//        gMap = googleMap
//        if (currentLocation != null) {
//            val latLng = LatLng(currentLocation!!.latitude, currentLocation!!.longitude)
//            val geocoder = Geocoder(this, Locale.getDefault())
//            val addressList: List<Address>?
//            var addressText = "My Current Location"
//            try {
//                addressList = geocoder.getFromLocation(currentLocation!!.latitude, currentLocation!!.longitude, 1)
//                if (addressList != null && addressList.isNotEmpty()) {
//                    addressText = addressList[0].getAddressLine(0)
//                }
//            } catch (e: IOException) {
//                e.printStackTrace()
//            }
//
//            val markerOptions = MarkerOptions().position(latLng).title(addressText)
//            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
//            currentLocationMarker = gMap.addMarker(markerOptions)
//            gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18f))
//        }
//
//        gMap.setOnMapClickListener { clickedLatLng ->
//            val geocoder = Geocoder(this, Locale.getDefault())
//            try {
//                val addressList: List<Address>? = geocoder.getFromLocation(clickedLatLng.latitude, clickedLatLng.longitude, 1)
//                if (addressList != null && addressList.isNotEmpty()) {
//                    val address = addressList[0]
//                    val addressText = address.getAddressLine(0)
//                    otherLocationMarker?.remove()
//                    val markerOptions = MarkerOptions().position(clickedLatLng).title(addressText)
//                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
//
//                    gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(clickedLatLng, 18f), object : GoogleMap.CancelableCallback {
//                        override fun onFinish() {
//                            otherLocationMarker = gMap.addMarker(markerOptions)
//                        }
//
//                        override fun onCancel() {}
//                    })
//
//                    Toast.makeText(this, addressText, Toast.LENGTH_SHORT).show()
//                }
//            } catch (e: IOException) {
//                e.printStackTrace()
//            }
//        }
//    }
//
//    /**
//     * Handles the result of the location permission request.
//     */
//    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        if (requestCode == REQUEST_CODE) {
//            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                getLocation()
//            } else {
//                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
//            }
//        }
//    }
//}