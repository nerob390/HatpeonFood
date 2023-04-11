package hatpeon.app.com.Fragment

import android.content.SharedPreferences
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.app.frammanagment.network.Rettrofit
import com.app.frammanagment.network.Services
import com.google.gson.JsonObject
import com.squareup.picasso.Picasso
import hatpeon.app.com.Adapter.CuisineAdapter
import hatpeon.app.com.Adapter.MenuAdapter
import hatpeon.app.com.DB.CartDatabase
import hatpeon.app.com.MVVVM.CuisineViewModel
import hatpeon.app.com.MVVVM.MenuViewModel
import hatpeon.app.com.MainActivity
import hatpeon.app.com.Model.Cart
import hatpeon.app.com.Model.Menu
import hatpeon.app.com.Model.Restaurant
import hatpeon.app.com.R
import hatpeon.app.com.databinding.FragmentRestaurantsViewBinding
import okhttp3.internal.notify
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.Serializable
import java.lang.IllegalStateException
import java.lang.RuntimeException


class RestaurantsView : Fragment() {
 lateinit var binding:FragmentRestaurantsViewBinding
    val menu:ArrayList<Menu> = ArrayList()
    val retService = Rettrofit.getInstance().create(Services::class.java)
    lateinit var navController: NavController
    lateinit var sharedPreference: SharedPreferences
    var database: CartDatabase? = null
    lateinit var viewModel: MenuViewModel
    lateinit var menuAdapter: MenuAdapter
    var totalPrice = 0.0
    var cartList: List<Cart> = ArrayList()
    companion object{
        lateinit var cartDialog: LinearLayout
        lateinit var cartCount: TextView
        lateinit var totalPriceText: TextView
        lateinit var resid:String
        var restaurant: Restaurant = Restaurant()
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        requireActivity().getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS

        )
/*        navController=findNavController()
        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (true) {
                    val args=this@RestaurantsView.arguments
                    if (args?.get("from").toString().equals("cuisine")){
                        val bundle= Bundle()
                        bundle.putString("from","cuisine")
                        bundle.putString("title",args?.get("title").toString())
                        bundle.putString("id",args?.get("id").toString())
                        navController.navigate(R.id.navigation_cuisine_restuarants,bundle)
                    }
                   else if (args?.get("from_").toString().equals("Home Made Food")){
                        val bundle= Bundle()
                        bundle.putString("home","Home Made Food")
                        navController.navigate(R.id.navigation_homecook,bundle)
                    }
                 else if (args?.get("from_").toString().equals("Ready To Cook Food")){
                        val bundle= Bundle()
                        bundle.putString("home","Ready To Cook Food")
                        navController.navigate(R.id.navigation_homecook,bundle)
                    }
                    else{
                        findNavController().popBackStack()
                        navController.navigate(R.id.navigation_home)
                    }

                } else  NavHostFragment.findNavController(this@RestaurantsView).navigateUp()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner, onBackPressedCallback
        )*/
        try {
            binding= FragmentRestaurantsViewBinding.inflate(inflater,container,false)
            database = CartDatabase.getInstance(requireContext()!!.applicationContext)
            cartList = database!!.getData().cart
            var bundle :Bundle ?=this.arguments
            if(bundle != null) {
                restaurant = bundle.getSerializable("value") as Restaurant
                Picasso.get().load(restaurant.image).into(binding.image)
                binding.restaruentName.text=restaurant.name
                binding.location.text=restaurant.address

                if (TextUtils.isEmpty(restaurant.coupons)){
                    binding.discount.text= ""
                    binding.viewID.visibility=View.GONE
                }else{
                    binding.discount.text= "Use Voucher: ${restaurant.coupons} \nGet Discount Tk "+restaurant.couponsvalue+" On orders"
                }


            }
            cartDialog=binding.cartDialog
            cartCount=binding.cartItem
            totalPriceText=binding.totalPrice
            Log.e("cartSize",cartList.size.toString())
            if (cartList.size>0){
                binding.cartItem.text=cartList.size.toString()
                binding.cartDialog.visibility=View.VISIBLE
                for (i in 0..cartList.size-1){
                    totalPrice+= cartList.get(i).price.toDouble()*cartList.get(i).quantity.toDouble()
                    Log.e("TotalPrice",totalPrice.toString())

                }
                binding.totalPrice.text="Tk "+database!!.getData().sum().toString()
            }
            binding.back.setOnClickListener {
                requireActivity().onBackPressed()

            }
            val args=this.arguments
            resid=args?.get("id").toString()
            binding.cartDialog.setOnClickListener {
                val bundle=Bundle()
                bundle.putString("id", resid)
                bundle.putSerializable("value", restaurant as Serializable)
               // findNavController().popBackStack()
                findNavController().navigate(R.id.navigation_cart,bundle)
            }
          //  MenuItem(args?.get("id").toString())
            viewModel= ViewModelProvider(requireActivity()).get(MenuViewModel::class.java)
            viewModel.myLiveData(binding.progress,args?.get("id").toString()).observe(requireActivity(), Observer {
                try {
                    menuAdapter= MenuAdapter(it,requireContext())
                    binding.menuRecycler.adapter=menuAdapter
                }catch (e:NullPointerException){
                    e.toString()
                }catch (e: IllegalStateException){
                    e.printStackTrace()
                }catch (r: RuntimeException){
                    r.printStackTrace()
                }


            })

            //  Cuisine()
            val layoutManager = GridLayoutManager(requireContext(),2)
            binding.menuRecycler.layoutManager=layoutManager
        }catch (e:NullPointerException){
            e.toString()
        }catch (e:IllegalStateException){
            e.printStackTrace()
        }catch (r:RuntimeException){
            r.printStackTrace()
        }

        return binding.root
    }
/*    fun MenuItem(id:String){
        retService.menuItem(id).enqueue(
            object : Callback<JsonObject> {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    if (response.isSuccessful) {
                        val response=response.body()
                        val jsonObject = JSONObject(response.toString())
                        Log.e("obj",jsonObject.toString())
                        val dataObj=jsonObject.getJSONObject("data")
                        val dataArray=dataObj.getJSONObject("data")
                        val menuArray=dataArray.getJSONArray("menuItems")
                        for (i in 0..menuArray.length()-1){
                            val cuiObj=menuArray.getJSONObject(i)
                            menu.add(
                                Menu(
                                    cuiObj.getString("id"),
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

                        }
                        try {
                            //Adapter
                            val layoutManager = GridLayoutManager(requireActivity(),2)
                            binding.menuRecycler.adapter= MenuAdapter(menu,requireContext())
                            binding.menuRecycler.layoutManager=layoutManager
                        }catch (e:NullPointerException){
                            e.toString()
                        }catch (e:IllegalStateException){
                            e.printStackTrace()
                        }catch (r:RuntimeException){
                            r.printStackTrace()
                        }


                    }


                }
                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    Log.e("Error",t.localizedMessage!!)
                }
            }

        )
    }*/
}