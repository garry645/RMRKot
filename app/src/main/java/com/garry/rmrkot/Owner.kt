package com.garry.rmrkot

data class Owner(
        var email: String = "",
        var username: String = "",
        var raterScore: Int = 1,
        var carsOwned: Int = 1,
        var carSlots: Int = 30,
        var totalRatings: Int = 0
)