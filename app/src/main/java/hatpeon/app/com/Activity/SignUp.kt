package hatpeon.app.com.Activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.app.frammanagment.Common.Common
import com.app.frammanagment.network.Rettrofit
import com.app.frammanagment.network.Services
import com.google.gson.JsonObject
import hatpeon.app.com.databinding.ActivitySignUpBinding
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response


class SignUp : AppCompatActivity() {
    lateinit var binding:ActivitySignUpBinding
    val retService = Rettrofit.getInstance().create(Services::class.java)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivitySignUpBinding.inflate(layoutInflater)

        setContentView(binding.root)

        binding.goToLogin.setOnClickListener {
            startActivity(Intent(this,Login::class.java))
        }
        binding.signUp.setOnClickListener {
          Registration()
          validationCheck()
        }

    }
    fun validationCheck(){
        if (binding.fullName.text.isNullOrEmpty()){
            Common.SnackBarText(findViewById(android.R.id.content),"Name Mandatory !","","ED0000")
        }
        else if (binding.number.text.isNullOrEmpty()){
            Common.SnackBarText(findViewById(android.R.id.content),"Mobile Number Mandatory !","","ED0000")
        }else if (!binding.number.text?.matches("^(?:\\+88|88)?(01[3-9]\\d{8})\$".toRegex())!!){
            Common.SnackBarText(findViewById(android.R.id.content),"Valid Mobile Number Mandatory !","","ED0000")
        } else if (binding.password.getText().toString().isNullOrEmpty()) {
            Common.SnackBarText(findViewById(android.R.id.content),"Password Mandatory !","","ED0000")
        } else if (binding.confirmPassword.getText().toString().isNullOrEmpty()) {
            Common.SnackBarText(findViewById(android.R.id.content),"Confirm Password Mandatory !","","ED0000")
        } else if (binding.confirmPassword.getText().toString() != binding.password.getText().toString()) {
            Common.SnackBarText(findViewById(android.R.id.content),"Password Not Match !","","ED0000")
        }
        else{
            Registration()
        }

    }
    fun Registration(){
        binding.progress.visibility=View.VISIBLE
        //Common.loading(this)
       // Common.pDialog.show()
        val jsonObject= JsonObject()
        jsonObject.addProperty("name",binding.fullName.text.toString()).toString()
        jsonObject.addProperty("email","test011@gmail.com")
        jsonObject.addProperty("phone",binding.number.text.toString())
        jsonObject.addProperty("password",binding.password.text.toString())
        jsonObject.addProperty("password_confirmation",binding.confirmPassword.text.toString())
        jsonObject.addProperty("role","4")
        jsonObject.addProperty("nid","")


/*        jsonObject.addProperty("name","Test").toString()
        jsonObject.addProperty("email","")
        jsonObject.addProperty("phone","01700000000")
        jsonObject.addProperty("password","12345678")
        jsonObject.addProperty("password_confirmation","12345678")
        jsonObject.addProperty("role","4")
        jsonObject.addProperty("nid","")*/
        retService.registration(jsonObject).enqueue(
            object : Callback<JsonObject> {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    binding.progress.visibility=View.GONE
                    if (response.isSuccessful) {
                        val response=response.body()
                        val jsonObject = JSONObject(response.toString())
                        Log.e("regObj",jsonObject.toString())
                        if (jsonObject.getString("status").equals("200")){
                            Common.SnackBarText(findViewById(android.R.id.content),jsonObject.getString("message"),"","144317")
                        }

                    } else {
                        try {
                        //   response.errorBody() // do something with that
                            Log.e("MYError",response.errorBody()!!.string())
                           // val jsonObject = JSONObject(response.errorBody()!!.string())

                            Common.SnackBarText(findViewById(android.R.id.content),
                                "Email or Phone already used",
                                "",
                                "ED0000"
                            )
/*
                            if (response is HttpException) {
                                if ((response as HttpException).code() == 422) {
                                    val errorResponse =
                                        (response as HttpException).response()!!.errorBody()!!.string()
                                    Log.e("ERROR",errorResponse)
                                    //your validations

                                }
                            }
*/

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