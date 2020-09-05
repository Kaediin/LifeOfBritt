package com.kaedin.lifeofbritt

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.google.android.gms.maps.model.LatLng

object Data {

    fun resizeMapIcons(context: Context, iconName: String?): Bitmap {
        val imageBitmap = BitmapFactory.decodeResource(context.resources, context.resources.getIdentifier(iconName, "drawable", context.packageName))
        return Bitmap.createScaledBitmap(imageBitmap, 65, 110, false)
    }

//    var letters = ""

    fun getAllLocations(): MutableList<Location>{
        // My home location = 52.37897135914867, 5.2356307581067085
        val list = ArrayList<Location>()
        val locationStart = Location("Start", "", LatLng(52.3854005,5.249031), "Start")
        val locationOptimist = Location("Waar het allemaal begon met BRITTney Spears", "n", LatLng(52.3727102, 5.2596209), "Optimist II")
        val locationHP = Location("Leren is moeilijk als de bueno niet goed valt", "I", LatLng(52.374923, 5.2368295), "HP")
        val locationSanne = Location("Goeie tip: vergeet je ID niet in zeeland...", " r", LatLng(52.3862821, 5.266027), "Sanne")
        val locationDansen = Location("Priscilla, the show must go on!", "o", LatLng(52.4019705, 5.2987081), "Dansen")// Dansen hier
//        val locationFBK = Location("Riddle here", "o", LatLng(52.3891981, 5.2434644), "FBK")// Dansen hier
        val locationEnola = Location("Broodje pitta gyrpos vlees met tzatziki saus", "b", LatLng(52.3725008, 5.2592671), "Enola")
        val locationGhetto = Location("Waar affaires plaats zijn gevonden...", "K", LatLng(52.3694635, 5.2776559), "Ghetto")
        val locationAmanda = Location("Chica's en chico", "e", LatLng(52.385448,5.2491612), "Amanda")// Pauze
        val locationKippie = Location("Wat kwam eerst, de kip of het ei?", "a", LatLng(52.37103, 5.2167776), "Kippie")
        val locationROC = Location("De POORT naar je toekomst", "k", LatLng(52.343222, 5.1542363), "ROC")
        val locationJesse = Location("Van LA naar Hollywood", "f", LatLng(52.3705894, 5.2279484), "Jesse")
//        val locationBios = Location("", "r", LatLng(52.3687107, 5.219526), "Bios")
        val locationBritt = Location("Zoals ze zeggen: 'Oost west ..... ....'", "f", LatLng(52.3854005,5.249031), "Britt")

        list.add(locationStart)
        list.add(locationOptimist)
        list.add(locationHP)
        list.add(locationSanne)
        list.add(locationDansen)
        list.add(locationEnola)
        list.add(locationGhetto)
        list.add(locationAmanda)
        list.add(locationKippie)
        list.add(locationROC)
        list.add(locationJesse)
//        list.add(locationBios)
        list.add(locationBritt)
        return list
    }
}