package hatpeon.app.com.MVVVM

import android.app.Application
import android.util.Log
import android.view.View
import android.widget.RelativeLayout
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.GridLayoutManager
import com.app.frammanagment.network.Rettrofit
import com.app.frammanagment.network.Services
import com.google.gson.JsonObject
import hatpeon.app.com.Adapter.CuisineAdapter
import hatpeon.app.com.Model.Cuisine
import kotlinx.coroutines.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CuisineViewModel(application: Application) : AndroidViewModel(application){
    var job: Job? = null
    var myHome= MutableLiveData<ArrayList<Cuisine>>()
    val dashboard:ArrayList<Cuisine> = ArrayList()
    private var isDataFetched = false
    val retService = Rettrofit.getInstance().create(Services::class.java)
    fun myLiveData(progressBar:RelativeLayout): LiveData<ArrayList<Cuisine>> {
        if (myHome==null){
            myHome = MutableLiveData<ArrayList<Cuisine>>()
        }
        job= CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.Main){
                if (isDataFetched) {
                    progressBar.visibility= View.GONE
                }else{
                    progressBar.visibility=View.VISIBLE
                    retService.cuisine().enqueue(
                        object : Callback<JsonObject> {
                            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                                if (response.isSuccessful) {
                                    val response=response.body()
                                    progressBar.visibility=View.GONE
                                    isDataFetched = true
                                    val jsonObject = JSONObject(response.toString())
                                    Log.e("obj",jsonObject.toString())
                                    val dataObj=jsonObject.getJSONObject("data")
                                    val dataArray=dataObj.getJSONArray("data")
                                    for (i in 0..dataArray.length()-1){
                                        val cuiObj=dataArray.getJSONObject(i)
                                        dashboard.add(
                                            Cuisine(
                                                cuiObj.getString("id"),
                                                cuiObj.getString("name"),
                                                cuiObj.getString("slug"),
                                                cuiObj.getString("image"),
                                                cuiObj.getString("description")
                                            )
                                        )

                                    }
                                    myHome.postValue(dashboard)
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