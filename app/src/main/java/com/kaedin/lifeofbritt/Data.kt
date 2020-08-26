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
        val list = ArrayList<Location>()
        val locationStart = Location("Riddle here", "", LatLng(52.37897135914867, 5.2356307581067085), "Start")
        val locationOptimist = Location("Riddle here", "n", LatLng(52.37897135914867, 5.2356307581067085), "Optimist II")
        val locationHP = Location("Riddle here", "I", LatLng(52.37897135914867, 5.2356307581067085), "HP")
        val locationSanne = Location("Riddle here", " ", LatLng(52.37897135914867, 5.2356307581067085), "Sanne")
        val locationFBK = Location("Riddle here", "o", LatLng(52.37897135914867, 5.2356307581067085), "FBK")
        val locationEnola = Location("Riddle here", "b", LatLng(52.37897135914867, 5.2356307581067085), "Enola")
        val locationGhetto = Location("Riddle here", "K", LatLng(52.37897135914867, 5.2356307581067085), "Ghetto")
        val locationAmanda = Location("Riddle here", "e", LatLng(52.37897135914867, 5.2356307581067085), "Amanda")
        val locationKippie = Location("Riddle here", "a", LatLng(52.37897135914867, 5.2356307581067085), "Kippie")
        val locationROC = Location("Riddle here", "k", LatLng(52.37897135914867, 5.2356307581067085), "ROC")
        val locationJesse = Location("Riddle here", "f", LatLng(52.37897135914867, 5.2356307581067085), "Jesse")
        val locationBios = Location("Riddle here", "r", LatLng(52.37897135914867, 5.2356307581067085), "Bios")
        val locationBritt = Location("Riddle here", "f", LatLng(52.37897135914867, 5.2356307581067085), "Britt")

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