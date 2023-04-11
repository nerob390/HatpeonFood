package hatpeon.app.com.Payment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import hatpeon.app.com.MainActivity;
import hatpeon.app.com.R;

public class Payment extends AppCompatActivity {

    WebView webView;
    ProgressDialog progressDialog;
    String tranid = "ini";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        if(!isConnected(Payment.this)){
            buildDialog(Payment.this).show();
        }
        else {
            setContentView(R.layout.activity_payment);
            Intent intent = getIntent();
            progressDialog = new ProgressDialog(Payment.this);
            progressDialog.setMessage("Loading...");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
               // progressDialog.setIndeterminateDrawable(getDrawable(R.drawable.placeholder_image));
            }
            webView = findViewById(R.id.payment_view);
            WebSettings webSettings = webView.getSettings();
            webSettings.setJavaScriptEnabled(true);
            WebViewClient myClient = new WebViewClient() {
                @Override
                public void onPageStarted(WebView view, String url, Bitmap favicon) {
                    super.onPageStarted(view, url, favicon);
                    progressDialog.show();
                }
                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                    progressDialog.dismiss();
                    HashMap<String,String> responseDataOfPayment = new HashMap<>();
                    HashMap<String,String>urlResponseKeyValue = new HashMap<>();
                    Log.e("url-->", "onPageFinished: "+url );
           /*         E/url-->: onPageFinished: https://hatpeon.com/bkash/fail*/

/*                    if (url.equals("http://202.191.121.20/devtest/pclb/app-bkash-payment-success-message/"+packageid)){
*//*                        customDialog = new Dialog(Payment.this);
                        customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        customDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        customDialog.setContentView(R.layout.payment_success_layout);
                        TextView packageN = customDialog.findViewById(R.id.packageName);
                        AppCompatButton startNow = customDialog.findViewById(R.id.start);
                        packageN.setText(packageName);
                        startNow.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent1=new Intent(getApplicationContext(),MainActivity.class);
                                startActivity(intent1);
                                finish();
                            }
                        });

                        customDialog.setCancelable(false);
                        customDialog.setCanceledOnTouchOutside(false);
                        customDialog.show();*//*
                    }*/

/*                    Uri respoUri = Uri.parse(url);

                    urlResponseKeyValue = getQueryKeyValueMap(respoUri);*/

                    Log.e("urlResponseKeyValue", String.valueOf(urlResponseKeyValue));
                    tranid = urlResponseKeyValue.get("spTransID");

                }

                @Override
                public boolean shouldOverrideUrlLoading (WebView view, String url) {


                    return false;
                }

            };

            webView.getSettings().setJavaScriptEnabled(true);
            webView.getSettings().setSupportZoom(true);
            //webView.setInitialScale(98);
            //webView.getSettings().setBuiltInZoomControls(true);

            webView.setWebViewClient(myClient);

            webView.loadUrl("https://hatpeon.com/bkash/pay?amount=1");
            // Log.e("url--->", "onCreate: "+url );
            // webView.addJavascriptInterface(new MyJavaScriptInterface(this), "HtmlViewer");

        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK)
        {
            Intent intent = new Intent(Payment.this, MainActivity.class);
            finish();
            startActivity(intent);

        }
        return true;

    }

    HashMap<String, String> getQueryKeyValueMap(Uri uri){
        HashMap<String, String> keyValueMap = new HashMap();
        String key;
        String value;

        Set<String> keyNamesList = uri.getQueryParameterNames();
        Iterator iterator = keyNamesList.iterator();

        while (iterator.hasNext()){
            key = (String) iterator.next();
            value = uri.getQueryParameter(key);
            keyValueMap.put(key, value);
        }
        return keyValueMap;
    }

    public boolean isConnected(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netinfo = cm.getActiveNetworkInfo();

        if (netinfo != null && netinfo.isConnectedOrConnecting()) {
            NetworkInfo wifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            NetworkInfo mobile = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

            if((mobile != null && mobile.isConnectedOrConnecting()) || (wifi != null &&wifi.isConnectedOrConnecting())) return true;
            else return false;
        } else
            return false;
    }

    public AlertDialog.Builder buildDialog(Context c) {
        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setTitle("No Internet Connection");
        builder.setMessage("You need to have Mobile Data or wifi to access this. Press ok to Exit");
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                finish();
            }
        });

        return builder;
    }
}
