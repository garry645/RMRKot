package com.garry.rmrkot

data class Car(
    var owner: Owner? = null,
    var make: String? = null,
    var model: String? = null,
    var year: Int? = null,
    var dbID: String? = null,
    var currentCar: Int = 0,
    var carImageUrls: MutableList<String>? = null,
    var totalRating: Float? = null,
    var posRatings: Float? = null,
    var numOfRatings: Float? = null
) {

    fun deleteImageUrl(position: Int) {
        carImageUrls?.removeAt(position)
    }

    fun addImageUrl(position: Int, urlIn: String) {
        if(carImageUrls?.size!! < 10) {
            if (carImageUrls?.size == position) {
                carImageUrls?.add(urlIn)
            } else {
                carImageUrls?.set(position, urlIn)
            }
        }
    }
}