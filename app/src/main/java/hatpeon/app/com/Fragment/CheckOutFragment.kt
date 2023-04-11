package hatpeon.app.com.Fragment


import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.frammanagment.Common.Common
import com.app.frammanagment.network.Rettrofit
import com.app.frammanagment.network.Services
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.gson.JsonObject
import hatpeon.app.com.Adapter.CartSummeryAdapter
import hatpeon.app.com.DB.CartDatabase
import hatpeon.app.com.MainActivity
import hatpeon.app.com.Model.Cart
import hatpeon.app.com.Model.Restaurant
import hatpeon.app.com.Payment.PayNow
import hatpeon.app.com.R
import hatpeon.app.com.databinding.FragmentCheckOutBinding
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.sql.Date
import java.util.*


class CheckOutFragment : Fragment(), OnMapReadyCallback {
    lateinit var binding:FragmentCheckOutBinding
    lateinit var navController: NavController
    private var mMap: GoogleMap? = null
    var cartList: List<Cart> = ArrayList()
    var database: CartDatabase? = null
    lateinit var sharedPreferences: SharedPreferences
    lateinit var user_profile: SharedPreferences
    var cartAdapter: CartSummeryAdapter? = null
    var restaurant: Restaurant = Restaurant()
    val retService = Rettrofit.getInstance().create(Services::class.java)
    var myAddress: String? = null
    var paymentType: String? = null
    var resId: String? = null
    var couponId: String = "empty"
    var cartJson = JSONArray()
    var optionArray = JSONArray()
    val orderObj=JSONObject()
    companion object{
        lateinit var totalPriceText: TextView
        lateinit var date: String

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
/*        navController=findNavController()
        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (true) {
                    var bundle :Bundle ?=this@CheckOutFragment.arguments
                    if(bundle != null) {
                        restaurant = bundle.getSerializable("value") as Restaurant
                    }
                    val args=this@CheckOutFragment.arguments
                    if (args?.get("from").toString().equals("search")){
                        val bundle=Bundle()
                        navController.popBackStack()
                        bundle.putString("from",args?.get("from").toString())
                        bundle.putString("id",args?.get("id").toString())
                        bundle.putSerializable("value", restaurant as Serializable)
                        navController.navigate(R.id.navigation_cart,bundle)
                    }else{
                        val bundle=Bundle()
                        navController.popBackStack()
                        bundle.putString("id",args?.get("id").toString())
                        bundle.putSerializable("value", restaurant as Serializable)
                        navController.navigate(R.id.navigation_cart,bundle)
                    }

                } else  NavHostFragment.findNavController(this@CheckOutFragment).navigateUp()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner, onBackPressedCallback
        )*/
        // Inflate the layout for this fragment
        binding= FragmentCheckOutBinding.inflate(inflater,container,false)
        binding.toolbar.setNavigationIcon(R.drawable.ic_baseline_keyboard_backspace_24);
        binding.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }
        sharedPreferences= requireContext().getSharedPreferences("Location", Context.MODE_PRIVATE)
        user_profile= requireContext().getSharedPreferences("UserInfo", Context.MODE_PRIVATE)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        binding.userNumber.text=user_profile.getString("phone","")
        mapFragment.getMapAsync(this)
        binding.today.setBackgroundResource(R.drawable.round_stock)
        binding.todayImage.setColorFilter(ContextCompat.getColor(requireContext(), R.color.theme_color))
        binding.checkBox.setImageResource(R.drawable.checkbox)
        binding.checkBox.setColorFilter(ContextCompat.getColor(requireContext(), R.color.theme_color))

