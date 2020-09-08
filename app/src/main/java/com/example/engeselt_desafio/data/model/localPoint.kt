package com.example.engeselt_desafio.data.model

import android.graphics.Bitmap

class FormLocal{
    var id : Int = 0
    var namePoint : String = ""
    var latitudePoint : Double = 0.00
    var longitudePoint : Double = 0.00

    constructor(namePoint:String, latitudePoint:Double, longitudePoint:Double){
        this.namePoint = namePoint
        this.latitudePoint = latitudePoint
        this.longitudePoint = longitudePoint
    }

    constructor(){
    }

}