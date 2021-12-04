package com.ferodev.drawroutes2

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.ferodev.drawroutes2.databinding.ActivityMainBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions

class MainActivity : FragmentActivity(), OnMapReadyCallback {

    //google map object
    private var mMap: GoogleMap? = null

    var penjahit = LatLng(-7.7668127, 113.2259559)
    var pelanggan = LatLng(	-7.7563362, 113.2175488)
    var locationPermission = false
    val REQUEST_CODE = 101

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //request location permission.
        requestPermision()

        //init google map fragment to show map.
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)
    }

    private fun requestPermision() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), REQUEST_CODE)
        } else {
            locationPermission = true
        }
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            REQUEST_CODE -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    locationPermission = true
                }
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap!!.addMarker(MarkerOptions().position(penjahit).title("Lokasi Penjahit"))
        mMap!!.moveCamera(CameraUpdateFactory.newLatLng(penjahit))

        mMap!!.addMarker(MarkerOptions().position(pelanggan).title("Lokasi Pelanggan"))
        mMap!!.moveCamera(CameraUpdateFactory.newLatLng(pelanggan))

        mMap!!.addPolyline(
            PolylineOptions().add(penjahit, pelanggan)
                .width // below line is use to specify the width of poly line.
                    (5f) // below line is use to add color to our poly line.
                .color(Color.RED) // below line is to make our poly line geodesic.
                .geodesic(true)
        )
        // on below line we will be starting the drawing of polyline.
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(penjahit, 13f))
    }
}