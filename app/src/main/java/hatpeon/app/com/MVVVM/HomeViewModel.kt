package hatpeon.app.com.MVVVM

import android.app.Application
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.frammanagment.network.Rettrofit
import com.app.frammanagment.network.Services
import com.google.gson.JsonObject
import hatpeon.app.com.Adapter.CuisineAdapter
import hatpeon.app.com.Adapter.RestaurantAdapter
import hatpeon.app.com.Fragment.HomeFragment
import hatpeon.app.com.Fragment.HomeMadeAndRedayToCookFragment
import hatpeon.app.com.Model.Cuisine
import hatpeon.app.com.Model.Restaurant
import kotlinx.coroutines.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.IllegalStateException
import java.lang.RuntimeException

class HomeViewModel(application: Application) : AndroidViewModel(application){
    var job: Job? = null
    var myHome= MutableLiveData<ArrayList<Restaurant>>()
    val restaurant:ArrayList<Restaurant> = ArrayList()
    private var isDataFetched = false
    lateinit var couponCode:String
    lateinit var couponCodeValue:String
    val retService = Rettrofit.getInstance().create(Services::class.java)
    fun myLiveData(progressBar:RelativeLayout): LiveData<ArrayList<Restaurant>> {
        if (myHome==null){
            myHome = MutableLiveData<ArrayList<Restaurant>>()
        }
        job= CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.Main){
             // restaurant.clear()
                if (isDataFetched) {
                 progressBar.visibility=View.GONE
                }else{
                    progressBar.visibility=View.VISIBLE
                    retService.popularRestaurant().enqueue(
                        object : Callback<JsonObject> {
                            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                                progressBar.visibility= View.GONE
                                isDataFetched = true
                                if (response.isSuccessful) {
                                    val response=response.body()
                                    val jsonObject = JSONObject(response.toString())
                                    Log.e("regObj",jsonObject.toString())
                                    val dataObj=jsonObject.getJSONObject("data")
                                    val dataArray=dataObj.getJSONArray("data")
                                    for (i in 0..dataArray.length()-1){
                                        val resObj=dataArray.getJSONObject(i)
                                        val couponsArray=resObj.getJSONArray("coupons")
                                      //  Log.e("couponsArray",couponsArray.toString())

                                        if (couponsArray.length()==0) {
                                        Log.e("Array","empty")
                                        couponCode=""
                                        couponCodeValue =""
                                       }
                                        else{
                                        Log.e("Array","not empty")
                                        val couponeObj=couponsArray.getJSONObject(0)
                                         couponCode=couponeObj.optString("code")
                                         couponCodeValue =couponeObj.optString("value")
                                        }

                                        restaurant.add(
                                            Restaurant(
                                                resObj.getString("id"),
                                                resObj.getString("food_type_id"),
                                                resObj.getString("name"),
                                                resObj.getString("description"),
                                                resObj.getString("address"),
                                                resObj.getString("image"),
                                                resObj.getString("avgRating"),
                                                resObj.getString("avgRatingUser"),
                                                resObj.getString("opening_time"),
                                                resObj.getString("closing_time"),
                                                couponCode,
                                                couponCodeValue
                                            )
                                        )

                                    }

                                    myHome.postValue(restaurant)

                                }


                            }
                            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                                Log.e("Error",t.localizedMessage!!)
                            }
                        }

                    )
                }


            }

        }
        return myHome


    }

}