package hatpeon.app.com.Fragment

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences

import android.net.Uri
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
import com.squareup.picasso.Picasso
import hatpeon.app.com.ImageUpload.UriUtil
import hatpeon.app.com.MainActivity
import hatpeon.app.com.Model.Restaurant


import hatpeon.app.com.R
import hatpeon.app.com.databinding.FragmentEditProfileBinding
import net.gotev.uploadservice.MultipartUploadRequest
import net.gotev.uploadservice.ServerResponse
import net.gotev.uploadservice.UploadInfo
import net.gotev.uploadservice.UploadStatusDelegate
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception
import java.util.*


class EditProfileFragment : Fragment() {
    lateinit var binding: FragmentEditProfileBinding
    lateinit var navController: NavController
    var SELECT_PICTURE = 200
    var productMainImageUri: Uri? = null
    var fullFilePathmain: String? = null
    lateinit var sharedPreferences: SharedPreferences
    val retService = Rettrofit.getInstance().create(Services::class.java)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
//        navController = findNavController()
//        val onBackPressedCallback = object : OnBackPressedCallback(true) {
//            override fun handleOnBackPressed() {
//                if (true) {
//                    navController.popBackStack()
//                    navController.navigate(R.id.navigation_profile)
//                } else NavHostFragment.findNavController(this@EditProfileFragment).navigateUp()
//            }
//        }
//        requireActivity().onBackPressedDispatcher.addCallback(
//            viewLifecycleOwner, onBackPressedCallback
//        )

        // Inflate the layout for this fragment
        binding = FragmentEditProfileBinding.inflate(inflater, container, false)

        sharedPreferences= requireContext().getSharedPreferences("UserInfo", Context.MODE_PRIVATE)
        binding.fullName.setText(sharedPreferences.getString("name",""))
        binding.email.setText(sharedPreferences.getString("email",""))
        binding.phone.setText(sharedPreferences.getString("phone",""))
        binding.address.setText(sharedPreferences.getString("address",""))
        Picasso.get().load(sharedPreferences.getString("image","")).placeholder(R.drawable.profile).into(binding.imgProfile)
        binding.back.setOnClickListener {
            requireActivity().onBackPressed()
        }
        binding.userImage.setOnClickListener {
            Log.e("click", "yes")
            imageChooser()
        }

        binding.changeProfilePicture.setOnClickListener {
            Log.e("click", "yes")
            Log.e("fullFilePathmain", fullFilePathmain.toString())
            if (fullFilePathmain.isNullOrEmpty()) {
                activity?.let { it1 ->
                    Common.SnackBarText(
                        it1.findViewById(android.R.id.content),
                        "Please selected image first !",
                        "",
                        "ED0000"
                    )
                }
            } else {
                Common.CustomeDialog(
                    requireContext(),
                    "Yes",
                    "No",
                    "Are you sure went to update your profile picture?"
                )
                Common.customDialog.show()
                Common.customeYesBtn.setOnClickListener {
                    Common.customDialog.dismiss()
                   // Common.pDialog.show()
                      UploadImageToServer()
                }
                Common.customeNoBtn.setOnClickListener {
                    Common.customDialog.dismiss()
                }

            }
        }
        binding.update.setOnClickListener {
            ProfileUpdate()
        }
        return binding.root
    }
    fun imageChooser() {
        // create an instance of the
        // intent of the type image
        val i = Intent()
        i.type = "image/*"
        i.action = Intent.ACTION_GET_CONTENT
        // pass the constant to compare it
        // with the returned requestCode
        startActivityForResult(Intent.createChooser(i, "Select Picture"), SELECT_PICTURE)
    }
