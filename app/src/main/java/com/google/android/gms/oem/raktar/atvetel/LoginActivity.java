package com.google.android.gms.oem.raktar.atvetel;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import static android.R.attr.password;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//san
        loginInputVevokod = (EditText) findViewById(R.id.input_vevokod);
        loginInputPassword = (EditText) findViewById(R.id.input_password);
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
        // Tag used to cancel the request
//
        //String cancel_req_tag = "login";
        //progressDialog.setMessage("Bejelentkezés folyamatban...");


/*
        progressDialog.setMessage(vevokod + ", " + password);
        showDialog();
*/


/* beírt adatok kiiratása
        Toast toast= Toast.makeText(getApplicationContext(),vevokod + ", " + password, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.BOTTOM,0,0); toast.show();
*/

        globalVevokod = vevokod;
        globalPassword = password;
        getData2();
    }

//
    private void getData2() {
        //final TextView barcodeInfo = (TextView) findViewById(R.id.code_info);
        //final TextView barcodeInfo = (TextView) findViewById(R.id.status_message);

        String id = "5997076721852";


//ha az id-t nem kell URL encode-olni, akkor a vevokodot sem!

/*        try {

            String encodedString = URLEncoder.encode(vevokod, "UTF-8");
            boltnev2 = encodedString;
            Log.d("TEST", encodedString);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
*/

//bolti URL nem kell most
        //String url = Config.DATA_URL + id + "&bkod=" + boltnev2;

        //if (boltnev.equals("Raktár")) {
        String url = DATA_RAKTAR_KESZLET_URL + id + "&vkod=" + globalVevokod;
        //}

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

        //final TextView barcodeInfo = (TextView) findViewById(R.id.code_info);
        //final TextView barcodeInfo = (TextView) findViewById(R.id.status_message);
        //final TextView code_results = (TextView) findViewById(R.id.read_barcode);

        /*
        String marka = "";
        String termek = "";
        String ar = "";
        String menny = "";
        Double netto = 0.00;
        Double afa = 0.00;
        Boolean akcios = false;
        */


        /* String kinalo = "";
        String karton = "";
        String raklap = ""; */

        //Double netto = 0.00;
        String jujel = "";


//Válasz adatok tárolása
        try {
            JSONObject jsonObject2 = new JSONObject(response);
            JSONArray result2 = jsonObject2.getJSONArray(Config.JSON_ARRAY);
            JSONObject termekData2 = result2.getJSONObject(0);
            //netto = termekData.getDouble(Config.KEY_NETTO);
             jujel = termekData2.getString(Config.KEY_JUJEL);

            hideDialog();

/* toast
            Toast toast= Toast.makeText(getApplicationContext(),jujel, Toast.LENGTH_LONG);
            toast.setGravity(Gravity.BOTTOM,0,0); toast.show();
*/
          /*  ar = termekData.getString(Config.KEY_KESZLET);
            kinalo = termekData.getString(Config.KEY_KINALO);
            karton = termekData.getString(Config.KEY_KARTON);
            raklap = termekData.getString(Config.KEY_RAKLAP); */


        } catch (JSONException e) {
            e.printStackTrace();
        }

        //textViewResult.setText(getString(R.string.results_marka)+marka+getString(R.string.results_termek) +termek);
        //textViewResult.setText(getString(R.string.results_vonalkod)+barcode+getString(R.string.results_marka)+marka+getString(R.string.results_termek) +termek);

        /*
        if (marka.equals("null")) {
            //barcodeValue.setTextColor(0xFFFF033A);
            barcodeValue.setText("A VONALKÓD NINCS A RAKTÁRI RENDSZERBEN");
            barcodeValue2.setText("");
        } else {
            Double brutto = 0.00;

            brutto = netto * (afa + 100) / 100;
            //barcodeValue.setTextColor(0xFF00DDFF);
            barcodeValue.setText(marka + "\n" + termek);
            //barcodeValue.setText(boltnev);
            barcodeValue2.setText("Nettó: " + netto + " Ft" + "\nBruttó: " + brutto + " Ft" + "\nRaktáron: " + menny);
        }

        */

        // beírt adatok kiiratása




        if (globalPassword.equals(jujel)){
        Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(mainIntent);
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
