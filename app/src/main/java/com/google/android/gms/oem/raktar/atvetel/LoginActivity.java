package com.google.android.gms.oem.raktar.atvetel;

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
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.google.android.gms.oem.raktar.atvetel.Config.URL_FOR_LOGIN;

public class LoginActivity extends AppCompatActivity {

//san

    private EditText loginInputVevokod, loginInputPassword;
    ProgressDialog progressDialog;
    private Button btnlogin;
    private static final String TAG = "LoginActivity";


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
        progressDialog.setMessage(vevokod + ", " + password);
        showDialog();

        Toast toast= Toast.makeText(getApplicationContext(),vevokod + ", " + password, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.BOTTOM,0,0); toast.show();


         hideDialog();



        /* StringRequest strReq = new StringRequest(Request.Method.POST,
                URL_FOR_LOGIN, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Register Response: " + response.toString());
                hideDialog();
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    if (!error) {
                        String user = jObj.getJSONObject("user").getString("name");
                        // Launch User activity
                        Intent intent = new Intent(
                                LoginActivity.this,
                                UserActivity.class);
                        intent.putExtra("username", user);
                        startActivity(intent);
                        finish();
                    } else {

                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Login Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting params to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", email);
                params.put("password", password);
                return params;
            }
        };
        // Adding request to request queue
        AppSingleton.getInstance(getApplicationContext()).addToRequestQueue(strReq,cancel_req_tag);

        */
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
