package com.kaedin.lifeofbritt

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_done.*
import java.util.*
import kotlin.math.floor

class DoneActivity : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_done)
        val sp = getSharedPreferences("progress", Context.MODE_PRIVATE)
        val startTime = sp.getLong("start_time", 0)
        val endTime = sp.getLong("end_time", 0)
        val millis = (endTime - startTime).toDouble()

        val seconds = floor((millis/ 1000 ) % 60).toInt()
        val minutes = floor((millis / (1000 * 60)) % 60).toInt()
        val hours = floor((millis / (1000 * 60 * 60)) % 24).toInt()

        var hourText = "uren"
        if (hours < 2) hourText = "uur"

        totalTimeTV.text = "$hours $hourText\n$minutes minuten \n$seconds seconden"

        opnieuw_spelen_button.setOnClickListener {
            sp.edit().putBoolean("isCompleted", false).apply()
            sp.edit().putInt("currentIteration", 0).apply()
            startActivity(Intent(this, MapsActivity::class.java))
        }
    }
}