        binding.bkash.setBackgroundResource(R.drawable.round_stock)
        binding.bkashImage.setColorFilter(ContextCompat.getColor(requireContext(), R.color.theme_color))
        binding.bkashcheckBox.setImageResource(R.drawable.checkbox)
        binding.bkashcheckBox.setColorFilter(ContextCompat.getColor(requireContext(), R.color.theme_color))
        date="Today"
        binding.today.setOnClickListener {
            date="Today"
            binding.today.setBackgroundResource(R.drawable.round_stock)
            binding.todayImage.setColorFilter(ContextCompat.getColor(requireContext(), R.color.theme_color))
            binding.checkBox.setImageResource(R.drawable.checkbox)
            binding.checkBox.setColorFilter(ContextCompat.getColor(requireContext(), R.color.theme_color))


            binding.tomorrow.setBackgroundResource(R.drawable.stock_shadow)
            binding.tomorrowImage.setColorFilter(ContextCompat.getColor(requireContext(), R.color.black))
            binding.tomorrowcheckBox.setImageResource(R.drawable.unchecked)
            binding.tomorrowcheckBox.setColorFilter(ContextCompat.getColor(requireContext(), R.color.black))
        }
        binding.tomorrow.setOnClickListener {
            date="Tomorrow"
            binding.tomorrow.setBackgroundResource(R.drawable.round_stock)
            binding.tomorrowImage.setColorFilter(ContextCompat.getColor(requireContext(), R.color.theme_color))
            binding.tomorrowcheckBox.setImageResource(R.drawable.checkbox)
            binding.tomorrowcheckBox.setColorFilter(ContextCompat.getColor(requireContext(), R.color.theme_color))


            binding.today.setBackgroundResource(R.drawable.stock_shadow)
            binding.todayImage.setImageResource(R.drawable.date)
            binding.todayImage.setColorFilter(ContextCompat.getColor(requireContext(), R.color.black))
            binding.checkBox.setImageResource(R.drawable.unchecked)
            binding.checkBox.setColorFilter(ContextCompat.getColor(requireContext(), R.color.black))

        }
        paymentType="bkash"
        binding.bkash.setOnClickListener {
            paymentType="bkash"
            binding.bkash.setBackgroundResource(R.drawable.round_stock)
            binding.bkashImage.setColorFilter(ContextCompat.getColor(requireContext(), R.color.theme_color))
            binding.bkashcheckBox.setImageResource(R.drawable.checkbox)
            binding.bkashcheckBox.setColorFilter(ContextCompat.getColor(requireContext(), R.color.theme_color))


            binding.cash.setBackgroundResource(R.drawable.stock_shadow)
            binding.cashImage.setColorFilter(ContextCompat.getColor(requireContext(), R.color.black))
            binding.cashcheckBox.setImageResource(R.drawable.unchecked)
            binding.cashcheckBox.setColorFilter(ContextCompat.getColor(requireContext(), R.color.black))
        }

        binding.cash.setOnClickListener {
            paymentType="cash"
            binding.cash.setBackgroundResource(R.drawable.round_stock)
            binding.cashImage.setColorFilter(ContextCompat.getColor(requireContext(), R.color.theme_color))
            binding.cashcheckBox.setImageResource(R.drawable.checkbox)
            binding.cashcheckBox.setColorFilter(ContextCompat.getColor(requireContext(), R.color.theme_color))


            binding.bkash.setBackgroundResource(R.drawable.stock_shadow)
            binding.bkashImage.setColorFilter(ContextCompat.getColor(requireContext(), R.color.black))
            binding.bkashcheckBox.setImageResource(R.drawable.unchecked)
            binding.bkashcheckBox.setColorFilter(ContextCompat.getColor(requireContext(), R.color.black))
        }
        database = CartDatabase.getInstance(requireContext()!!.applicationContext)
        cartList = database!!.getData().cart
        totalPriceText =binding.subTotal
        val args=this@CheckOutFragment.arguments
        resId=args?.get("id").toString()


        Log.e("restuartID",args?.get("id").toString())
        val linearLayoutManager = LinearLayoutManager(requireContext())
        cartAdapter = CartSummeryAdapter(cartList,requireContext())
        binding.recyclerView.layoutManager = linearLayoutManager
        binding.recyclerView.adapter = cartAdapter

        if (MainActivity.lat.equals("default")){
            getShipingCharge(args?.get("id").toString(),sharedPreferences.getString("lat","").toString(),sharedPreferences.getString("long","")!!.toString())
            getAddress(requireContext(),sharedPreferences.getString("lat","").toString().toDouble(),sharedPreferences.getString("long","")!!.toString().toDouble())
        }else{
            getShipingCharge(args?.get("id").toString(),MainActivity.lat,MainActivity.long_)
            getAddress(requireContext(),MainActivity.lat.toDouble(),MainActivity.long_.toDouble())
        }

        binding.couponCheck.setOnClickListener {
            CheckCupon()
        }


