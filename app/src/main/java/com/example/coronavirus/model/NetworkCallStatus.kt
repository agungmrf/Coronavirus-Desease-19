package com.example.coronavirus.model

data class NetworkCallStatus(
        var status : Boolean,
        var errMsg : String,
        var responseCode : Int
)
