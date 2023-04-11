package hatpeon.app.com.MVVVM

import android.app.Application
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.RelativeLayout
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
import hatpeon.app.com.Model.Cuisine
import hatpeon.app.com.Model.Menu
import hatpeon.app.com.Model.Restaurant
import kotlinx.coroutines.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.IllegalStateException
import java.lang.RuntimeException

class RestaurantsItemSearchViewModel(application: Application) : AndroidViewModel(application){
    var job: Job? = null
    var myHome= MutableLiveData<ArrayList<Menu>>()
    val restaurant:ArrayList<Menu> = ArrayList()
    private var isDataFetched = false
    val retService = Rettrofit.getInstance().create(Services::class.java)
    fun myLiveData(progressBar:RelativeLayout): LiveData<ArrayList<Menu>> {
        if (myHome==null){
            myHome = MutableLiveData<ArrayList<Menu>>()
        }
        job= CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.Main){
             // restaurant.clear()
                if (isDataFetched) {
                 progressBar.visibility=View.GONE
                }else{
                    progressBar.visibility=View.VISIBLE
                    retService.searchItem().enqueue(
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
                                        restaurant.add(
                                            Menu(
                                                resObj.getString("id"),
                                                resObj.getString("restaurant_id"),
                                                resObj.getString("name"),
                                                resObj.getString("slug"),
                                                resObj.getString("menu_number"),
                                                resObj.getString("unit_price"),
                                                resObj.getString("discount_price"),
                                                resObj.getString("currency_code"),
                                                resObj.getString("image"),
                                                resObj.getString("description")
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