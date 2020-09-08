package com.example.engeselt_desafio.data.model

class FormLocalUpdateDelete{
    var id : String = ""
    var namePoint : String = ""
    var latitudePoint : Double = 0.00
    var longitudePoint : Double = 0.00

    constructor(id: String, namePoint:String, latitudePoint:Double, longitudePoint:Double){
        this.id = id
        this.namePoint = namePoint
        this.latitudePoint = latitudePoint
        this.longitudePoint = longitudePoint
    }
}