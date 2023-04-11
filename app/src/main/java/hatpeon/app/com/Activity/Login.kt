package hatpeon.app.com.Activity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.app.frammanagment.Common.Common
import com.app.frammanagment.network.Rettrofit
import com.app.frammanagment.network.Services
import com.google.gson.JsonObject
import hatpeon.app.com.MainActivity
import hatpeon.app.com.R
import hatpeon.app.com.databinding.ActivityLoginBinding
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Login : AppCompatActivity() {
    lateinit var binding:ActivityLoginBinding
    val retService = Rettrofit.getInstance().create(Services::class.java)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val sharedPreference= getSharedPreferences("UserInfo", Context.MODE_PRIVATE)
        val isLoggedIn2: Boolean = sharedPreference.getBoolean("isLoggedIn", false)
        if (isLoggedIn2){
            val intent = Intent(applicationContext, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.putExtra("EXIT",true)
            startActivity(intent)
            finish()
        }
        binding.login.setOnClickListener {
            validationCheck()
        }

        binding.gotoRegister.setOnClickListener {
            startActivity(Intent(this,SignUp::class.java))
        }

    }
    fun validationCheck(){
       if (binding.phone.text.isNullOrEmpty()){
            Common.SnackBarText(findViewById(android.R.id.content),"Mobile Number Mandatory !","","ED0000")
        }else if (!binding.phone.text?.matches("^(?:\\+88|88)?(01[3-9]\\d{8})\$".toRegex())!!){
            Common.SnackBarText(findViewById(android.R.id.content),"Valid Mobile Number Mandatory !","","ED0000")
        } else if (binding.password.getText().toString().isNullOrEmpty()) {
            Common.SnackBarText(findViewById(android.R.id.content),"Password Mandatory !","","ED0000")
        }
        else{
            Login_()
        }

    }
    fun Login_(){
        binding.progress.visibility=View.VISIBLE
        val jsonObject= JsonObject()
        jsonObject.addProperty("phone",binding.phone.text.toString()).toString()
        jsonObject.addProperty("password",binding.password.text.toString())
        retService.login(jsonObject).enqueue(
            object : Callback<JsonObject> {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    binding.progress.visibility=View.GONE
                    if (response.isSuccessful) {
                        val response=response.body()
                        val jsonObject = JSONObject(response.toString())
                        Log.e("regObj",jsonObject.toString())
                        if (jsonObject.getString("status").equals("200")){
                          val dataObj=jsonObject.getJSONObject("data")
                            Common.sharePreferences(applicationContext,
                                dataObj.getString("id"),
                                dataObj.getString("name"),
                                dataObj.getString("email"),
                                dataObj.getString("phone"),
                                dataObj.getString("image"),
                                dataObj.getString("address"),
                                binding.password.text.toString(),
                                jsonObject.getString("token"),
                            )
                            Common.SnackBarText(findViewById(android.R.id.content),jsonObject.getString("message"),"","144317")
                            val intent = Intent(applicationContext, MainActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            intent.putExtra("EXIT",true)
                            startActivity(intent)
                            finish()
                        }

                    } else {
                        try {
                            Common.SnackBarText(findViewById(android.R.id.content),
                                "Something wrong,try again",
                                "",
                                "ED0000"
                            )


                        } catch (e:Exception){
                            e.printStackTrace()
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