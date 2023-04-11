package hatpeon.app.com.Fragment

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.frammanagment.network.Rettrofit
import com.app.frammanagment.network.Services
import com.google.gson.JsonObject
import hatpeon.app.com.Adapter.CuisineToRestaurantAdapter
import hatpeon.app.com.Adapter.RestaurantAdapter
import hatpeon.app.com.Model.Cuisine
import hatpeon.app.com.Model.Restaurant
import hatpeon.app.com.R
import hatpeon.app.com.databinding.FragmentCuisineReataurantsBinding
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.IllegalStateException
import java.lang.RuntimeException

class CuisineReataurants : Fragment() {
    lateinit var binding:FragmentCuisineReataurantsBinding
    val restaurant:ArrayList<Restaurant> = ArrayList()
    val retService = Rettrofit.getInstance().create(Services::class.java)
    var cuisineList: Cuisine = Cuisine()
    lateinit var navController: NavController
    companion object{
        lateinit var cuisine: String
        lateinit var title: String
        lateinit var idcuisine: String
        lateinit var couponCode:String
        lateinit var couponCodeValue:String

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var win: Window = requireActivity().window
        win.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        navController=findNavController()
        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (true) {
                    val args=this@CuisineReataurants.arguments
                    Log.e("Titile",args?.get("title").toString())
                    if (args?.get("from").toString().equals("cuisine")){
                        navController.popBackStack()
                        navController.navigate(R.id.navigation_cuisine)
                    }else{
//                        navController.popBackStack()
//                        navController.navigate(R.id.navigation_home)
                    }

                } else  NavHostFragment.findNavController(this@CuisineReataurants).navigateUp()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner, onBackPressedCallback
        )
        // Inflate the layout for this fragment
        binding= FragmentCuisineReataurantsBinding.inflate(inflater,container,false)

        val args=this.arguments
        var bundle :Bundle ?=this.arguments
        if(bundle != null) {
          //  cuisineList = bundle.getSerializable("value") as Cuisine
        }
        onSaveInstanceState(args!!)
        binding.title.text=args?.get("title").toString()
        cuisine="cuisine"
        title=args?.get("title").toString()
        idcuisine=args?.get("id").toString()
        Log.e("From",args?.get("from").toString())
        binding.back.setOnClickListener {
            requireActivity().onBackPressed()
        }
/*        restaurant.add(Restaurant("Seven One","Seven one restaruent was born out of affection...","Town hall more,Mymensign","EV!"))
        restaurant.add(Restaurant("Seven One","Seven one restaruent was born out of affection...","Town hall more,Mymensign","EV!"))
        restaurant.add(Restaurant("Seven One","Seven one restaruent was born out of affection...","Town hall more,Mymensign","EV!"))
        restaurant.add(Restaurant("Seven One","Seven one restaruent was born out of affection...","Town hall more,Mymensign","EV!"))*/
        RestuarantByCuisine(idcuisine,"1")
       // Log.e("cuisineID",cuisineList.id.toString())
        return binding.root

    }
    fun RestuarantByCuisine(cuisineID:String,foodType:String){
        restaurant.clear()
        binding.progress.visibility=View.VISIBLE
        retService.restaurantBycuisine(cuisineID,foodType).enqueue(
            object : Callback<JsonObject> {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    if (response.isSuccessful) {
                        val response=response.body()
                        binding.progress.visibility=View.GONE
                        val jsonObject = JSONObject(response.toString())
                        Log.e("regObj",jsonObject.toString())
                        val dataObj=jsonObject.getJSONObject("data")
                        val dataTodata=dataObj.getJSONObject("data")
                        val resArray=dataTodata.getJSONArray("restaurants")
                        for (i in 0..resArray.length()-1){
                            val resObj=resArray.getJSONObject(i)
                            val couponsArray=resObj.getJSONArray("coupons")
                            //  Log.e("couponsArray",couponsArray.toString())

                            if (couponsArray.length()==0) {
                                couponCode=""
                                couponCodeValue=""
                            }
                            else{
                                val couponeObj=couponsArray.getJSONObject(0)
                                couponCode=couponeObj.optString("code")
                                couponCodeValue=couponeObj.optString("value")
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
                        try {
                            //Adapter
                            val layoutManager = LinearLayoutManager(requireContext())
                            binding.reataurants.adapter= CuisineToRestaurantAdapter(restaurant,requireContext())
                            binding.reataurants.layoutManager=layoutManager

                        }catch (e:NullPointerException){
                            e.toString()
                        }catch (e: IllegalStateException){
                            e.printStackTrace()
                        }catch (r: RuntimeException){
                            r.printStackTrace()
                        }


                    }


                }
                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    Log.e("Error",t.localizedMessage!!)
                }
            }

        )
    }
}