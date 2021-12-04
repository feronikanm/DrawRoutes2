package com.ferodev.drawroutes2

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Geocoder
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.directions.route.*
import com.ferodev.drawroutes2.databinding.ActivityMain2Binding
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import java.util.*

class MainActivity2 : FragmentActivity(), OnMapReadyCallback {

    private lateinit var binding: ActivityMain2Binding
    private var mMap: GoogleMap? = null
    private lateinit var currentLocation : Location
    private val LOCATION_REQUEST_CODE = 10
    private lateinit var fusedLocationProviderClient : FusedLocationProviderClient
    var myLocation: Location? = null
    var locationPermission = false

    var penjahit = LatLng(-7.7668127, 113.2259559)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMain2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        //request location permission.
        requestPermision()
    }

    private fun requestPermision() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), LOCATION_REQUEST_CODE)
        } else {
            locationPermission = true
        }

        val task = fusedLocationProviderClient.lastLocation
        task.addOnSuccessListener { location ->
            if (location != null){
                currentLocation = location

                //init google map fragment to show map.
                val supportMapFragment = (supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment)
                supportMapFragment.getMapAsync(this)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            LOCATION_REQUEST_CODE -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    locationPermission = true
                    getMyLocation()
                }
                return
            }
        }
    }

    private fun getMyLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        mMap!!.isMyLocationEnabled = true



        mMap!!.setOnMyLocationChangeListener { location ->
            myLocation = location
            val latlng = LatLng(location.latitude, location.longitude)
            val markerOptions = MarkerOptions().position(latlng).title("I am Here!")
                .snippet(getAddress(myLocation!!.latitude, myLocation!!.longitude)).draggable(true)
            mMap!!.addMarker(markerOptions)

            mMap!!.addMarker(MarkerOptions().position(penjahit).title("Lokasi Penjahit"))
            mMap!!.moveCamera(CameraUpdateFactory.newLatLng(penjahit))

            mMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng, 15f))

            mMap!!.addPolyline(
                PolylineOptions().add(penjahit, latlng)
                    .width // below line is use to specify the width of poly line.
                        (5f) // below line is use to add color to our poly line.
                    .color(Color.RED) // below line is to make our poly line geodesic.
                    .geodesic(true)
            )
        }

    }

    private fun getAddress(latitude: Double, longitude: Double): String? {
        val geoCoder = Geocoder(this, Locale.getDefault())
        val addresses = geoCoder.getFromLocation(latitude, longitude, 1)
        return addresses[0].getAddressLine(0).toString()
    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        getMyLocation()
    }


}