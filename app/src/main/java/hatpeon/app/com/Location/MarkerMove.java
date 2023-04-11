package hatpeon.app.com.Location;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.model.Place;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import hatpeon.app.com.MainActivity;
import hatpeon.app.com.R;

public class MarkerMove extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private static String TAG = "MAP LOCATION";
    Context mContext;
   // TextView mLocationMarkerText;
    public LatLng mCenterLatLong;
    TextView mLocationText;
    private static final int REQUEST_CODE_AUTOCOMPLETE = 1;
    Toolbar mToolbar;
    AppCompatButton confirmLocation;
    String myAddress,lat,long_;
    ImageView back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        mContext = this;
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mLocationText = (TextView) findViewById(R.id.locationMarkertext);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);           //sss
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
       // getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        back=findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             onBackPressed();
            }
        });

        getSupportActionBar().setTitle(getResources().getString(R.string.app_name));
        confirmLocation=findViewById(R.id.confirmLocation);
        confirmLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("myAddress",myAddress);
                intent.putExtra("lat",lat);
                intent.putExtra("long",long_);
                intent.putExtra("EXIT",true);
                startActivity(intent);
                finish();
            }
        });
        mLocationText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

              //  openAutocompleteActivity();

            }


        });
        mapFragment.getMapAsync(this);



            if (!AppUtils.isLocationEnabled(mContext)) {
                // notify user
                AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
                dialog.setMessage("Location not enabled!");
                dialog.setPositiveButton("Open location settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(myIntent);
                    }
                });
                dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        // TODO Auto-generated method stub

                    }
                });
                dialog.show();
            }
            buildGoogleApiClient();


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "OnMapReady");
        mMap = googleMap;
        mMap.setInfoWindowAdapter(new InfoWindowAdapter(this));
        mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                Log.d("Camera postion change" + "", cameraPosition + "");
