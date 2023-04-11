package hatpeon.app.com.Fragment

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.frammanagment.network.Rettrofit
import com.app.frammanagment.network.Services
import com.google.gson.JsonObject
import hatpeon.app.com.Adapter.RestaurantAdapter
import hatpeon.app.com.DB.CartDatabase
import hatpeon.app.com.Location.MarkerMove
import hatpeon.app.com.MVVVM.HomeViewModel
import hatpeon.app.com.MVVVM.SliderViewModel
import hatpeon.app.com.MainActivity
import hatpeon.app.com.Model.Restaurant
import hatpeon.app.com.OrderTracking.OrderTracking
import hatpeon.app.com.R
import hatpeon.app.com.databinding.FragmentHomeBinding
import org.imaginativeworld.whynotimagecarousel.model.CarouselItem
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.IllegalStateException
import java.lang.RuntimeException


class HomeFragment : Fragment() {
    lateinit var binding:FragmentHomeBinding
    val restaurant:ArrayList<Restaurant> = ArrayList()
    val list = mutableListOf<CarouselItem>()
    var database: CartDatabase? = null
    lateinit var restuarants: RestaurantAdapter
    val retService = Rettrofit.getInstance().create(Services::class.java)
    lateinit var sharedPreference: SharedPreferences
    lateinit var viewModel: HomeViewModel
    lateinit var viewModelSlider: SliderViewModel
    companion object{
        lateinit var myAddress: TextView
        lateinit var locationImage: ImageView
        var token: String?=null


    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (savedInstanceState==null){
            viewModel= ViewModelProvider(requireActivity()).get(HomeViewModel::class.java)
            viewModel.myLiveData(binding.progress).observe(requireActivity(), Observer {
                try {
                    val layoutManager = LinearLayoutManager(requireContext())
                    restuarants= RestaurantAdapter(it,requireContext())
                    binding.rasturentRecycler.adapter=restuarants
                    binding.rasturentRecycler.layoutManager=layoutManager
                }catch (e:NullPointerException){
                    e.toString()
                }catch (e: IllegalStateException){
                    e.printStackTrace()
                }catch (r: RuntimeException){
                    r.printStackTrace()
                }


            })

            viewModelSlider= ViewModelProvider(requireActivity()).get(SliderViewModel::class.java)
            viewModelSlider.myLiveData(binding.progress).observe(requireActivity(), Observer {
                try {
                    binding.carousel.setData(it)
                    binding.carousel.registerLifecycle(lifecycle)
                }catch (e:NullPointerException){
                    e.toString()
                }catch (e: IllegalStateException){
                    e.printStackTrace()
                }catch (r: RuntimeException){
                    r.printStackTrace()
                }


            })
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var win: Window = requireActivity().window
        win.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)

        binding= FragmentHomeBinding.inflate(inflater,container,false)
        myAddress=binding.myAddress
        locationImage=binding.locationImage
        sharedPreference= requireActivity().getSharedPreferences("UserInfo", Context.MODE_PRIVATE)
        token=sharedPreference.getString("token","")
        Log.e("Token",token.toString())

        binding.deliveryTo.setOnClickListener {
            startActivity(Intent(requireContext(),OrderTracking::class.java))
        }

        binding.myAddress.text=MainActivity.myAddress


        locationImage.setOnClickListener {
            val intent = Intent(requireContext(), MarkerMove::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.putExtra("EXIT", true)
            startActivity(intent)
        }
        binding.userImage.setOnClickListener {
           // findNavController().popBackStack()
            findNavController().navigate(R.id.action_home_to_profile)
        }

        binding.search.setOnClickListener {
          //  findNavController().popBackStack()
            findNavController().navigate(R.id.navigation_search)
        }
        binding.restaurants.setOnClickListener {
           // findNavController().popBackStack()
            findNavController().navigate(R.id.navigation_cuisine)
        }
        binding.home.setOnClickListener {
            val bundle=Bundle()
            bundle.putString("home","Home Made Food")
            bundle.putString("foodType","3")
            //findNavController().popBackStack()
            findNavController().navigate(R.id.navigation_homecook,bundle)
        }
        binding.cook.setOnClickListener {
            val bundle=Bundle()
            bundle.putString("home","Ready To Cook Food")
            bundle.putString("foodType","2")
           // findNavController().popBackStack()
            findNavController().navigate(R.id.navigation_homecook,bundle)
        }




        database = CartDatabase.getInstance(requireContext()!!.applicationContext)
        database!!.getData().deleteCartData()

     //   SlidrImage()

       // PopularRestaurant()

        return binding.root
    }
    fun SlidrImage(){
        retService.sliderImage().enqueue(
            object : Callback<JsonObject> {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    if (response.isSuccessful) {
                        val response=response.body()
                        val jsonObject = JSONObject(response.toString())
                        Log.e("regObj",jsonObject.toString())
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
                            binding.carousel.setData(list)
                            binding.carousel.registerLifecycle(lifecycle)

                        }


                }
                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    Log.e("Error",t.localizedMessage!!)
                }
            }

        )
    }
//    fun PopularRestaurant(){
//        retService.popularRestaurant().enqueue(
//            object : Callback<JsonObject> {
//                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
//                    binding.progress.visibility=View.GONE
//                    if (response.isSuccessful) {
//                        val response=response.body()
//                        val jsonObject = JSONObject(response.toString())
//                        Log.e("regObj",jsonObject.toString())
//                        val dataObj=jsonObject.getJSONObject("data")
//                        val dataArray=dataObj.getJSONArray("data")
//                        for (i in 0..dataArray.length()-1){
//                            val resObj=dataArray.getJSONObject(i)
//                            restaurant.add(
//                                Restaurant(
//                                    resObj.getString("id"),
//                                    resObj.getString("food_type_id"),
//                                    resObj.getString("name"),
//                                    resObj.getString("description"),
//                                    resObj.getString("address"),
//                                    resObj.getString("image"),
//                                    resObj.getString("avgRating"),
//                                    resObj.getString("avgRatingUser")
//                                )
//                            )
//
//                        }
//
//                        try {
//                            //Adapter
//                            val layoutManager = LinearLayoutManager(requireContext())
//                            binding.rasturentRecycler.adapter=RestaurantAdapter(restaurant,requireContext())
//                            binding.rasturentRecycler.layoutManager=layoutManager
//                        }catch (e:NullPointerException){
//                            e.toString()
//                        }catch (e: IllegalStateException){
//                            e.printStackTrace()
//                        }catch (r: RuntimeException){
//                            r.printStackTrace()
//                        }
//
//
//                    }
//
//
//                }
//                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
//                    Log.e("Error",t.localizedMessage!!)
//                }
//            }
//
//        )
//    }
}