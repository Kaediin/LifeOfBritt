package com.kaedin.lifeofbritt

import com.google.android.gms.maps.model.LatLng

class Location {
    var riddle: String? = null
    var letter: String? = null
    var coordinates: LatLng? = null
    var name: String? = null

    constructor(){}
    constructor(
        riddle: String?,
        letter: String?,
        coordinates: LatLng?,
        name: String?
    ) {
        this.riddle = riddle
        this.letter = letter
        this.coordinates = coordinates
        this.name = name
    }
}