//    private fun checkPermission(permissionlistener: PermissionListener) {
//        TedPermission.create()
//            .setPermissionListener(permissionlistener)
//            .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
//            .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
//
//    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            // compare the resultCode with the
            // SELECT_PICTURE constant
            if (requestCode == SELECT_PICTURE) {
                // Get the url of the image from data
                productMainImageUri = data?.getData()
                if (null != productMainImageUri) {
                    // update the preview image in the layout
                    binding.imgProfile.setImageURI(productMainImageUri);
                    fullFilePathmain = UriUtil.getPathFromUri(activity,productMainImageUri)

                    Log.e("ImageUri",productMainImageUri.toString());
                }
            }
        }
    }
    fun UploadImageToServer(){
        try {
            val request = MultipartUploadRequest(
                activity,
                Rettrofit.baseUrl+"profile"
            )
            request.setMaxRetries(2)
                .addHeader("Accept","application/json")
                .addHeader("Authorization","Bearer "+ HomeFragment.token)
            request.addFileToUpload(fullFilePathmain, "image")
            request.addParameter("name", binding.fullName.text.toString())
            request.addParameter("phone", binding.phone.text.toString())
            request.addParameter("address", binding.address.text.toString())
            request.addParameter("email", binding.email.text.toString())
            request.setDelegate(object : UploadStatusDelegate {
                override fun onProgress(uploadInfo: UploadInfo) {}
                override fun onError(uploadInfo: UploadInfo, exception: Exception) {}
                override fun onCompleted(uploadInfo: UploadInfo, serverResponse: ServerResponse) {
                    println(serverResponse)
                    Log.e("Server Response", serverResponse.bodyAsString.toString())
                    val jsonObj = JSONObject(serverResponse.bodyAsString.toString())
                    if (jsonObj.getString("status").equals("200")) {
                        Common.customDialog.dismiss()
                        Toast.makeText(activity, "Update Success", Toast.LENGTH_SHORT).show()
                       login()

                    }else{
                        Toast.makeText(activity, "Update Failed", Toast.LENGTH_SHORT).show()
/*                        Common.pDialog.dismiss()
                        val dataObject=jsonObj.getJSONObject("data")
                        val errorObj=dataObject.getJSONObject("errors")
                        Common.SnackBarText(requireActivity().findViewById(android.R.id.content),errorObj.optString("profile_pic").replace("[","").replace("]",""),"","ED0000")*/
                    }
                    Log.e(
                        ContentValues.TAG, java.lang.String.format(
                            Locale.getDefault(),
                            "ID %1\$s: completed in %2\$ds at %3$.2f Kbit/s. Response code: %4\$d, body:[%5\$s]",
                            uploadInfo.uploadId, uploadInfo.elapsedTime / 1000,
                            uploadInfo.uploadRate, serverResponse.httpCode,
                            serverResponse.bodyAsString
                        )
                    )
                }

                override fun onCancelled(uploadInfo: UploadInfo) {}
            }).startUpload()
            //   request.startUpload(); //Starting the upload;

//            Log.e("PostData", String.valueOf(request.startUpload()));
//            Log.e("PostData", String.valueOf(request.startUpload()));
        } catch (exc: Exception) {
            Toast.makeText(activity, exc.message, Toast.LENGTH_SHORT).show()
            Log.e(ContentValues.TAG, exc.message, exc)
        }
    }
    fun ProfileUpdate(){
        val jsonObject= JsonObject()
        jsonObject.addProperty("name", binding.fullName.text.toString())
        jsonObject.addProperty("phone", binding.phone.text.toString())
        jsonObject.addProperty("address", binding.address.text.toString())
        jsonObject.addProperty("email", binding.email.text.toString())
        Log.e("postJsonData",jsonObject.toString())
        retService.profileUpdate(jsonObject).enqueue(
            object : Callback<JsonObject> {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    val response=response.body()
                    try {

                        val jsonObj = JSONObject(response.toString())
                        if (jsonObj.getString("status").equals("200")){
                            Toast.makeText(requireContext(), "Profile update success", Toast.LENGTH_SHORT).show()
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
        jsonObject.addProperty("phone",binding.phone.text.toString()).toString()
        jsonObject.addProperty("password",sharedPreferences.getString("password",""))

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
                                    sharedPreferences.getString("password","").toString(),
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



