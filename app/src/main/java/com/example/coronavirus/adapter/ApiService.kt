package com.example.coronavirus.adapter

import com.example.coronavirus.model.Summary
import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.GET

interface ApiService {
    @GET("summary")
    fun getSummary(): Deferred<Response<Summary?>>
}