package hatpeon.app.com.Adapter


import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import hatpeon.app.com.Fragment.CuisineReataurants
import hatpeon.app.com.Fragment.RestaurantsView
import hatpeon.app.com.Model.Restaurant
import hatpeon.app.com.R
import hatpeon.app.com.databinding.RestaurantChildBinding
import java.io.Serializable
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*


class RestaurantAdapter (private val arrayList: ArrayList<Restaurant>, private val context: Context): RecyclerView.Adapter<HomeViewHolder>() {
    lateinit var sharedPreference: SharedPreferences

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = RestaurantChildBinding.inflate(inflater, parent, false)
        return HomeViewHolder(binding)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        val restaurant = arrayList[position]

        holder.binding.restaruentName.text=restaurant.name
        holder.binding.description.text=restaurant.description
        holder.binding.location.text=restaurant.address
        Glide.with(context).
        load(restaurant.image)
            .timeout(60000)
            /*   .placeholder(R.drawable.hatpeon)*/
            .into(holder.binding.image);
        val data: Restaurant = arrayList[position]
        holder.binding.voucher.text="VOUCHER : "+restaurant.coupons




        /*TimeCalculation*/
        try {
            val startTime = restaurant.opening_time
            val endTime = restaurant.closing_time
            val format = SimpleDateFormat("h:m a")
            val date2 = format.parse(startTime)
            val currentTimeNow = SimpleDateFormat("h:m a", Locale.getDefault()).format(Date())
            val current = format.parse(currentTimeNow.toString())
            val difference = current.time - date2.time
            val differenceSeconds = difference / 1000 % 60
            val differenceMinutes = difference / (60 * 1000) % 60
            val differenceHours = difference / (60 * 60 * 1000) % 24
            val differenceDays = difference / (24 * 60 * 60 * 1000)
            println("$differenceDays days, ")
            println("$differenceHours hours, ")
            println("$differenceMinutes minutes, ")
            println("$differenceSeconds seconds.")
            val currentTime = SimpleDateFormat("h:m a", Locale.getDefault()).format(Date())
            val df = SimpleDateFormat("h:m a", Locale.getDefault())
            val currentTime_ = df.parse(currentTime.toString())
            val sdf: DateFormat = SimpleDateFormat("h:m a")
            val endTime_ = sdf.parse(endTime)
            val startTime_ = sdf.parse(startTime)
            if (currentTime_>endTime_||currentTime_<startTime_){
                holder.binding.openLayout.visibility= View.VISIBLE
                holder.binding.openAt.text="$differenceHours hr $differenceMinutes min".replace("-","")
            }else{
                holder.binding.openLayout.visibility= View.GONE
            }
            holder.binding.next.setOnClickListener {
                if (currentTime_>endTime_||currentTime_<startTime_){
                    Toast.makeText(context,"This Restaurant close now", Toast.LENGTH_SHORT).show()
                }else{
                    val bundle= Bundle()
                    bundle.putSerializable("value", data as Serializable)
                    bundle.putString("id", data.id)
                    val result= RestaurantsView()
                    result.arguments=bundle
                    it.findNavController().navigate(R.id.navigation_restaruant,bundle)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        if (holder.binding.voucher.text.equals("VOUCHER : ")){
            holder.binding.voucher.visibility= View.GONE
        }else{
            holder.binding.voucher.visibility= View.VISIBLE
        }

/*        holder.binding.next.setOnClickListener {
            //Log.e("Data",CuisineReataurants.cuisine)

            val bundle= Bundle()
            bundle.putSerializable("value", data as Serializable)
            bundle.putString("id", data.id)
*//*            if (CuisineReataurants.cuisine==null){

            }else{
                bundle.putString("from",CuisineReataurants.cuisine)
            }*//*
            val result= RestaurantsView()

            result.arguments=bundle
           // it.findNavController().popBackStack()
            it.findNavController().navigate(R.id.navigation_restaruant,bundle)
        //    Navigation.findNavController(it).navigate(R.id.action_fifthFragment_to_sixthFragment)
        }*/

    }

    override fun getItemCount(): Int {
        return arrayList.size
    }
}

class HomeViewHolder(val binding: RestaurantChildBinding):RecyclerView.ViewHolder(binding.root) {

}