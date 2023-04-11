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
import hatpeon.app.com.Model.Cuisine
import hatpeon.app.com.Model.Restaurant
import kotlinx.coroutines.*
import org.imaginativeworld.whynotimagecarousel.model.CarouselItem
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.IllegalStateException
import java.lang.RuntimeException

class SliderViewModel(application: Application) : AndroidViewModel(application){
    var job: Job? = null
    val list = mutableListOf<CarouselItem>()
    var myHome= MutableLiveData<ArrayList<CarouselItem>>()
    private var isDataFetched = false
    lateinit var couponCode:String
    val retService = Rettrofit.getInstance().create(Services::class.java)
    fun myLiveData(progressBar:RelativeLayout): LiveData<ArrayList<CarouselItem>> {
        if (myHome==null){
            myHome =  MutableLiveData<ArrayList<CarouselItem>>()
        }
        job= CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.Main){
                if (isDataFetched) {
                 progressBar.visibility=View.GONE
                }else{
                    progressBar.visibility=View.VISIBLE
                    retService.sliderImage().enqueue(
                        object : Callback<JsonObject> {
                            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                                progressBar.visibility= View.GONE
                                isDataFetched = true
                                if (response.isSuccessful) {
                                    val response=response.body()
                                    val jsonObject = JSONObject(response.toString())
                                    Log.e("sliderImage",jsonObject.toString())
                                    val dataObj=jsonObject.getJSONObject("data")
                                    val dataArray=dataObj.getJSONArray("data")
                                    for (i in 0..dataArray.length()-1){
                                        val sliderObj=dataArray.getJSONObject(i)
                                        list.add(
                                            CarouselItem(
                                                imageUrl = sliderObj.getString("image"),
                                                caption = sliderObj.getString("description")
                                            )
                                        )

                                    }
                                    myHome.postValue(list as ArrayList<CarouselItem>?)

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