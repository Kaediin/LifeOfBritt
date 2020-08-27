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
        val locationStart = Location("Riddle here", "", LatLng(52.3854005,5.249031), "Start")
        val locationOptimist = Location("Riddle here", "n", LatLng(52.3727102, 5.2596209), "Optimist II")
        val locationHP = Location("Riddle here", "I", LatLng(52.374923, 5.2368295), "HP")
        val locationSanne = Location("Riddle here", " ", LatLng(52.3862821, 5.266027), "Sanne")
        val locationFBK = Location("Riddle here", "o", LatLng(52.3891981, 5.2434644), "FBK")
        val locationEnola = Location("Riddle here", "b", LatLng(52.3725008, 5.2592671), "Enola")
        val locationGhetto = Location("Riddle here", "K", LatLng(52.3694635, 5.2776559), "Ghetto")
        val locationAmanda = Location("Riddle here", "e", LatLng(52.385448,5.2491612), "Amanda")
        val locationKippie = Location("Riddle here", "a", LatLng(52.37103, 5.2167776), "Kippie")
        val locationROC = Location("Riddle here", "k", LatLng(52.343222, 5.1542363), "ROC")
        val locationJesse = Location("Riddle here", "f", LatLng(52.3705894, 5.2279484), "Jesse")
        val locationBios = Location("Riddle here", "r", LatLng(52.3687107, 5.219526), "Bios")
        val locationBritt = Location("Riddle here", "f", LatLng(52.3854005,5.249031), "Britt")

        list.add(locationStart)
        list.add(locationOptimist)
        list.add(locationHP)
        list.add(locationSanne)
        list.add(locationFBK)
        list.add(locationEnola)
        list.add(locationGhetto)
        list.add(locationAmanda)
        list.add(locationKippie)
        list.add(locationROC)
        list.add(locationJesse)
        list.add(locationBios)
        list.add(locationBritt)
        return list
    }
}