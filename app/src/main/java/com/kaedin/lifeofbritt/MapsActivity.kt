package com.kaedin.lifeofbritt

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Build
import com.google.android.gms.location.LocationListener
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_maps.*
import kotlinx.android.synthetic.main.dialog_riddle.view.*
import java.lang.IndexOutOfBoundsException
import java.text.DecimalFormat
import java.util.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private lateinit var mMap: GoogleMap
    private var dialog: AlertDialog? = null
    private val riddles = Data.getAllLocations()
    private var currentRiddle = Location()
    private var currentIteration: Int = 0
    private var googleApiClient: GoogleApiClient? = null
    private var isCompleted: Boolean = false
    private var popupContext: View? = null
    private var sp: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        supportActionBar!!.hide()
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        googleApiClient = GoogleApiClient.Builder(applicationContext).addApi(LocationServices.API)
            .addConnectionCallbacks(this).addOnConnectionFailedListener(this).build()

        sp = getSharedPreferences("progress", Context.MODE_PRIVATE)
//        sp!!.edit().putInt("currentIteration", 5).apply() //FOR HARDCODING THE STAGE
        currentIteration = sp!!.getInt("currentIteration", 0)
        isCompleted = sp!!.getBoolean("isCompleted", false)

        if (isCompleted){
            startActivity(Intent(this, DoneActivity::class.java))
        } else {

            startup()
        }



    }

    fun startup(){
        requestPerms()
        buildDialogRiddle()

        image_riddle_button.setOnClickListener {
            if (!dialog!!.isShowing) {
                dialog!!.show()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if (googleApiClient != null && !isCompleted) {
            googleApiClient!!.connect()
        }
    }

    override fun onPause() {
        super.onPause()
        if (googleApiClient != null && googleApiClient!!.isConnected) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this)
            googleApiClient!!.disconnect()
        }
    }

    private fun startLocationUpdates() {
        val locationRequest = LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 1000
        locationRequest.fastestInterval = 1000
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Toast.makeText(
                this,
                "You need to enable permissions to display your location!",
                Toast.LENGTH_SHORT
            ).show()
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(
            googleApiClient,
            locationRequest,
            this
        )
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
    }

    override fun onLocationChanged(location: Location) {
        val latLng = LatLng(location.latitude, location.longitude)
        mMap.clear()
        mMap.addMarker(MarkerOptions().position(latLng))
        tv_coords.text = latLng.toString()

        if (isOnLocation(location)) {
            goToNextLocation()
        }
    }

    fun goToNextLocation() {
        if (currentIteration == 0){
            val startTime = Calendar.getInstance().timeInMillis
            sp!!.edit().putLong("start_time", startTime).apply()
        }
        currentIteration++
        showDialog()
        sp!!.edit().putInt("currentIteration", currentIteration).apply()

        if (image_riddle_button.visibility == View.GONE) {
            image_riddle_button.visibility = View.VISIBLE
        }
    }

    fun showDialog() {
        if (dialog!!.isShowing) {
            dialog!!.dismiss()
        }
        try {
            currentRiddle = riddles[currentIteration]
            popupContext!!.riddle.text = currentRiddle.name
            vibratePhone()
            dialog!!.show()
        } catch (e: IndexOutOfBoundsException) {
            goToCredits()
        }
    }

    fun goToCredits(){
        sp!!.edit().putBoolean("isCompleted", true).apply()
        sp!!.edit().putLong("end_time", Calendar.getInstance().timeInMillis).apply()
        LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this)
        googleApiClient!!.disconnect()
        val i = Intent(this, DoneActivity::class.java)
        startActivity(i)
    }

    fun isOnLocation(location: Location): Boolean {

        val locCheck = Location(LocationManager.GPS_PROVIDER)
        locCheck.latitude = riddles[currentIteration].coordinates!!.latitude
        locCheck.longitude = riddles[currentIteration].coordinates!!.longitude
        val distanceInM = location.distanceTo(locCheck)
        println(distanceInM)
        return distanceInM < 20
    }

    override fun onConnected(p0: Bundle?) {
        val location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient)
        setupMap(location)
        startLocationUpdates()
    }

    fun setupMap(location: Location) {
        val latLng = LatLng(location.latitude, location.longitude)
        val markerOptions = MarkerOptions().position(latLng)
        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng))
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
        mMap.addMarker(markerOptions)
    }

    fun vibratePhone() {
        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= 26) {
            vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            vibrator.vibrate(500)
        }
    }

    fun buildDialogRiddle() {
        val builder = AlertDialog.Builder(this)
        val layoutInflater = LayoutInflater.from(this)
        popupContext = layoutInflater.inflate(R.layout.dialog_riddle, null)
        builder.setView(popupContext)
        dialog = builder.create()
    }

    fun requestPerms() {
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) !=
            PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION
            ) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 200
            )
            return
        }
    }

    override fun onConnectionSuspended(p0: Int) {
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
    }
}
