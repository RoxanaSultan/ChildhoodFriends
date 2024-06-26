package com.cst.cstacademy2024

import android.Manifest
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.cst.cstacademy2024.models.PlaceUser
import com.cst.cstacademy2024.models.User
import com.cst.cstacademy2024.viewModels.PlaceUserViewModel
import com.cst.cstacademy2024.viewModels.PlaceViewModel
import com.cst.cstacademy2024.viewModels.SharedViewModel
import com.cst.cstacademy2024.viewModels.UserViewModel
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
import com.cst.cstacademy2024.models.Place as MyPlace
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
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
    private lateinit var userViewModel: UserViewModel
    private lateinit var placeViewModel: PlaceViewModel
    private lateinit var placeUserViewModel: PlaceUserViewModel
    private lateinit var viewModel: SharedViewModel
    private var user: User? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_maps, container, false)

        viewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)

        viewModel.user.observe(viewLifecycleOwner) { userVM ->
            // Update your UI here with user information
            // For example: textView.text = user.name
            user = userVM
        }

//        val args = MapsFragmentArgs.fromBundle(requireArguments())
//        user = args.user


        placeViewModel = ViewModelProvider(this).get(PlaceViewModel::class.java)
        placeUserViewModel = ViewModelProvider(this).get(PlaceUserViewModel::class.java)
        // Get the current user
        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)

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
        view.findViewById<Button>(R.id.btnAddLocation).setOnClickListener {
            showCategorySelectionDialog()
        }
    }

    private fun showCategorySelectionDialog() {
        val categories = arrayOf("Addresses", "Schools", "High Schools", "Colleges", "Favourite Places")

        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Select Category")
        builder.setItems(categories) { dialog, which ->
            val selectedCategory = categories[which]
            val marker = if (otherLocationMarker != null) otherLocationMarker else currentLocationMarker

            if (marker != null) {
                // Get the location details
                val placeName = marker?.title ?: "Unknown Place"
                val latLng = marker?.position
                val location = "${latLng?.latitude},${latLng?.longitude}"

                // Create a Place object
                val place = MyPlace(
                    name = placeName,
                    location = location
                )

                //Insert the Place object into the database and get the generated ID
                lifecycleScope.launch {
                    placeViewModel.getPlace(placeName, location).observe(viewLifecycleOwner, Observer { placeFound ->
                        if (placeFound == null) {
                            placeViewModel.insertPlace(place)
                        }
                    })



                    delay(500)
                    
                    val placeIdLiveData = placeViewModel.getPlace(placeName, location)
                    placeIdLiveData.observe(viewLifecycleOwner) { placeId ->
                        placeIdLiveData.removeObservers(viewLifecycleOwner) // Remove observer after getting value

                        if (placeId != null && placeId > 0) {
                            // Create a PlaceUser object with the correct placeId
                            //val user = arguments?.getSerializable("USER") as? User
                            if (user != null) {
                                val placeUser = PlaceUser(
                                    placeId = placeId,
                                    userId = user!!.id,
                                    category = selectedCategory
                                )
                                placeUserViewModel.getPlaceUser(placeId, user!!.id).observe(viewLifecycleOwner, Observer { placeFound ->
                                    if (placeFound != null) {
                                        Toast.makeText(
                                            requireContext(),
                                            "Location already exists.",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    } else {
                                        placeUserViewModel.insertPlaceUser(placeUser)
                                        Toast.makeText(
                                            requireContext(),
                                            "Location added successfully.",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                })

                            } else {
                                Toast.makeText(
                                    requireContext(),
                                    "User information is missing.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else {
                            Toast.makeText(
                                requireContext(),
                                "Error adding location. Place could not be inserted.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }

            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
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