        binding.goToCheckOut.setOnClickListener {
            if (paymentType.equals("bkash")){
                val intent = Intent(requireContext(), PayNow::class.java)
                intent.putExtra("mobile",binding.userNumber.text.toString())
                intent.putExtra("address",binding.userAddress.text.toString())
                intent.putExtra("addressLabel","current")
                intent.putExtra("delivery_charge",binding.deliveryfee.text.toString().replace("Tk ",""))
                if (binding.discountTaka.text.isNullOrEmpty()){
                    intent.putExtra("discount","0.0")
                }else{
                    intent.putExtra("discount",binding.discountTaka.text.toString().replace("Tk ",""))
                }
                intent.putExtra("coupon_id",couponId)
                intent.putExtra("order_type",1)
                if (MainActivity.lat.equals("default")){
//                    orderObj.put("lat",sharedPreferences.getString("lat","")!!)
//                    orderObj.put("long",sharedPreferences.getString("long","")!!)
                    intent.putExtra("lat","37.4219983")
                    intent.putExtra("long","-122.084")
                }else{
                    intent.putExtra("lat",MainActivity.lat.toDouble())
                    intent.putExtra("long",MainActivity.long_.toDouble())
                }
                intent.putExtra("remarks","remarks")
                intent.putExtra("total",binding.totalPrice.text.toString().replace("Tk ",""))
                intent.putExtra("restaurant_id",resId)
                intent.putExtra("delivery_date",date)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                intent.putExtra("EXIT",true)
                startActivity(intent)
             //   requireActivity().finish()
            }else{
                ConfirmOrder()
            }

        }
        return binding.root
    }

    override fun onMapReady(p0: GoogleMap?) {
        mMap = p0
        // Add a marker in Sydney and move the camera
        // Add a marker in Sydney and move the camera
        val letLng:LatLng;
        if (MainActivity.lat.equals("default")){
            letLng = LatLng(sharedPreferences.getString("lat","")!!.toDouble(), sharedPreferences.getString("long","")!!.toDouble())
        }else{
            letLng = LatLng(MainActivity.lat.toDouble(),MainActivity.long_.toDouble())
        }
        mMap!!.addMarker(MarkerOptions().position(letLng).title("Hat Peon"))
        mMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(letLng, 18f), 5000, null)
    }
