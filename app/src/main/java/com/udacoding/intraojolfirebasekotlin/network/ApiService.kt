package com.udacoding.intraojolfirebasekotlin.network

import com.udacoding.intraojolfirebasekotlin.utama.home.model.ResultRoute
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query


interface ApiService {

    @GET("json")
    fun route(
        @Query("origin") origin: String,
        @Query("destination") destination: String,
        @Query("key")string: String
    ) : Call<ResultRoute>
}