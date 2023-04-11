package hatpeon.app.com.Adapter


import android.app.Dialog
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.navigation.Navigation
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
import java.util.*


class CuisineToRestaurantAdapter (private val arrayList: ArrayList<Restaurant>, private val context: Context): RecyclerView.Adapter<CuisineHomeViewHolder>() {
    lateinit var sharedPreference: SharedPreferences
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CuisineHomeViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = RestaurantChildBinding.inflate(inflater, parent, false)
        return CuisineHomeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CuisineHomeViewHolder, position: Int) {
        val restaurant = arrayList[position]
        holder.binding.restaruentName.text=restaurant.name
        holder.binding.description.text=restaurant.description
        holder.binding.location.text=restaurant.address
        Glide.with(context).load(restaurant.image).into(holder.binding.image)
        val data: Restaurant = arrayList[position]
        holder.binding.voucher.text="VOUCHER : "+restaurant.coupons




        /*TimeCalculation*/
        try {
            Log.e("openingTime",restaurant.opening_time)
            Log.e("closeingTime",restaurant.closing_time)
            val startTime = restaurant.opening_time
            // Thread.sleep(10000)
            val endTime = restaurant.closing_time
            val format = SimpleDateFormat("h:m a")

            val date1 = format.parse(endTime)
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
            Log.e("currentTime",currentTime.toString())

            val df = SimpleDateFormat("h:m a", Locale.getDefault())
            val currentTime_ = df.parse(currentTime.toString())


            val sdf: DateFormat = SimpleDateFormat("h:m a")
            val endTime_ = sdf.parse(endTime)
            val startTime_ = sdf.parse(startTime)

            //  println("Time: " + sdf.format(endTime_))
            Log.e("Time: " ,sdf.format(endTime_))

            if (currentTime_>endTime_||currentTime_<startTime_){
                Log.e("timeOver","yes")
                holder.binding.openLayout.visibility=View.VISIBLE
                holder.binding.openAt.text="$differenceHours hr $differenceMinutes min".replace("-","")
            }else{
                holder.binding.openLayout.visibility=View.GONE
                Log.e("timeOver","No")
            }
            holder.binding.next.setOnClickListener {
                lateinit var dialog_close_now: Dialog
                if (currentTime_>endTime_||currentTime_<startTime_){
                    dialog_close_now = Dialog(context)
                    dialog_close_now.requestWindowFeature(Window.FEATURE_NO_TITLE)
                    dialog_close_now.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    dialog_close_now.setContentView(R.layout.close_now)

                    val okBtn = dialog_close_now.findViewById<AppCompatButton>(R.id.ok)
                    okBtn.setOnClickListener {
                       dialog_close_now.dismiss()
                    }
                    dialog_close_now.setCancelable(false)
                    dialog_close_now.setCanceledOnTouchOutside(false)
                    dialog_close_now.show()
                }else{
                    val bundle= Bundle()
                    bundle.putSerializable("value", data as Serializable)
                    bundle.putString("from",CuisineReataurants.cuisine)
                    bundle.putString("title",CuisineReataurants.title)
                    bundle.putString("id",restaurant.id)
                    val result= RestaurantsView()
                    result.arguments=bundle
                    it.findNavController().navigate(R.id.navigation_restaruant,bundle)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }









        if (holder.binding.voucher.text.equals("VOUCHER : ")){
            holder.binding.voucher.visibility=View.GONE
        }else{
            holder.binding.voucher.visibility=View.VISIBLE
        }


    }

    override fun getItemCount(): Int {
        return arrayList.size
    }
}

class CuisineHomeViewHolder(val binding: RestaurantChildBinding):RecyclerView.ViewHolder(binding.root) {

}