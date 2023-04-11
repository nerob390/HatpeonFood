package hatpeon.app.com

import android.Manifest
import android.content.*
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.app.frammanagment.Common.Common
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import hatpeon.app.com.Activity.SignUp
import hatpeon.app.com.Fragment.HomeFragment
import hatpeon.app.com.Location.LocationPermissionHelper
import hatpeon.app.com.Location.MarkerMove
import hatpeon.app.com.Location.NetworkChangeReceiver
import hatpeon.app.com.databinding.ActivityMainBinding
import java.util.*

class MainActivity : AppCompatActivity(), LocationListener {
    lateinit var binding:ActivityMainBinding
    private var MyReceiver: BroadcastReceiver? = null
    private var locationRequest: LocationRequest? = null
    var location: Location? = null
    companion object{
        lateinit var lat:String
        lateinit var long_:String
        var myAddress:String="Searching"
    }
    private val locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)
            for (location in locationResult.locations) {
                // locationViewModel.setLocationMutableLiveData(location)
                val latitude = locationResult.lastLocation!!.latitude
                val longitude = locationResult.lastLocation!!.longitude
                val sharedPreferences: SharedPreferences = applicationContext.getSharedPreferences("Location", 0)
                val editor = sharedPreferences.edit()
                editor.clear()
                editor.apply()
                Log.e("location_update_data", "onLocationResult: $latitude+$longitude")
                Common.saveLocation(this@MainActivity,latitude.toString(),longitude.toString())
                if (HomeFragment.myAddress.text.toString().equals("Searching")){
                    getAddress(
                        applicationContext,
                        latitude,
                        longitude
                    )
                }

               // getLocationFromAddress(this@MainActivity,latitude,longitude)
                // Log.e("f", "onLocationResult: "+locationViewModel.setLocationMutableLiveData(location.getLatitude()));
                //  jsonObject.addProperty("location", );
            }
        }
    }
    private fun broadcastIntent() {
        registerReceiver(MyReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        MyReceiver = NetworkChangeReceiver()
        locationPermission()

        broadcastIntent()
        val locationRequest = LocationRequest()
        locationRequest.interval = 2000
        locationRequest.fastestInterval = 1000
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        LocationServices.getFusedLocationProviderClient(this)
            .requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
        val intent=intent
        Log.e("IntentAddress",intent.getStringExtra("myAddress").toString())
        if (intent.getStringExtra("myAddress")==null){
            Log.e("truefromIntent","yes")
              lat="default"
              long_="default"
        }else if(intent.getStringExtra("myAddress")!=null){
            HomeFragment.myAddress.setText(intent.getStringExtra("myAddress")!!.replace("null",""))
            myAddress= intent.getStringExtra("myAddress")!!
            lat= intent.getStringExtra("lat")!!
            long_=intent.getStringExtra("long")!!
        }
        else{
          HomeFragment.myAddress.setText(intent.getStringExtra("myAddress")!!.replace("null",""))
          lat= intent.getStringExtra("lat")!!
          long_=intent.getStringExtra("long")!!
        }

//        HomeFragment.locationImage.setOnClickListener {
//            startActivity(Intent(this, MarkerMove::class.java))
//        }
    }
    private fun locationPermission() {
        ActivityCompat.requestPermissions(
            this, arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            PackageManager.PERMISSION_GRANTED

        )

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    override fun onResume() {
        super.onResume()
        if (!LocationPermissionHelper.hasLocationPermission(this)) {
            LocationPermissionHelper.requestLocationPermission(this)
            return
        }

        getDeviceLastLocation()

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (!LocationPermissionHelper.hasLocationPermission(
                this
            )
        ) {
            return
        }

        getDeviceLastLocation()

    }
    private fun getDeviceLastLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        createLocationRequest()
    }
    private fun createLocationRequest() {
        locationRequest = LocationRequest.create()
        locationRequest!!.setInterval(10000)
        locationRequest!!.setFastestInterval(5000)
        locationRequest!!.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        locationRequest!!.setNumUpdates(1)
        val locationSettingsRequest = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest!!).build()
        val settingsClient = LocationServices.getSettingsClient(this)
        val responseTask = settingsClient.checkLocationSettings(locationSettingsRequest)
        responseTask.addOnSuccessListener {
            requestDeviceLocationUpdate()

        }
        responseTask.addOnFailureListener { e ->
            if (e is ResolvableApiException) {
                try {
                    e.startResolutionForResult(this@MainActivity, 222)
                } catch (e1: Exception) {
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 222 && resultCode == RESULT_OK) {
            requestDeviceLocationUpdate()
            val intent = Intent(applicationContext, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.putExtra("EXIT",true)
            startActivity(intent)
            finish()
        }
    }

    private fun requestDeviceLocationUpdate() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }

    }


    override fun onLocationChanged(location: Location) {
        Toast.makeText(
            this, "Location Changed " + location.latitude
                    + location.longitude, Toast.LENGTH_LONG
        ).show()
    }
    fun getAddress(context: Context?, LATITUDE: Double, LONGITUDE: Double) {
        //Set Address
        try {
            val geo = Geocoder(context, Locale.getDefault())
            val addresses = geo.getFromLocation(LATITUDE, LONGITUDE, 1)
            HomeFragment.myAddress.setText("Finding...")
            if (addresses != null && addresses.size > 0) {
                val locality = addresses[0].getAddressLine(0)
                val country = addresses[0].countryName
                val state = addresses[0].adminArea
                val sub_admin = addresses[0].subAdminArea
                val city = addresses[0].featureName
                val pincode = addresses[0].postalCode
                val locality_city = addresses[0].locality
                val sub_localoty = addresses[0].subLocality
                Log.e("fullAddress",sub_localoty+" "+locality_city+" "+state)
                if (locality != null && country != null) {
                    Log.e("MyAddress",locality + ", " + (if (sub_localoty != null) "$sub_localoty, " else "") + (if (locality_city != null) "$locality_city, " else "") + (if (city != null) "$city, " else "") + (if (sub_admin != null) "$sub_admin, " else "") + (if (state != null) "$state, " else "") + country + ", " + (pincode ?: ""))
/*                    mLocationText.setText(
                        locality + ", " + (if (sub_localoty != null) "$sub_localoty, " else "") + (if (locality_city != null) "$locality_city, " else "") + (if (city != null) "$city, " else "") + (if (sub_admin != null) "$sub_admin, " else "") + (if (state != null) "$state, " else "") + country + ", " + (pincode
                            ?: "")
                    )*/

                  HomeFragment.myAddress.setText(locality+sub_localoty+locality_city+state)
                  myAddress=locality+sub_localoty+locality_city+state.replace("null","")


                } else {
                   // mLocationText.setText("Location could not be fetched...")
                }
            }
        } catch (e: Exception) {
           HomeFragment.myAddress.setText("Location could not be fetched...")
            e.printStackTrace() // getFromLocation() may sometimes fail
        }
        return
    }
}