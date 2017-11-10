package com.google.android.gms.oem.raktar.atvetel;

import android.app.ProgressDialog;
import android.content.Context;
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

import static com.google.android.gms.oem.raktar.atvetel.Config.DATA_RAKTAR_KESZLET_URL;

public class LoginActivity extends AppCompatActivity {

//san

    private EditText loginInputVevokod, loginInputPassword;
    ProgressDialog progressDialog;
    private Button btnlogin;
    private static final String TAG = "LoginActivity";
    private String globalVevokod, globalPassword = "";
    String globalSsid ="null";

    private CompoundButton maradjonbeBox;

    Boolean allowLogin = true;
    Boolean adminMode = true;
    Boolean wifienable, wificonnect = false;

    public static final String MY_PREFS_NAME = "loginPrefs";


    //SharedPreferences sharedPref = LoginActivity().getPreferences(Context.MODE_PRIVATE);
    //SharedPreferences.Editor editor = sharedPref.edit();
    //editor.

    //SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);
    //pref.edit().putBoolean("check",true).commit();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("VL-SCAN Raktár");
//san
        loginInputVevokod = (EditText) findViewById(R.id.input_vevokod);
        loginInputVevokod.setInputType(InputType.TYPE_CLASS_NUMBER);
        loginInputPassword = (EditText) findViewById(R.id.input_password);
        loginInputPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        btnlogin = (Button) findViewById(R.id.btn_login);

        // Progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        maradjonbeBox = (CompoundButton) findViewById(R.id.maradjonBe);

//////////
        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        String  prefsText = prefs.getString("text", null);
        //if (prefsText != null) {
            String prefsVevokod = prefs.getString("vevokod", "");
            String prefsVevojelszo = prefs.getString("vevojelszo", "");
            Boolean prefsMaradjonbe = prefs.getBoolean("maradjon", false);
            if(prefsMaradjonbe) maradjonbeBox.setChecked(true);
        loginInputVevokod.setText(prefsVevokod, TextView.BufferType.EDITABLE);
        loginInputPassword.setText(prefsVevojelszo, TextView.BufferType.EDITABLE);

       // }





//WIFI Bekapcsolása, csatlakozás a VLEURO wifihez
        final WifiConfiguration wifiConfig = new WifiConfiguration();
        wifiConfig.SSID = String.format("\"%s\"", "VLEURO");
        wifiConfig.preSharedKey = String.format("\"%s\"", "vleurokft");
        WifiManager wifiManager=(WifiManager)getSystemService(WIFI_SERVICE);

        WifiInfo wifiInfo;
        wifiInfo = wifiManager.getConnectionInfo();
        if (wifiInfo.getSupplicantState() == SupplicantState.COMPLETED) {
            globalSsid = wifiInfo.getSSID();
        }

        //if(!globalSsid.equals("VLEURO")){
        if(globalSsid.compareTo("\"VLEURO\"")==0) {
            //Toast toast= Toast.makeText(getApplicationContext(),globalSsid, Toast.LENGTH_LONG);
            //toast.setGravity(Gravity.CENTER,0,0); toast.show();

        } else {

            allowLogin = false;
            View view = View.inflate(this, R.layout.alert_dialog_net, null);
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle("Csatlakozhatok a VL-Euro Kft. hálózatára?");
            // alert.setMessage("Message");
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

/*
        if (!wifiManager.isWifiEnabled()){
            allowLogin = false;
            View view = View.inflate(this, R.layout.alert_dialog_net, null);
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle("Csatlakozhatok a VL-Euro Kft. hálózatára?");
            // alert.setMessage("Message");
            final WifiManager wifiManager2=(WifiManager)getSystemService(WIFI_SERVICE);

            alert.setPositiveButton("Kilépés", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    finish();
                    System.exit(0);
                }
            });

            alert.setNegativeButton("Csatlakozás",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            wifiManager2.setWifiEnabled(true);

                            int netId = wifiManager2.addNetwork(wifiConfig);
                            wifiManager2.disconnect();
                            wifiManager2.enableNetwork(netId, true);
                            wifiManager2.reconnect();
                            allowLogin = true;
                        }
                    });

            alert.show();

        }
*/


        btnlogin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                loginUser(loginInputVevokod.getText().toString(),
                        loginInputPassword.getText().toString());
            }

        });
    }


    private void loginUser( final String vevokod, final String password) {

        adminMode = false;

        if (vevokod.equals("11111")) {
            if (password.equals("99999")) {
                Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
                mainIntent.putExtra("adminMode", true);
                adminMode = true;
                startActivity(mainIntent);
                //setResult(CommonStatusCodes.SUCCESS, mainIntent);
                //finish();
            }
        }


/*        if(!allowLogin) {
            finish();
            System.exit(0);
        }
*/
        progressDialog.setMessage("Bejelentkezés folyamatban...");
        showDialog();
        globalVevokod = vevokod;
        globalPassword = password;







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

        //String akcurl = Config.DATA_RAKTAR_AKCIO_URL
        //Toast toast= Toast.makeText(getApplicationContext(),url, Toast.LENGTH_LONG);
        //toast.setGravity(Gravity.CENTER,0,0); toast.show();

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

    //>>>JSON feldolgozása, adatok kiirasa
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
        Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);


//Write to shared preferences
            if (maradjonbeBox.isChecked()) {
                SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                editor.putString("vevokod", globalVevokod);
                editor.putString("vevojelszo", globalPassword);
                editor.putBoolean("maradjon", true);
                editor.apply();
            } else {
                SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                editor.clear();
                editor.apply();
            }

/*
            SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
            String  prefsText = prefs.getString("text", null);
            //if (prefsText != null) {
            String prefsVevokod = prefs.getString("vevokod", "");
            String prefsVevojelszo = prefs.getString("vevojelszo", "");
            Boolean prefsMaradjonbe = prefs.getBoolean("maradjon", false);
            if(prefsMaradjonbe) maradjonbeBox.setChecked(true);

            // }
*/

            hideDialog();


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
