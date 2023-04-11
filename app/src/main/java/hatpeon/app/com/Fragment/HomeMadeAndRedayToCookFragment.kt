package hatpeon.app.com.Fragment

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.activity.OnBackPressedCallback
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.frammanagment.network.Rettrofit
import com.app.frammanagment.network.Services
import com.google.gson.JsonObject
import hatpeon.app.com.Adapter.CuisineToHomeCookRestaurantAdapter
import hatpeon.app.com.Adapter.CuisineToRestaurantAdapter
import hatpeon.app.com.Model.Restaurant
import hatpeon.app.com.R
import hatpeon.app.com.databinding.FragmentHomeMadeAndRedayToCookBinding
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.IllegalStateException
import java.lang.RuntimeException


class HomeMadeAndRedayToCookFragment : Fragment() {
  lateinit var binding:FragmentHomeMadeAndRedayToCookBinding
    val restaurant:ArrayList<Restaurant> = ArrayList()
    val retService = Rettrofit.getInstance().create(Services::class.java)
    lateinit var navController: NavController
    companion object{
        lateinit var cuisine: String
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
                        navController.popBackStack()
                        navController.navigate(R.id.navigation_home)
                } else  NavHostFragment.findNavController(this@HomeMadeAndRedayToCookFragment).navigateUp()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner, onBackPressedCallback
        )
        // Inflate the layout for this fragment
        binding= FragmentHomeMadeAndRedayToCookBinding.inflate(inflater,container,false)
        restaurant.clear()
        val args=this.arguments
        cuisine=args?.get("home").toString()
        binding.title.setText(args?.get("home").toString())
        binding.back.setOnClickListener {
            requireActivity().onBackPressed()
        }

        RestuarantByCuisine(args?.get("foodType").toString())


        return binding.root
    }
    fun RestuarantByCuisine(cuisineID:String){
        retService.homereadytocook(cuisineID).enqueue(
            object : Callback<JsonObject> {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    if (response.isSuccessful) {
                        val response=response.body()
                        val jsonObject = JSONObject(response.toString())
                        Log.e("regObj",jsonObject.toString())
                        val dataObj=jsonObject.getJSONObject("data")
                        val dataTodata=dataObj.getJSONObject("data")
                        val resArray=dataTodata.getJSONArray("restaurants")
                        for (i in 0..resArray.length()-1){
                            val resObj=resArray.getJSONObject(i)
                            val couponsArray=resObj.getJSONArray("coupons")
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
                        try {
                            //Adapter
                            val layoutManager = LinearLayoutManager(requireContext())
                            binding.reataurants.adapter= CuisineToHomeCookRestaurantAdapter(restaurant,requireContext())
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