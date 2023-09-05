package com.example.weatherapp

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.adapter.RvAdapter
import com.example.weatherapp.data.forecastModels.ForecastData
import com.example.weatherapp.databinding.ActivityMainBinding
import com.example.weatherapp.databinding.FiveDayBottomBinding
import com.example.weatherapp.utils.RetrofitInstance
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Task
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import android.provider.Settings
class MainActivity : AppCompatActivity() {
    lateinit var  binding : ActivityMainBinding
    lateinit var  forecast_binding : FiveDayBottomBinding
private lateinit var  dialog: BottomSheetDialog
     var  city = "lviv"
private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
         binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        dialog= BottomSheetDialog(this,R.style.BottomSheetTheme)
        forecast_binding= FiveDayBottomBinding.inflate(layoutInflater)
        dialog.setContentView(forecast_binding.root)

fusedLocationProviderClient=LocationServices.getFusedLocationProviderClient(this)


      binding.searchView11.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener{
          override fun onQueryTextSubmit(query: String?): Boolean {
              if(query!=null)
              {city= query}
              getCurrentWeather(city)

              return true
          }

          override fun onQueryTextChange(newText: String?): Boolean {
              return false
          }

      })

        //fetchLocation()
        fetchLocation()

        getCurrentWeather(city)

        binding.tvForecast.setOnClickListener {

            openDialog()

        }



        binding.tvLocation.setOnClickListener {
            fetchLocation()

        }
    }
    fun requestLocationServicesActivation(context: Context) {
        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        context.startActivity(intent)
    }
    fun areLocationServicesEnabled(context: Context): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    private fun fetchLocation() {


        if (!areLocationServicesEnabled(this)) {
            // Location services are disabled, request activation.
            requestLocationServicesActivation(this)
            return
        }
        val task: Task<Location> = fusedLocationProviderClient.lastLocation


        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),101
            )
            return
        }

        task.addOnSuccessListener {
            val geocoder=Geocoder(this,Locale.getDefault())

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
                geocoder.getFromLocation(it.latitude,it.longitude,1, object: Geocoder.GeocodeListener{
                    override fun onGeocode(addresses: MutableList<Address>) {
                        city = addresses[0].locality
                    }

                })
            }else{
                val address = geocoder.getFromLocation(it.latitude,it.longitude,1) as List<Address>

                city = address[0].locality
            }

            getCurrentWeather(city)
        }
    }

    private fun openDialog() {
        getForecast()
        forecast_binding.rvForecast.apply{
            setHasFixedSize(true)
            layoutManager= GridLayoutManager(this@MainActivity,1,RecyclerView.HORIZONTAL,false)
        }
        dialog.window?.attributes?.windowAnimations=R.style.DialogAnimation
        dialog.show()
    }

    private fun getForecast() {
        GlobalScope.launch (Dispatchers.IO){

            val response = try{
                RetrofitInstance.api.getForecasr(city, "metric","32975a8757688ffdfb49c7c73266659d")
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
                    var ForecastArray= arrayListOf<ForecastData>()
                    ForecastArray = data.list as ArrayList<ForecastData>
                    val adapter= RvAdapter(ForecastArray)
                    forecast_binding.rvForecast.adapter=adapter
                    forecast_binding.textView.text=" Five days forecast in ${data.city.name}"


                }

            }

        }
    }




    @SuppressLint("SetTextI18n")
    private fun getCurrentWeather(city: String) {
       GlobalScope.launch (Dispatchers.IO){

           val response = try{
RetrofitInstance.api.getCurrentWeather(city, "metric","32975a8757688ffdfb49c7c73266659d")
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