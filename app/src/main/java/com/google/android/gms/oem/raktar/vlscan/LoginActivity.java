package com.google.android.gms.oem.raktar.vlscan;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.api.CommonStatusCodes;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.google.android.gms.oem.raktar.vlscan.Config.DATA_RAKTAR_KESZLET_URL;

public class LoginActivity extends AppCompatActivity {

    //*** LOGIN - GLOBÁLIS VÁLTOZÓK ***
    private String globalVevokod, globalPassword = "";
    private EditText loginInputVevokod, loginInputPassword;
    String globalSsid ="null";
    String barcode3, barcode4 = "";
    private CompoundButton maradjonbeBox;
    Boolean allowLogin = true;
    Boolean qrcodeLogin = false;
    public static final String MY_PREFS_NAME = "loginPrefs";
    private static final String TAG = "LoginActivity";
    private static final int RC_QRCODE_LOGIN = 9001;
    private TextView vevonevLogin;

    //*** LOGIN ONCREATE ***
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //TOOLBAR
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("VL-SCAN Raktár");

        //UI ELEMEK
        loginInputVevokod = (EditText) findViewById(R.id.input_vevokod);
        loginInputVevokod.setInputType(InputType.TYPE_CLASS_NUMBER);
        loginInputPassword = (EditText) findViewById(R.id.input_password);
        loginInputPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        Button btnlogin = (Button) findViewById(R.id.btn_login);
        Button btnQRCode = (Button) findViewById(R.id.btn_qrcode);
        maradjonbeBox = (CompoundButton) findViewById(R.id.maradjonBe);

        //SHARED PREFERENCES
        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        String prefsVevokod = prefs.getString("vevokod", "");
        String prefsVevojelszo = prefs.getString("vevojelszo", "");
        Boolean prefsMaradjonbe = prefs.getBoolean("maradjon", false);
        if(prefsMaradjonbe) maradjonbeBox.setChecked(true);
        loginInputVevokod.setText(prefsVevokod, TextView.BufferType.EDITABLE);
        loginInputPassword.setText(prefsVevojelszo, TextView.BufferType.EDITABLE);

        //WIFI KONFIGURÁCIÓ
        final WifiConfiguration wifiConfig = new WifiConfiguration();
        wifiConfig.SSID = String.format("\"%s\"", "VLEURO");
        wifiConfig.preSharedKey = String.format("\"%s\"", "vleurokft");
        WifiManager wifiManager=(WifiManager)getSystemService(WIFI_SERVICE);

        WifiInfo wifiInfo;
        wifiInfo = wifiManager.getConnectionInfo();
        if (wifiInfo.getSupplicantState() == SupplicantState.COMPLETED) {
            globalSsid = wifiInfo.getSSID();
        }

        //WIFI ELLENŐRZÉS
        if(!globalSsid.equals("\"VLEURO\"")) {
            allowLogin = false;
            View view = View.inflate(this, R.layout.alert_dialog_net, null);

            //CSATLAKOZÁS A VLEURO WIFI-HEZ
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle("Csatlakozhatok a VL-Euro Kft. hálózatára?");
            final WifiManager wifiManager3=(WifiManager)getSystemService(WIFI_SERVICE);
            alert.setPositiveButton("Kilépés", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    finish();
                    System.exit(0);
                }
            });
            alert.setNegativeButton("Csatlakozás",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            wifiManager3.setWifiEnabled(true);

                            int netId = wifiManager3.addNetwork(wifiConfig);
                            wifiManager3.disconnect();
                            wifiManager3.enableNetwork(netId, true);
                            wifiManager3.reconnect();
                            allowLogin = true;
                        }
                    });
            alert.show();
        }

        //BEJELENTKEZÉS GOMB
        btnlogin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                loginUser(loginInputVevokod.getText().toString(),
                        loginInputPassword.getText().toString());
            }

        });

        //BEJELENTKEZÉS QRKÓDDAL
        btnQRCode.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent qrcodeReader = new Intent(LoginActivity.this, BarcodeCaptureActivity.class);
                qrcodeReader.putExtra("qrcodeLogin", true);
                startActivityForResult(qrcodeReader, RC_QRCODE_LOGIN);
                //
            }

        });
    }

    //*** ON ACTIVITY RESULT: RC_QRCODE_LOGIN ***
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_QRCODE_LOGIN) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    barcode3 = data.getStringExtra("barcode3");
                    qrcodeLogin = true;

                    //QRCODE FELDOLGOZÁSA
                    String[] separated = barcode3.split(":");
                    globalVevokod = separated[1];
                    globalPassword = separated[2];

                    Toast toast= Toast.makeText(getApplicationContext(),globalVevokod + ",,,," + globalPassword, Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.BOTTOM,0,20); toast.show();

                    loginInputVevokod.setText(globalVevokod, TextView.BufferType.EDITABLE);
                    loginInputPassword.setText(globalPassword, TextView.BufferType.EDITABLE);

                    Log.d(TAG, "Vonalkód (LoginAvtivity) " + barcode3);
                } else {
                    Log.d(TAG, "No barcode captured, intent data is null");
                }
            } else {
//
               }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


    //*** LOGIN ***
    private void loginUser( final String vevokod, final String password) {

        if (qrcodeLogin) {
            getData2();
        } else {
            globalVevokod = vevokod;
            globalPassword = password;
            getData2();
        }

    }

    //*** LOGIN GET DATA ***
    private void getData2() {
        String id = "5997076721852";
        String url = DATA_RAKTAR_KESZLET_URL + id + "&vkod=" + globalVevokod;

        StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //        loading.dismiss();
                showJSON(response);
            }
        },
                new Response.ErrorListener() {
                    @Override
                    //Todo: meg kell nezni, hogy van-e error message mielott tostringet hivsz ra....
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(LoginActivity.this, error.getMessage().toString(), Toast.LENGTH_LONG).show();
                    }
                });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
        //barcodeInfo.setText(id);
    }

    //*** LOGIN SHOWJSON ***
    private void showJSON(String response) {

        String aroszt = "";
        String vevonev = "";
        String jujel = "";

//Válasz adatok tárolása
        try {
            JSONObject jsonObject2 = new JSONObject(response);
            JSONArray result2 = jsonObject2.getJSONArray(Config.JSON_ARRAY);
            JSONObject termekData2 = result2.getJSONObject(0);
            jujel = termekData2.getString(Config.KEY_JUJEL);
            vevonev = termekData2.getString(Config.KEY_VEVONEV);
            aroszt = termekData2.getString(Config.KEY_AROSZT);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (globalPassword.equals(jujel)){
            //Write to shared preferences
            if (maradjonbeBox.isChecked()) {
                SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                editor.putString("vevokod", globalVevokod);
                editor.putString("vevonev", vevonev);
                editor.putString("vevojelszo", globalPassword);
                editor.putBoolean("maradjon", true);
                editor.apply();
            } else {
                SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                editor.clear();
                editor.apply();
            }

            Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
            mainIntent.putExtra("intentvevonev", vevonev);
            mainIntent.putExtra("intentaroszt", aroszt);
            mainIntent.putExtra("intentvevokod", globalVevokod);
            setResult(CommonStatusCodes.SUCCESS, mainIntent);
            startActivity(mainIntent);
            finish();
        } else {
            Toast toast= Toast.makeText(getApplicationContext(),"Hibás vevőkód, vagy jelszó!", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.BOTTOM,0,20); toast.show();
        }
    }

}