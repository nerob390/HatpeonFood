package com.app.frammanagment.Common

import android.app.ActionBar
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment

import com.google.android.material.snackbar.Snackbar
import hatpeon.app.com.R


class Common {
    companion object{
        var mOnClickListener: View.OnClickListener? = null
        lateinit var pDialog: Dialog
        lateinit var customDialog: Dialog
        lateinit var customeYesBtn: Button
        lateinit var customeNoBtn: Button
        // lateinit var context: Context
        private val sharedPrefFile = "UserInfo"
        private val sharedLocation = "Location"
        lateinit var sharedPreferences: SharedPreferences
        fun SnackBarText( view:View,text:String,actionText:String,mycolor:String){
            val snackbar = Snackbar
                .make(view, text, Snackbar.LENGTH_LONG)
                .setAction(actionText, mOnClickListener)
            snackbar.setActionTextColor(Color.WHITE)
            val snackbarView = snackbar.view
            snackbarView.setBackgroundColor(Color.parseColor("#"+mycolor))
            val textView = snackbarView.findViewById(com.google.android.material.R.id.snackbar_text) as TextView
            textView.setTextColor(Color.WHITE)
            snackbar.show()

        }
        /*      SnackBar Action*/
        fun startActivity(context: Context, clazz: Class<*>) {
            val intent = Intent(context, clazz)
            context.startActivity(intent)

        }


        /*  IsInternet On*/
        fun isInternetAvailable(context: Context): Boolean {

            var result = false
            val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val networkCapabilities = connectivityManager.activeNetwork ?: return false
                val actNw =
                    connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
                result = when {
                    actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                    actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                    actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                    else -> false
                }
            } else {
                connectivityManager.run {
                    connectivityManager.activeNetworkInfo?.run {
                        result = when (type) {
                            ConnectivityManager.TYPE_WIFI -> true
                            ConnectivityManager.TYPE_MOBILE -> true
                            ConnectivityManager.TYPE_ETHERNET -> true
                            else -> false
                        }

                    }
                }
            }
            return result
        }

/*        *//*   progressDialog*//*
        fun loading(context: Context) {
            //final AlertDialog.Builder builder = new AlertDialog.Builder(this,android.R.style.Theme_DeviceDefault_Dialog);
            if (::pDialog.isInitialized)
                if (pDialog.isShowing) {
                    pDialog.dismiss()
                }
            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val view: View =
                inflater.inflate(R.layout.layout_progress, null)
            val logo = view.findViewById(R.id.gifImage) as GifImageView
            val pulsator = view.findViewById(R.id.pulsator) as PulsatorLayout
            pulsator.start()
            val rotate = RotateAnimation(
                0f, 360f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f
            )
            //Looper.prepare()
            //  rotate.duration = 900
            //  rotate.repeatCount = Animation.INFINITE
            // logo.startAnimation(rotate)
            pDialog = Dialog(context)
            if (!isInternetAvailable(context)) {
                Toast.makeText(context, "No Internet Connection", Toast.LENGTH_LONG).show()
                return
            }
            pDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
            pDialog.requestWindowFeature(DialogFragment.STYLE_NO_TITLE)
            pDialog.setContentView(view)
            val window: Window = pDialog.getWindow()!!
            pDialog.setCancelable(false)
            window.setLayout(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT)
            window.setGravity(Gravity.CENTER)
            Log.e("pdailog", pDialog.isShowing.toString() + " " + context.applicationContext)
            //  pDialog.show()

        }*/

        /*sharePreferences*/
        fun sharePreferences(context: Context,id:String, name:String, email:String, phone:String, image:String, address:String, password:String, token:String){
            sharedPreferences= context.getSharedPreferences(sharedPrefFile,Context.MODE_PRIVATE)
            val editor: SharedPreferences.Editor =  sharedPreferences.edit()
            editor.putString("id",id)
            editor.putString("name",name)
            editor.putString("email",email)
            editor.putString("phone",phone)
            editor.putString("image",image)
            editor.putString("address",address)
            editor.putString("password",password)
            editor.putString("token",token)
            editor.putBoolean("isLoggedIn",true);
            editor.apply()
            editor.commit()
            Log.e("Save","success")
        }
        /*sharePreferences*/
        fun saveLocation(context: Context,lat:String, long:String){
            sharedPreferences= context.getSharedPreferences(sharedLocation,Context.MODE_PRIVATE)
            val editor: SharedPreferences.Editor =  sharedPreferences.edit()
            editor.putString("lat",lat)
            editor.putString("long",long)
            editor.apply()
            editor.commit()
            Log.e("Save Location","success")
        }

        fun CustomeDialog(context: Context,optionOne:String,optionTwo:String,bodyMessage:String){
            customDialog = Dialog(context)
            customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            customDialog.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
            customDialog.setContentView(R.layout.dialog)
            customDialog.setCancelable(false)
            customeYesBtn = customDialog.findViewById(R.id.yesLogout) as Button
            customeNoBtn = customDialog.findViewById(R.id.noLogout) as Button
            val body = customDialog.findViewById(R.id.logoutMessage) as TextView
            body.setText(bodyMessage)
            customeYesBtn.setText(optionOne)
            customeNoBtn.setText(optionTwo)
        }
    }
}