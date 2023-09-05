package com.example.weatherapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.data.forecastModels.ForecastData
import com.example.weatherapp.databinding.RvitemlayoutBinding
import com.squareup.picasso.Picasso
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class RvAdapter( val ForecastArray: ArrayList<ForecastData>) : RecyclerView.Adapter<RvAdapter.ViewHolder>() {
    class ViewHolder( val binding: RvitemlayoutBinding ): RecyclerView.ViewHolder(binding.root){

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem= ForecastArray[position]
        holder.binding.apply {

            val image_icon = currentItem.weather[0].icon
            val imgUrl="https://openweathermap.org/img/w/${image_icon}.png"
            Picasso.get().load(imgUrl).into(imgItem)
            tvItemTemp.text= "${currentItem.main.temp.toInt()} ° C"
            tvItemStatus.text="${currentItem.weather[0].description}"
            tvItemTime.text=displayTime(currentItem.dt_txt)
        }
    }

    private fun displayTime(dtTxt: String): CharSequence? {
        val input = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val output = DateTimeFormatter.ofPattern("MM-dd HH:mm")
        val dateTime = LocalDateTime.parse(dtTxt,input)
        return output.format(dateTime)}

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(RvitemlayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun getItemCount(): Int {
        return  ForecastArray.count()
    }
}