fun getShipingCharge(id:String,lat:String,long:String){
        retService.getShippingChanrge(id,lat,long).enqueue(
            object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful) {
                        val response = response.body()
                        val jsonObj = JSONObject(response!!.string())
                        val shippingCharge=jsonObj.getString("data").toDouble()
                        val subTotal=binding.subTotal.text.toString().replace("Tk ","").toDouble()
                        val total=shippingCharge+subTotal
                        binding.deliveryfee.text="Tk "+shippingCharge.toString()
                        binding.totalPrice.text="Tk "+total.toString()
                        Log.e("obj", jsonObj.toString())


                    }

                    }
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Log.e("Error",t.localizedMessage!!)
                }
            }

        )

 }
    fun getAddress(context: Context?, LATITUDE: Double, LONGITUDE: Double) {
        //Set Address
        try {
            val geo = Geocoder(context, Locale.getDefault())
            val addresses = geo.getFromLocation(LATITUDE, LONGITUDE, 1)
            binding.userAddress.setText("Loading...")
            if (addresses != null && addresses.size > 0) {
                val locality = addresses[0].getAddressLine(0)
                val country = addresses[0].countryName
                val state = addresses[0].adminArea
                val sub_admin = addresses[0].subAdminArea
                val city = addresses[0].featureName
                val pincode = addresses[0].postalCode
                val locality_city = addresses[0].locality
                val sub_localoty = addresses[0].subLocality
                if (locality != null && country != null) {
                    binding.userAddress.setText(
                        locality + ", " + (if (sub_localoty != null) "$sub_localoty, " else "") + (if (locality_city != null) "$locality_city, " else "") + (if (city != null) "$city, " else "") + (if (sub_admin != null) "$sub_admin, " else "") + (if (state != null) "$state, " else "") + country + ", " + (pincode
                            ?: "")
                    )
                    myAddress = locality + sub_localoty + locality_city + state
                } else {
                    binding.userAddress.setText("Location could not be fetched...")
                }
            }
        } catch (e: Exception) {
            binding.userAddress.setText("Location could not be fetched...")
            e.printStackTrace()
        }
        return
    }
    fun CheckCupon(){
        binding.progress.visibility=View.VISIBLE
        val jsonObject= JsonObject()
        jsonObject.addProperty("code", binding.couponText.text.toString())
        jsonObject.addProperty("restaurant_id", resId)
        retService.checkCupon(jsonObject).enqueue(
            object : Callback<JsonObject> {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    try {
                        if (response.isSuccessful){
                            binding.progress.visibility=View.GONE
                            val jsonObj = JSONObject(response.body().toString())
                            val dataObj=jsonObj.getJSONObject("data")
                            val type=dataObj.getString("type")
                            couponId=dataObj.getString("id")
                            val discount:Double
                            if (type.equals("flat")){
                                discount=dataObj.getString("value").toDouble()
                            }else{
                                discount=binding.totalPrice.text.toString().replace("Tk ","").toDouble()*dataObj.getString("value").toDouble()/100
                            }
                            val mainPrice=binding.totalPrice.text.toString().replace("Tk ","").toDouble()
                            val pricewithoutDiscount=mainPrice-discount
                            binding.discount.visibility=View.VISIBLE
                            binding.totalPrice.text="Tk "+pricewithoutDiscount
                            binding.discountTaka.text="Tk "+discount

                            Log.e("Discount",discount.toString())
/*                        if (jsonObj.getString("status").equals("200")){
                            Toast.makeText(requireContext(), "Password update success", Toast.LENGTH_SHORT).show()

                        }else{
                            Toast.makeText(activity, "Update Failed", Toast.LENGTH_SHORT).show()
                        }*/
                        }else{
                            binding.progress.visibility=View.GONE
                            Toast.makeText(activity, "Coupon not valid", Toast.LENGTH_SHORT).show()
                        }

                    }catch (e: java.lang.Exception){
                        e.printStackTrace()
                    }

                }
                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    Log.e("Error",t.message!!)
                }
            }

        )
    }

    fun ConfirmOrder(){
        for (i in 0..cartList.size-1){
            var cartArray = JSONObject()
            cartArray.put("instructions",cartList.get(i).instructions)
            cartArray.put("restaurant_id",cartList.get(i).restaurantId)
            cartArray.put("menu_item_variation_id",cartList.get(i).menuItemVariationId)
            cartArray.put("menuItem_id",cartList.get(i).itemId.toString())
            cartArray.put("unit_price",cartList.get(i).price)
            cartArray.put("quantity",cartList.get(i).quantity)
            cartArray.put("discounted_price",cartList.get(i).discountedPrice)
            cartArray.put("options",optionArray)

            Log.e("cartArray",cartArray.toString())
            cartJson.put(cartArray)
        }


        orderObj.put("items",cartJson.toString())
        orderObj.put("mobile",binding.userNumber.text.toString())
/*        orderObj.put("address",binding.userAddress.text.toString())
        orderObj.put("addressLabel",binding.userAddress.text.toString())   */
        orderObj.put("address","Dhaka Division, Bangladesh")
        orderObj.put("addressLabel","current")
        orderObj.put("delivery_charge",binding.deliveryfee.text.toString().replace("Tk ",""))
        if (binding.discountTaka.text.isNullOrEmpty()){
            orderObj.put("discount","0.0")
        }else{
            orderObj.put("discount",binding.discountTaka.text.toString().replace("Tk ",""))
        }
        orderObj.put("discount","0.0")
        orderObj.put("coupon_id",couponId)
        orderObj.put("order_type",1)
        if (MainActivity.lat.equals("default")){
/*            orderObj.put("lat",sharedPreferences.getString("lat","")!!)
            orderObj.put("long",sharedPreferences.getString("long","")!!)  */
            orderObj.put("lat","37.4219983")
            orderObj.put("long","-122.084")
        }else{
            orderObj.put("lat",MainActivity.lat.toDouble())
            orderObj.put("long",MainActivity.long_.toDouble())
        }
        orderObj.put("remarks","remarks")
        orderObj.put("total",binding.totalPrice.text.toString().replace("Tk ",""))
        orderObj.put("restaurant_id",resId)
        orderObj.put("delivery_date",date)
        Log.e("PostData",orderObj.toString())
        val mediaType = "application/json".toMediaType()
        val body = orderObj.toString().toRequestBody(mediaType)
        retService.orders(body).enqueue(
            object : Callback<JsonObject> {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    binding.progress.visibility=View.GONE
                    if (response.isSuccessful) {
                        val response=response.body()
                        val jsonObject = JSONObject(response.toString())
                        Log.e("regObj",jsonObject.toString())
                        if (jsonObject.getString("status").equals("200")){
                            Common.SnackBarText(requireActivity().findViewById(android.R.id.content),jsonObject.getString("message"),"","144317")
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