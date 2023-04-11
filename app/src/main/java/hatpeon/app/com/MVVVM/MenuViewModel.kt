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

class MenuViewModel(application: Application) : AndroidViewModel(application){
    var job: Job? = null
    var myData= MutableLiveData<ArrayList<Menu>>()
    val arrayList:ArrayList<Menu> = ArrayList()
   // private var isDataFetched = false
    val retService = Rettrofit.getInstance().create(Services::class.java)
    fun myLiveData(progressBar:RelativeLayout,id:String): LiveData<ArrayList<Menu>> {
        if (myData==null){
            myData = MutableLiveData<ArrayList<Menu>>()
        }
        job= CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.Main){
               arrayList.clear()
/*                if (isDataFetched) {
                 progressBar.visibility=View.GONE
                }else{*/
                    progressBar.visibility=View.VISIBLE
                    retService.menuItem(id).enqueue(
                        object : Callback<JsonObject> {
                            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                                progressBar.visibility= View.GONE
                               // isDataFetched = true
                                if (response.isSuccessful) {
                                    val response = response.body()
                                    val jsonObject = JSONObject(response.toString())
                                    Log.e("obj", jsonObject.toString())
                                    val dataObj = jsonObject.getJSONObject("data")
                                    val dataArray = dataObj.getJSONObject("data")
                                    val menuArray = dataArray.getJSONArray("menuItems")
                                    for (i in 0..menuArray.length() - 1) {
                                        val cuiObj = menuArray.getJSONObject(i)
                                        arrayList.add(
                                            Menu(
                                                cuiObj.getString("id"),
                                                cuiObj.optString("restaurant_id"),
                                                cuiObj.getString("name"),
                                                cuiObj.getString("slug"),
                                                cuiObj.getString("menu_number"),
                                                cuiObj.getString("unit_price"),
                                                cuiObj.getString("discount_price"),
                                                cuiObj.getString("currency_code"),
                                                cuiObj.getString("image"),
                                                cuiObj.getString("description"),
                                            )
                                        )
                                        myData.postValue(arrayList)

                                    }

                                }
                            }
                            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                                Log.e("Error",t.localizedMessage!!)
                            }
                        }

                    )
               // }


            }

        }
        return myData


    }

}