package com.example.weatherapp

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.weatherapp.databinding.ActivityMainBinding
import com.example.weatherapp.utils.RetrofitInstance
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    lateinit var  binding : ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
         binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getCurrentWeather()
    }

    @SuppressLint("SetTextI18n")
    private fun getCurrentWeather() {
       GlobalScope.launch (Dispatchers.IO){

           val response = try{
RetrofitInstance.api.getCurrentWeather("new york", "metric","32975a8757688ffdfb49c7c73266659d")
           }catch (e:IOException){
               Toast.makeText(applicationContext,"app error ${e.message}",Toast.LENGTH_SHORT).show()
               return@launch

           }catch (e :HttpException){
               Toast.makeText(applicationContext,"http error ${e.message}",Toast.LENGTH_SHORT).show()
return@launch
           }


           if( response.isSuccessful&& response.body()!= null){
               withContext((Dispatchers.Main)){

val data = response.body()!!
                   val iconid = data.weather[0].icon
                   val imgUrl="https://openweathermap.org/img/wn/${iconid}.png"
                   Picasso.get().load(imgUrl).into(binding.imgWeather)


                   binding.tvSunrise.text=SimpleDateFormat(
                       "hh:mm a",
                       Locale.ENGLISH
                   ).format(data.sys.sunrise * 1000)


                   binding.tvSunset.text=SimpleDateFormat(
                       "hh:mm a",
                       Locale.ENGLISH
                   ).format(data.sys.sunset * 1000)


                   binding.apply {

                       tvStatus.text= data.weather[0].description
                       tvWind.text="${data.wind.speed.toString()} KM/H"
                       tvLocation.text="${data.name}"
                       tvTemp.text=" ${data.main.temp.toInt()}° C"
                        tvFeelsLike.text = "Feels like: ${data.main.feels_like.toInt()}° C"
                       tvMinTemp.text="Min tem: ${ data.main.temp_min.toInt()} ° C"
                       tvMaxTemp.text="Min tem: ${ data.main.temp_max.toInt()} ° C"
                       tvHumidity.text="${data.main.humidity}%"
                       tvPressure.text="${data.main.pressure} hPa"
                       tvUpdateTime.text ="Last update ${
                           SimpleDateFormat(
                               "hh:mm a",
                               Locale.ENGLISH
                           ).format(data.dt * 1000)
                       }"

                   }



                   println("temp : ${response.body()!!.main.temp}")

               }
           }


       }
    }
}