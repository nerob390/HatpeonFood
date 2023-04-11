package hatpeon.app.com.Payment


import android.app.AlertDialog
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.app.frammanagment.Common.Common
import com.app.frammanagment.network.Rettrofit
import com.app.frammanagment.network.Services
import com.google.gson.JsonObject
import hatpeon.app.com.DB.CartDatabase
import hatpeon.app.com.Fragment.CheckOutFragment
import hatpeon.app.com.MainActivity
import hatpeon.app.com.Model.Cart
import hatpeon.app.com.R
import hatpeon.app.com.databinding.ActivityPaymentBinding
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.ArrayList


class PayNow : AppCompatActivity() {
    lateinit var binding:ActivityPaymentBinding
    lateinit var progressDialog: ProgressDialog
    val retService = Rettrofit.getInstance().create(Services::class.java)
    var cartList: List<Cart> = ArrayList()
    var database: CartDatabase? = null
    var cartJson = JSONArray()
    var optionArray = JSONArray()
    val orderObj=JSONObject()
    val paymentObj=JSONObject()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
         binding= ActivityPaymentBinding.inflate(layoutInflater)
        setContentView(binding.root)
        database = CartDatabase.getInstance(applicationContext!!.applicationContext)
        cartList = database!!.getData().cart
        if (!isConnected(this@PayNow)) {
            buildDialog(this@PayNow).show()
        }else{
            setContentView(binding.root)
            progressDialog = ProgressDialog(this@PayNow)
            progressDialog.setMessage("Loading...")
            val webSettings: WebSettings = binding.paymentView.getSettings()
            webSettings.javaScriptEnabled = true
            val myClient: WebViewClient = object : WebViewClient() {
                override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
                    super.onPageStarted(view, url, favicon)
                    progressDialog.show()
                }

                override fun onPageFinished(view: WebView, url: String) {
                    super.onPageFinished(view, url)
                    progressDialog.dismiss()
                    val responseDataOfPayment = HashMap<String, String>()
                    var urlResponseKeyValue = HashMap<String?, String?>()
                    Log.e("url-->", "onPageFinished: $url")
                    if (url.matches("(.*)https://hatpeon.com/bkash/success?(.*)".toRegex())){
                        Log.e("PaymentSuccess","Yes")
                        order(url)
                    }
                    if(url.equals("https://hatpeon.com/bkash/fail")) {
                        lateinit var failedDialog: Dialog
                        Log.e("PaymentSuccess", "No")
                        failedDialog = Dialog(this@PayNow)
                        failedDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                        failedDialog.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                        failedDialog.setContentView(R.layout.order_fail)

                        val okBtn = failedDialog.findViewById<AppCompatButton>(R.id.ok)
                        okBtn.setOnClickListener {
                            onBackPressed()
                        }
                        failedDialog.setCancelable(false)
                        failedDialog.setCanceledOnTouchOutside(false)
                        failedDialog.show()
                    }
                }

                override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                    return false
                }
            }

            binding.paymentView.getSettings().setJavaScriptEnabled(true)
            binding.paymentView.getSettings().setSupportZoom(true)
            binding.paymentView.setWebViewClient(myClient)
            binding.paymentView.loadUrl("https://hatpeon.com/bkash/pay?amount=1")

        }



      }
    fun isConnected(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netinfo = cm.activeNetworkInfo
        return if (netinfo != null && netinfo.isConnectedOrConnecting) {
            val wifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
            val mobile = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
            if (mobile != null && mobile.isConnectedOrConnecting || wifi != null && wifi.isConnectedOrConnecting) true else false
        } else false
    }

    fun buildDialog(c: Context): AlertDialog.Builder {
        val builder = AlertDialog.Builder(c)
        builder.setTitle("No Internet Connection")
        builder.setMessage("You need to have Mobile Data or wifi to access this. Press ok to Exit")
        builder.setPositiveButton("Ok") { dialog, which ->
            finish()
        }
        return builder

     }
    fun order(url:String){
        progressDialog = ProgressDialog(this@PayNow)
        progressDialog.setMessage("Order processing...")
        progressDialog.show()
        val intent = getIntent()
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
        orderObj.put("mobile",intent.getStringExtra("mobile"))
        orderObj.put("address","Dhaka District, Dhaka Division")
        orderObj.put("addressLabel",intent.getStringExtra("addressLabel"))
        orderObj.put("delivery_charge",intent.getStringExtra("delivery_charge"))
        orderObj.put("discount",intent.getStringExtra("discount"))
        orderObj.put("coupon_id",intent.getStringExtra("coupon_id"))
        orderObj.put("order_type",1)
        orderObj.put("lat", intent.getStringExtra("lat"))
        orderObj.put("long", intent.getStringExtra("long"))

        orderObj.put("remarks","remarks")
        orderObj.put("total",intent.getStringExtra("total"))
        orderObj.put("restaurant_id",intent.getStringExtra("restaurant_id"))
        orderObj.put("delivery_date",intent.getStringExtra("delivery_date"))
        Log.e("PostData",orderObj.toString())
        val mediaType = "application/json".toMediaType()
        val body = orderObj.toString().toRequestBody(mediaType)
        retService.orders(body).enqueue(
            object : Callback<JsonObject> {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    if (response.isSuccessful) {
                        val response=response.body()
                        val jsonObject = JSONObject(response.toString())
                        Log.e("regObj",jsonObject.toString())
                        /*{"status":200,"message":"You order completed successfully.","data":{"order_id":166,"total_amount":"1387.00"}}*/
                        if (jsonObject.getString("status").equals("200")){
                            progressDialog.dismiss()
                            val id=getTransactionId(url)
                            Log.e("TransactionId",id)
                            val data=jsonObject.getJSONObject("data")

                            paymentOrder(data.getString("order_id"),data.getString("total_amount"),"8",id.replace("[","").replace("]",""))
/*                            successDialog = Dialog(this@PayNow)
                            successDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                            successDialog.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                            successDialog.setContentView(R.layout.order_success)

                            val okBtn = successDialog.findViewById<AppCompatButton>(R.id.ok)
                            okBtn.setOnClickListener {

                            }
                            successDialog.setCancelable(false)
                            successDialog.setCanceledOnTouchOutside(false)
                            successDialog.show()*/
                        }

                    }
                }
                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    Log.e("Error",t.localizedMessage!!)
                }
            }

        )

    }

    override fun onBackPressed() {
        super.onBackPressed()
    }
  /*  onPageFinished: https://hatpeon.com/bkash/success?paymentId=0IFYXDJ1680292744606&transactionId=AD151814IX&amount=1*/
    fun getPaymentId(url: String): String {
        val uri = Uri.parse(url)
        val queryParameters = uri.getQueryParameters("paymentId")
        return queryParameters.toString()
    }
    fun getTransactionId(url: String): String {
        val uri = Uri.parse(url)
        val queryParameters = uri.getQueryParameters("transactionId")
        return queryParameters.toString()
    }

    fun paymentOrder(orderID:String, amount:String, paymentTypeID:String, transactionID:String){
        lateinit var successDialog: Dialog
        paymentObj.put("order_id",orderID)
        paymentObj.put("amount",amount)
        paymentObj.put("payment_method",paymentTypeID)
        paymentObj.put("payment_transaction_id",transactionID)

        val mediaType = "application/json".toMediaType()
        val body = paymentObj.toString().toRequestBody(mediaType)
        retService.ordersPayment(body).enqueue(
            object : Callback<JsonObject> {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    if (response.isSuccessful) {
                        val response=response.body()
                        val jsonObject = JSONObject(response.toString())
                        Log.e("paymentResponse",jsonObject.toString())
                        if (jsonObject.getString("status").equals("200")){

                            successDialog = Dialog(this@PayNow)
                            successDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                            successDialog.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                            successDialog.setContentView(R.layout.order_success)

                            val okBtn = successDialog.findViewById<AppCompatButton>(R.id.ok)
                            okBtn.setOnClickListener {

                            }
                            successDialog.setCancelable(false)
                            successDialog.setCanceledOnTouchOutside(false)
                            successDialog.show()

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

