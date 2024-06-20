package com.example.doglist

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Url

interface APIService {
    @GET
    //Se le pone un Suspend porque es una corutina
    suspend fun getDogsByBreeds(@Url url:String):Response<DogsResponse>
}