package com.cst.cstacademy2024

import androidx.annotation.NonNull
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
import android.Manifest
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.Toast
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdate
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
import java.io.IOException
import java.util.Locale


class MapsActivity : FragmentActivity(), OnMapReadyCallback {
    lateinit var map: FrameLayout
    lateinit var gMap: GoogleMap
    lateinit var currentLocation: Location
    lateinit var marker: Marker
    lateinit var fusedClient: FusedLocationProviderClient
    private companion object {
        const val REQUEST_CODE = 101
    }
    lateinit var searchView: SearchView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        map = findViewById(R.id.map)
        searchView = findViewById(R.id.search)
        searchView.clearFocus()
        fusedClient = LocationServices.getFusedLocationProviderClient(this)
        getLocation()
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                val loc = searchView.query.toString()
                if (loc == null) {
                    Toast.makeText(this@MapsActivity, "Location Not Found", Toast.LENGTH_SHORT).show()
                } else {
                    val geocoder = Geocoder(this@MapsActivity, Locale.getDefault())
                    try {
                        val addressList = geocoder.getFromLocationName(loc, 1)
                        if (!addressList.isNullOrEmpty()) {
                            val latLng = LatLng(addressList[0].latitude, addressList[0].longitude)
                            marker?.remove()
                            val markerOptions = MarkerOptions().position(latLng).title(loc)
                            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                            val cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 5f)
                            gMap.animateCamera(cameraUpdate)
                            var markernull = gMap.addMarker(markerOptions)
                            if(markernull != null){
                                marker = markernull
                            }
                            //marker = gMap.addMarker(markerOptions)
                        }

                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }
        })
    }

    private fun getLocation() {
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_CODE)
            return
        }
        val task: Task<Location> = fusedClient.lastLocation
        task.addOnSuccessListener { location ->
            if (location != null) {
                currentLocation = location
                //Toast.makeText(applicationContext, currentLocation.latitude.toString() + "" + currentLocation.longitude, Toast.LENGTH_SHORT).show()
                val supportMapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
                supportMapFragment?.getMapAsync(this@MapsActivity)
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        gMap = googleMap
        val latLng = LatLng(currentLocation.latitude, currentLocation.longitude)
        val markerOptions = MarkerOptions().position(latLng).title("My Current Location")
        googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng))
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 5f))
        googleMap.addMarker(markerOptions)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocation()
            }
        }
    }

}