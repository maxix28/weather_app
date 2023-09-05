package com.example.weatherapp.data

import com.example.weatherapp.data.forecastModels.Forecast
import com.example.weatherapp.data.models.CurrentWeather
import retrofit2.http.GET
import retrofit2.Response
import retrofit2.http.Query

interface ApiInterface {
@GET("weather?")
suspend fun getCurrentWeather(
    @Query("q") city: String,
    @Query("units") units: String,
    @Query("appid") apiKye: String,

    ):Response<CurrentWeather>

@GET("forecast?")
suspend fun getForecasr(
    @Query("q") city: String,
    @Query("units") units: String,
    @Query("appid") apiKye: String,

    ):Response<Forecast>
}