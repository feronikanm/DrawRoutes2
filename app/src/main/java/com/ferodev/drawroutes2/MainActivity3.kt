package com.ferodev.drawroutes2

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Geocoder
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.ferodev.drawroutes2.databinding.ActivityMain3Binding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import java.util.*

class MainActivity3 : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var binding: ActivityMain3Binding
    private lateinit var currentLocation : Location
    private var mMap: GoogleMap? = null
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    val REQUEST_CODE = 101

    var penjahit = LatLng(-7.7668127, 113.2259559)
    var penjahitLat = -7.7668127
    var penjahitLng = 113.2259559


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMain3Binding.inflate(layoutInflater)
        setContentView(binding.root)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        fetchLocation()

    }

    private fun fetchLocation() {

        if(ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_CODE)
//            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), REQUEST_CODE)
            return
        }

        val task = fusedLocationProviderClient.lastLocation
        task.addOnSuccessListener { location ->
            if (location != null){
                currentLocation = location
                val supportMapFragment = (supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment)
                supportMapFragment.getMapAsync(this)
            }
        }

    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        getMyLocation()
    }

    private fun getMyLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        mMap!!.isMyLocationEnabled = true

        mMap!!.uiSettings.isZoomControlsEnabled = true


//        val latLng = LatLng(currentLocation.latitude, currentLocation.longitude)
//        val markerPelanggan = MarkerOptions().position(latLng).title("Saya disini")
//            .snippet(getAddress(currentLocation.latitude, currentLocation.longitude))
//        mMap!!.animateCamera(CameraUpdateFactory.newLatLng(latLng))
//        mMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
//        mMap!!.addMarker(markerPelanggan)

        mMap!!.setOnMyLocationChangeListener { location ->
            currentLocation = location
            val latlng = LatLng(location.latitude, location.longitude)
            val markerOptions = MarkerOptions().position(latlng).title("Saya disini")
                .snippet(getAddress(currentLocation.latitude, currentLocation.longitude))
            mMap!!.addMarker(markerOptions)

            val markerPenjahit = MarkerOptions().position(penjahit).title("Lokasi Penjahit")
                .snippet(getAddress(penjahitLat, penjahitLng))
            mMap!!.addMarker(markerPenjahit)
//            mMap!!.moveCamera(CameraUpdateFactory.newLatLng(penjahit))

            mMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng, 15f))

            mMap!!.addPolyline(
                PolylineOptions().add(penjahit, latlng)
                    .width // below line is use to specify the width of poly line.
                        (10f) // below line is use to add color to our poly line.
                    .color(Color.BLUE) // below line is to make our poly line geodesic.
                    .geodesic(true)
            )
        }

    }

    private fun getAddress(latitude: Double, longitude: Double): String? {
        val geoCoder = Geocoder(this, Locale.getDefault())
        val addresses = geoCoder.getFromLocation(latitude, longitude, 1)
        return addresses[0].getAddressLine(0).toString()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            REQUEST_CODE -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    fetchLocation()
                }
            }
        }

    }

}