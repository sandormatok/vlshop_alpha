package com.google.android.gms.oem.raktar.atvetel;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.api.CommonStatusCodes;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import static android.R.attr.data;
import static android.R.attr.password;
import static com.google.android.gms.oem.raktar.atvetel.BarcodeCaptureActivity.BarcodeObject;
import static com.google.android.gms.oem.raktar.atvetel.BarcodeCaptureActivity.barcode3;
import static com.google.android.gms.oem.raktar.atvetel.Config.DATA_RAKTAR_KESZLET_URL;
import static com.google.android.gms.oem.raktar.atvetel.Config.URL_FOR_LOGIN;

public class LoginActivity extends AppCompatActivity {

//san

    private EditText loginInputVevokod, loginInputPassword;
    ProgressDialog progressDialog;
    private Button btnlogin;
    private static final String TAG = "LoginActivity";
    private String globalVevokod, globalPassword = "";
    Boolean adminMode = false;
    SharedPreferences mPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//san
        loginInputVevokod = (EditText) findViewById(R.id.input_vevokod);
        loginInputVevokod.setInputType(InputType.TYPE_CLASS_NUMBER);

        loginInputPassword = (EditText) findViewById(R.id.input_password);
        loginInputPassword.setInputType(InputType.TYPE_CLASS_NUMBER);

        btnlogin = (Button) findViewById(R.id.btn_login);

        // Progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        //gombnyomás-kor loginuser fügvényt meghívja
        btnlogin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                loginUser(loginInputVevokod.getText().toString(),
                        loginInputPassword.getText().toString());
            }

        });
    }


    private void loginUser( final String vevokod, final String password) {
        //progressDialog.setMessage("Bejelentkezés folyamatban...");

        globalVevokod = vevokod;
        globalPassword = password;

        if (globalVevokod.equals("11111111")) {
            if (globalPassword.equals("99999999")) {

                Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
                mainIntent.putExtra("adminMode", true);
                adminMode = true;
                startActivity(mainIntent);
                //setResult(CommonStatusCodes.SUCCESS, mainIntent);
                //finish();
            }
        }

//        Intent data = new Intent();
//        data.putExtra(BarcodeObject, barcode3);

        if(!adminMode){
            getData2();
        }

    }

//
    private void getData2() {
        //final TextView barcodeInfo = (TextView) findViewById(R.id.code_info);
        //final TextView barcodeInfo = (TextView) findViewById(R.id.status_message);

        String id = "5997076721852";
        String url = DATA_RAKTAR_KESZLET_URL + id + "&vkod=" + globalVevokod;


/* toast
        Toast toast= Toast.makeText(getApplicationContext(),url, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER,0,0); toast.show();
*/

        StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //        loading.dismiss();
                showJSON(response);
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(LoginActivity.this, error.getMessage().toString(), Toast.LENGTH_LONG).show();
                    }
                });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
        //barcodeInfo.setText(id);
    }

    //>>>JSON feldolgozása, adatok kiirasa
    private void showJSON(String response) {

           String vevonev,jujel = "";
        vevonev = "Teszt";


//Válasz adatok tárolása
        try {
            JSONObject jsonObject2 = new JSONObject(response);
            JSONArray result2 = jsonObject2.getJSONArray(Config.JSON_ARRAY);
            JSONObject termekData2 = result2.getJSONObject(0);
            jujel = termekData2.getString(Config.KEY_JUJEL);
            vevonev = termekData2.getString(Config.KEY_VEVONEV);

            //hideDialog();


        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (globalPassword.equals(jujel)){
        Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);





            mainIntent.putExtra("intentvevonev", vevonev);

            setResult(CommonStatusCodes.SUCCESS, mainIntent);
            startActivity(mainIntent);
            finish();
        } else {
            Toast toast= Toast.makeText(getApplicationContext(),"Hibás vevőkód, vagy jelszó!", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.BOTTOM,0,20); toast.show();

        }

        //getSupportActionBar().setTitle(ar+"db ("+termek+")");
        //sleepAwhile();
        //textViewResult.setText(getString(R.string.results_marka)+marka+getString(R.string.results_termek) +termek+ getString(R.string.results_ar)+ ar);
        //barcodeInfo.setText(getString(R.string.results_marka)+marka+getString(R.string.results_termek) +termek+ getString(R.string.results_ar)+ ar);
    }



    private void showDialog() {
        if (!progressDialog.isShowing())
            progressDialog.show();
    }
    private void hideDialog() {
        if (progressDialog.isShowing())
            progressDialog.dismiss();
    }












 //san közé kell a cucc
}
