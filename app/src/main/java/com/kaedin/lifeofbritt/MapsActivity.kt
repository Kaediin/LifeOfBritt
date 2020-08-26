package com.kaedin.lifeofbritt

import android.Manifest
import android.app.PendingIntent.getActivity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Color
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
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_maps.*
import kotlinx.android.synthetic.main.activity_maps.view.*
import kotlinx.android.synthetic.main.dialog_guess_word.view.*
import kotlinx.android.synthetic.main.dialog_riddle.view.*
import java.lang.Exception
import java.lang.IndexOutOfBoundsException
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
    private var isDone: Boolean = false
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
        currentIteration = sp!!.getInt("currentIteration", 0)
        isCompleted = sp!!.getBoolean("isCompleted", false)
        isDone = sp!!.getBoolean("isDone", false)

        when {
            isCompleted -> {
                startActivity(Intent(this, DoneActivity::class.java))
            }
            isDone -> {
                guessWord()
            }
            else -> {
                startup()
            }
        }
    }

    override fun onLocationChanged(location: Location) {
        if (!isDone) {
            val latLng = LatLng(location.latitude, location.longitude)
            mMap.clear()
            mMap.addMarker(MarkerOptions().position(latLng))
            tv_coords.text = latLng.toString()

            if (isOnLocation(location)) {
                goToNextLocation()
            }
        }
    }

    fun startup() {
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


    fun goToNextLocation() {
        if (currentIteration == 0) {
            val startTime = Calendar.getInstance().timeInMillis
            sp!!.edit().putLong("start_time", startTime).apply()
        }
        appendLetter(riddles[currentIteration].letter!!)
        popupContext!!.lettersTV.text = sp!!.getString("letters", "")
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
            guessWord()
        }
    }

    fun guessWord() {
        try {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this)
            googleApiClient!!.disconnect()
        } catch (ignored: Exception) {
        }

        val builder = AlertDialog.Builder(this)
        val layoutInflater = LayoutInflater.from(this)
        val guessContext = layoutInflater.inflate(R.layout.dialog_guess_word, null)
        builder.setView(guessContext)
        builder.setCancelable(false)
        val dialogGuess = builder.create()
        sp!!.edit().putBoolean("isDone", true).apply()
        guess_word_button.apply {
            this.setBackgroundColor(resources.getColor(R.color.colorPrimaryDark))
            isEnabled = true
            setOnClickListener {
                guessContext.letters_in_dialog.text = sp!!.getString("letters", "")
                dialogGuess.show()
                guessContext.guess_word_in_dialog_button.setOnClickListener {
                    val word = guessContext.et_letters.text.toString()
                    if (word.toLowerCase(Locale.ROOT) == "in kofferbak") {
                        goToCredits()
                    } else {
                        createSnackbar(it, "Fout...")
//                        Toast.makeText(applicationContext, "Fout...", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    fun createSnackbar(view: View, msg: String){
        Snackbar.make(view, msg, Snackbar.LENGTH_SHORT).show()
    }


    fun goToCredits(){
        sp!!.edit().putLong("end_time", Calendar.getInstance().timeInMillis).apply()
        sp!!.edit().putBoolean("isCompleted", true).apply()
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
        if (location != null) {
            setupMap(location)
            startLocationUpdates()
        } else {
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
        }
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

        popupContext!!.lettersTV.text = sp!!.getString("letters", "")
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

    fun appendLetter(letter: String) {
        var letters = sp!!.getString("letters", "")
        letters += letter
        sp!!.edit().putString("letters", letters).apply()
    }

    override fun onConnectionSuspended(p0: Int) {
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
    }
}

