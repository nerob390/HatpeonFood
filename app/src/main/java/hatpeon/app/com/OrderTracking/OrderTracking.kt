package hatpeon.app.com.OrderTracking

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Window
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.frammanagment.network.Rettrofit
import com.app.frammanagment.network.Services
import com.bumptech.glide.Glide
import com.google.gson.JsonObject
import hatpeon.app.com.Adapter.CartSummeryAdapter
import hatpeon.app.com.Adapter.OrderTrackingAdapter
import hatpeon.app.com.Model.Cart
import hatpeon.app.com.R
import hatpeon.app.com.databinding.ActivityOrderTrackingBinding
import hatpeon.app.com.databinding.ActivityPaymentBinding
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.collections.ArrayList

class OrderTracking : AppCompatActivity() {
    val retService = Rettrofit.getInstance().create(Services::class.java)
    var cartAdapter: OrderTrackingAdapter? = null
    lateinit var binding:ActivityOrderTrackingBinding
    var cartList: ArrayList<Cart> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityOrderTrackingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val orderId="168"
        retService.ordersTracking(orderId).enqueue(
            object : Callback<JsonObject> {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    if (response.isSuccessful) {
                        val response=response.body()
                        val jsonObject = JSONObject(response.toString())
                        val fristDataObj=jsonObject.getJSONObject("data")
                        val dataObj=fristDataObj.getJSONObject("data")
                        binding.subTotal.text="Tk "+dataObj.getString("sub_total")
                        binding.deliveryfee.text="Tk "+dataObj.getString("delivery_charge")
                        binding.discountTaka.text="Tk "+dataObj.getString("discount")
                        binding.totalPrice.text="Tk "+dataObj.getString("total")
                        binding.orderId.text="Order ID: "+dataObj.getString("id")
                        binding.status.text=dataObj.getString("status_name")

                        /*customer*/
                        val customerObj=dataObj.getJSONObject("customer")
                        binding.phone.text=customerObj.getString("phone")
                        binding.name.text=customerObj.getString("name")
                        binding.address.text=dataObj.getString("address")

                        /*Restaurants*/

                        val restaurant=dataObj.getJSONObject("restaurant")
                        binding.resName.text=restaurant.getString("name")
                        binding.resAddress.text=restaurant.getString("address")
                        binding.resFullName.text=restaurant.getString("name")
                        binding.resDescription.text=restaurant.getString("description")
                        binding.paymentStatus.text=dataObj.getString("payment_method_name")
                        Glide.with(applicationContext).
                        load(restaurant.getString("image"))
                            .timeout(60000)
                            .into(binding.resImage)

                        val itemArray=dataObj.getJSONArray("items")
                        for (i in 0..itemArray.length()-1) {
                            val item = itemArray.getJSONObject(i)
                            val items=item.getJSONObject("menu_item")
                            cartList.add(Cart(item.getInt("id"),
                                items.getString("name"),
                                item.getString("restaurant_id"),
                                item.getString("discounted_price"),
                                "",
                                item.getString("unit_price"),
                                item.getString("quantity"),
                                "",
                                ""))
                        }

                        Log.e("OrderTrackingRsponse",jsonObject.toString())
                        val linearLayoutManager = LinearLayoutManager(applicationContext)
                        cartAdapter = OrderTrackingAdapter(cartList,applicationContext)
                        binding.recyclerView.layoutManager = linearLayoutManager
                        binding.recyclerView.adapter = cartAdapter

                    }
                }
                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    Log.e("Error",t.localizedMessage!!)
                }
            }

        )
    }

}