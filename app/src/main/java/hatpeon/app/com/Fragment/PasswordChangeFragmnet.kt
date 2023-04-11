package hatpeon.app.com.Fragment

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.app.frammanagment.Common.Common
import com.app.frammanagment.network.Rettrofit
import com.app.frammanagment.network.Services
import com.google.gson.JsonObject
import hatpeon.app.com.MainActivity
import hatpeon.app.com.R
import hatpeon.app.com.databinding.FragmentPasswordChangeFragmnetBinding
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception

class PasswordChangeFragmnet : Fragment() {
    lateinit var binding:FragmentPasswordChangeFragmnetBinding
    lateinit var navController: NavController
    val retService = Rettrofit.getInstance().create(Services::class.java)
    lateinit var sharedPreferences: SharedPreferences
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
/*        // Inflate the layout for this fragment
        navController=findNavController()
        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (true) {
                    navController.popBackStack()
                    navController.navigate(R.id.navigation_profile)
                } else  NavHostFragment.findNavController(this@PasswordChangeFragmnet).navigateUp()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner, onBackPressedCallback
        )*/
        binding= FragmentPasswordChangeFragmnetBinding.inflate(inflater,container,false)
        sharedPreferences= requireContext().getSharedPreferences("UserInfo", Context.MODE_PRIVATE)
        binding.back.setOnClickListener {
            requireActivity().onBackPressed()
        }

        binding.update.setOnClickListener {
            validation()

        }
        Log.e("password",sharedPreferences.getString("password","").toString())
        return binding.root
    }
    fun validation(){
        if (binding.currentPassword.text.isNullOrEmpty()){
            binding.currentPassword.setError("Current Password Mandatory !")
            binding.currentPassword.requestFocus()
        }
        else if (binding.newPassword.text.isNullOrEmpty()){
            binding.newPassword.setError("New Password Mandatory !")
            binding.newPassword.requestFocus()
        }else if(binding.newPassword.length()<6){
            binding.newPassword.setError("Minimum 6 character password Mandatory !")
            binding.newPassword.requestFocus()
        }
        else if (binding.confirmPassword.text.isNullOrEmpty()){
            binding.confirmPassword.setError("Re Type Password Mandatory !")
            binding.confirmPassword.requestFocus()
        }else if (!binding.newPassword.text.toString().matches(binding.confirmPassword.text.toString().toRegex())){
            binding.confirmPassword.setError("Re Type Password Not Match !")
            binding.confirmPassword.requestFocus()
        }else if (!binding.currentPassword.text.toString().matches(sharedPreferences.getString("password","").toString().toRegex())){
            binding.currentPassword.setError("Current Password Not Match !")
            binding.currentPassword.requestFocus()
        }
        else{
            Common.CustomeDialog(requireContext(),"Yes","No","Are you sure went to update your password?")
            Common.customDialog.show()
            Common.customeYesBtn.setOnClickListener {
                Common.customDialog.dismiss()
                ProfileUpdate()
            }
            Common.customeNoBtn.setOnClickListener {
                Common.customDialog.dismiss()
            }

        }
    }
    fun ProfileUpdate(){
        val jsonObject= JsonObject()
        jsonObject.addProperty("password_current", binding.currentPassword.text.toString())
        jsonObject.addProperty("password", binding.newPassword.text.toString())
        jsonObject.addProperty("password_confirmation", binding.confirmPassword.text.toString())
        Log.e("postJsonData",jsonObject.toString())
        retService.change_password(jsonObject).enqueue(
            object : Callback<JsonObject> {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    val response=response.body()
                    try {

                        val jsonObj = JSONObject(response.toString())
                        if (jsonObj.getString("status").equals("200")){
                            Toast.makeText(requireContext(), "Password update success", Toast.LENGTH_SHORT).show()
                            login()

                        }else{
                            Toast.makeText(activity, "Update Failed", Toast.LENGTH_SHORT).show()
                        }
                    }catch (e:Exception){
                        e.printStackTrace()
                    }

                }
                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    Log.e("Error",t.message!!)
                }
            }

        )
    }
    fun login(){
        val jsonObject= JsonObject()
        jsonObject.addProperty("phone",sharedPreferences.getString("phone",""))
        jsonObject.addProperty("password",binding.confirmPassword.text.toString())
        Log.e("PostJsonData",jsonObject.toString())
        retService.login(jsonObject).enqueue(
            object : Callback<JsonObject> {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    try {
                        if (response.isSuccessful) {
                            val response = response.body()
                            val jsonObject = JSONObject(response.toString())
                            Log.e("regObj", jsonObject.toString())
                            if (jsonObject.getString("status").equals("200")) {
                                val dataObj = jsonObject.getJSONObject("data")
                                Common.sharePreferences(
                                    requireContext(),
                                    dataObj.getString("id"),
                                    dataObj.getString("name"),
                                    dataObj.getString("email"),
                                    dataObj.getString("phone"),
                                    dataObj.getString("image"),
                                    dataObj.getString("address"),
                                    binding.confirmPassword.text.toString(),
                                    jsonObject.getString("token"),
                                )
                                val intent = Intent(requireContext(), MainActivity::class.java)
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                intent.putExtra("EXIT", true)
                                startActivity(intent)
                                activity?.finish()
                            }
                        }

                    }catch (e:Exception){
                        e.printStackTrace()
                    }
                }
                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    Log.e("JsonData",jsonObject.toString())
                }
            }

        )
    }

}