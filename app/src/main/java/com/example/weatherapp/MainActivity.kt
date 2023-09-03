package com.example.weatherapp

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.weatherapp.databinding.ActivityMainBinding
import com.example.weatherapp.utils.RetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

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
RetrofitInstance.api.getCurrentWeather("lviv", "metric","32975a8757688ffdfb49c7c73266659d")
           }catch (e:IOException){
               Toast.makeText(applicationContext,"app error ${e.message}",Toast.LENGTH_SHORT).show()
               return@launch

           }catch (e :HttpException){
               Toast.makeText(applicationContext,"http error ${e.message}",Toast.LENGTH_SHORT).show()
return@launch
           }
           println("\n AAAAAAAAAAAAAAAAAAAAAAAAA1\n")

           if( response.isSuccessful&& response.body()!= null){
               withContext((Dispatchers.Main)){
                   binding.tvText.text="tem : ${response.body()!!.main.temp}"
                   println("\n AAAAAAAAAAAAAAAAAAAAAAAAA\n")

                   println("temp : ${response.body()!!.main.temp}")
                   println("\nQAAAAAAAAAAAAAAAAAAAAAAAAA\n")

               }
           }
           println("\n AAAAAAAAAAAAAAAAAAAAAAAAA2\n")

       }
    }
}