/*                confirmLocation.setText("Chhecking...");
                confirmLocation.setTextColor(getResources().getColor(R.color.shadow_black));*/
                mCenterLatLong = cameraPosition.target;


                 mMap.clear();
                Location mLocation = new Location("");
                mLocation.setLatitude(mCenterLatLong.latitude);
                mLocation.setLongitude(mCenterLatLong.longitude);

              //  mLocationMarkerText.setText("Lat : " + mCenterLatLong.latitude + "," + "Long : " + mCenterLatLong.longitude);
                getLocationFromAddress(getApplicationContext(),Double.parseDouble(String.valueOf(mCenterLatLong.latitude)), Double.parseDouble(String.valueOf(mCenterLatLong.longitude)));
                getAddress(getApplicationContext(),Double.parseDouble(String.valueOf(mCenterLatLong.latitude)), Double.parseDouble(String.valueOf(mCenterLatLong.longitude)));
                drawMarker(Double.parseDouble(String.valueOf(mCenterLatLong.latitude)),Double.parseDouble(String.valueOf(mCenterLatLong.longitude)), getText(R.string.i_am_here).toString());
            }


        });
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }

    }

    @Override
    public void onConnected(Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            changeMap(mLastLocation);
            Log.d(TAG, "ON connected");

        } else
            try {
                LocationServices.FusedLocationApi.removeLocationUpdates(
                        mGoogleApiClient, this);

            } catch (Exception e) {
                e.printStackTrace();
            }
        try {
            LocationRequest mLocationRequest = new LocationRequest();
            mLocationRequest.setInterval(10000);
            mLocationRequest.setFastestInterval(5000);
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, this);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {
        try {
            if (location != null)
                changeMap(location);
            LocationServices.FusedLocationApi.removeLocationUpdates(
                    mGoogleApiClient, this);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }


    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        try {
            mGoogleApiClient.connect();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {

        } catch (RuntimeException e) {
            e.printStackTrace();
        }
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }


    private void changeMap(Location location) {

        Log.d(TAG, "Reaching map" + mMap);


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        // check if map is created successfully or not
        if (mMap != null) {
            mMap.getUiSettings().setZoomControlsEnabled(false);
            LatLng latLong;


            latLong = new LatLng(location.getLatitude(), location.getLongitude());

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(latLong).zoom(15).tilt(70).build();

            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
            mMap.animateCamera(CameraUpdateFactory
                    .newCameraPosition(cameraPosition));

         //   mLocationMarkerText.setText("Lat : " + location.getLatitude() + "," + "Long : " + location.getLongitude());



        } else {
            Toast.makeText(getApplicationContext(),
                    "Sorry! unable to create maps", Toast.LENGTH_SHORT)
                    .show();
        }

    }






    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Check that the result was from the autocomplete widget.
        if (requestCode == REQUEST_CODE_AUTOCOMPLETE) {
            if (resultCode == RESULT_OK) {
                // Get the user's selected place from the Intent.
                Place place = null;

                // TODO call location based filter


                LatLng latLong;


                latLong = place.getLatLng();

                //mLocationText.setText(place.getName() + "");

                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(latLong).zoom(15).tilt(70).build();

                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling

                    return;
                }
                mMap.setMyLocationEnabled(true);
                mMap.animateCamera(CameraUpdateFactory
                        .newCameraPosition(cameraPosition));


            }


        }
    }
    public LatLng getLocationFromAddress(Context context, double l1, double l2) {

        Geocoder coder = new Geocoder(context);
        List<Address> address;
        LatLng p1 = null;

        try {
            // May throw an IOException
            address = coder.getFromLocation(l1,l2, 5);
            if (address == null) {
                return null;
            }

            Address location = address.get(0);
            String addressLine1 = location.getAddressLine(0);
            //  Log.e("line1", addressLine1);

            String city = location.getLocality();
            // Log.e("city", city);

            String state = location.getAdminArea();
            // Log.e("state", state);

            String pinCode = location.getPostalCode();
            //     Log.e("pinCode", pinCode);

            String fullAddress = addressLine1 + ",  " + city + ",  " + state + ",  " + pinCode;
            //tvLocation.setText(fullAddress);
            Log.e("selectedAddress", fullAddress);
            p1 = new LatLng(location.getLatitude(), location.getLongitude() );

        } catch (IndexOutOfBoundsException | IOException ex) {

            ex.printStackTrace();
        }

        return p1;
    }
    private void drawMarker(double lat,double long_, String title) {
        if (mMap != null) {
         mMap.clear();
            int height = 100;
            int width = 80;
            Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable. hat_loc);
            Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
            BitmapDescriptor smallMarkerIcon = BitmapDescriptorFactory.fromBitmap(smallMarker);
            LatLng latLng = new LatLng(lat,long_);
              MarkerOptions markerOptions = new MarkerOptions();
              markerOptions.position(latLng);
              markerOptions.title(title);
              markerOptions.icon(smallMarkerIcon);
              mMap.addMarker(markerOptions).showInfoWindow();
//            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
//            mMap.animateCamera(CameraUpdateFactory.zoomTo(12));
        }
    }

    public void getAddress(Context context, double LATITUDE, double LONGITUDE){
        //Set Address
        try {
            Geocoder geo = new Geocoder(context, Locale.getDefault());
            List<Address> addresses = geo.getFromLocation(LATITUDE, LONGITUDE, 1);
            mLocationText.setText("Loading...");
            if (addresses != null && addresses.size() > 0) {
                String locality = addresses.get(0).getAddressLine(0);
                String country = addresses.get(0).getCountryName();
                String state = addresses.get(0).getAdminArea();
                String sub_admin = addresses.get(0).getSubAdminArea();
                String city = addresses.get(0).getFeatureName();
                String pincode = addresses.get(0).getPostalCode();
                String locality_city = addresses.get(0).getLocality();
                String sub_localoty = addresses.get(0).getSubLocality();
                if (locality != null && country != null) {
                    mLocationText.setText(locality + ", " + (sub_localoty != null ? sub_localoty + ", " : "")  + (locality_city != null ? locality_city + ", " : "" ) + (city != null ? city + ", " : "")  + (sub_admin != null ? sub_admin + ", " : "") + (state != null ? state + ", " : "") + country + ", " + (pincode != null ? pincode : ""));
                    myAddress=locality+sub_localoty+locality_city+state;
                    lat=String.valueOf(LATITUDE);
                    long_=String.valueOf(LONGITUDE);
                } else {
                    mLocationText.setText("Location could not be fetched...");
                }
            }
        } catch (Exception e) {
            mLocationText.setText("Location could not be fetched...");
            e.printStackTrace(); // getFromLocation() may sometimes fail
        }
        return;
    }
}