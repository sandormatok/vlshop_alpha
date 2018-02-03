/*
 * Copyright (C) The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.android.gms.oem.raktar.vlscan;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.CommonStatusCodes;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.google.android.gms.oem.raktar.vlscan.BarcodeCaptureActivity.barcode3;


/**
 * Main activity demonstrating how to pass extra parameters to an activity that
 * reads barcodes.
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    // use a compound button so either checkbox or switch widgets work.
    private CompoundButton autoFocus;
    private CompoundButton useFlash;
    private CompoundButton remoteDB;

    Boolean globaladminMode = false;
    Boolean globaltorchMode = false;

    private TextView statusMessage;
    private TextView barcodeValue;
    private TextView barcodeValue2;
    private TextView barcodeInfo;
    private TextView toptextView;

    private TextView akcioValue,tableRow02,tableRow12,tableRow22,tableRow32,tableRow42,tableRow52,tableRow62,tableRow61;

    String barcode = "barcode";
    String bruttoRound,nettoRound,akcbruttoRound,akcnettoRound;;
    String globalAroszt;
    String globalvevoKod = "";

    private static final int RC_BARCODE_CAPTURE = 9001;
    private static final String TAG = "BarcodeMain";
    String url, boltnev2;
    //String boltnev = "Raktár";

    private String m_Text = "";
    private String manualInput = "NO";

     /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//notitle
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);
        //getSupportActionBar().setTitle("Raktári Készletellenörző");
        //statusMessage = (TextView) findViewById(R.id.status_message);
        //barcodeValue = (TextView) findViewById(R.id.barcode_value);
        //barcodeValue2 = (TextView) findViewById(R.id.barcode_value2);
        toptextView = (TextView) findViewById(R.id.toptextView);
        //barcodeInfo = (TextView) findViewById(R.id.status_message);

        //tablerows
        tableRow02 = (TextView) findViewById(R.id.table02);
        tableRow12 = (TextView) findViewById(R.id.table12);
        tableRow22 = (TextView) findViewById(R.id.table22);
        tableRow32 = (TextView) findViewById(R.id.table32);
        tableRow42 = (TextView) findViewById(R.id.table42);
        tableRow52 = (TextView) findViewById(R.id.table52);
        tableRow61 = (TextView) findViewById(R.id.table61);
        tableRow62 = (TextView) findViewById(R.id.table62);
//        akcioValue = (TextView) findViewById(R.id.akcioValue);

//san        tableRow22.setText("Használja telefonja hangerő fel/le gombjait a vakku bekapcsolásához beolvasás közben!");


        //autoFocus = (CompoundButton) findViewById(R.id.auto_focus);
        //useFlash = (CompoundButton) findViewById(R.id.use_flash);
        //remoteDB = (CompoundButton) findViewById(R.id.remote_db);

        //autoFocus.setChecked(true);
        //autoFocus.setVisibility(View.GONE);
        //remoteDB.setVisibility(View.GONE);
        //useFlash.setVisibility(View.GONE);

        Intent intent = getIntent();

        if(intent.hasExtra("intentvevonev")) {
            String vevonev = getIntent().getExtras().getString("intentvevonev");
            globalAroszt = getIntent().getExtras().getString("intentaroszt");
            globalvevoKod = getIntent().getExtras().getString("intentvevokod");
            //toptextView.setText("Vevő: " + vevonev);
            toptextView.setText(vevonev);
        }
/*
        if(intent.hasExtra("globaltourchmode")) {
            globaltorchMode = getIntent().getExtras().getBoolean("globaltourchmode");
        }
*/
        if(intent.hasExtra("adminMode")) {
            Boolean adminMode = getIntent().getExtras().getBoolean("adminMode");
                if(adminMode) {
                    globalvevoKod = "0045801";
                            //                    remoteDB.setVisibility(View.VISIBLE);
//                    remoteDB.setChecked(true);
                    globaladminMode = true;
                    toptextView.setText("ONLINE MÓD (4-es ár)");
            }
        }

//>>>Itt adom meg, hogy melyik gombra melyig view jelenjen meg... (layout.xml)

        findViewById(R.id.read_barcode).setOnClickListener(this);
        findViewById(R.id.enter_barcode).setOnClickListener(this);

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

//>>>Ez jó leket hogy hova kattint...     de inkább feljebb kell több view-et definiálni

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */


    //>>>Gombok megnomására más más view
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.read_barcode) {
            // launch barcode activity.
            Intent intent = new Intent(this, BarcodeCaptureActivity.class);

            /*
            intent.putExtra(BarcodeCaptureActivity.AutoFocus, autoFocus.isChecked());
            intent.putExtra(BarcodeCaptureActivity.UseFlash, useFlash.isChecked());
*/
            intent.putExtra(BarcodeCaptureActivity.AutoFocus, true);
            intent.putExtra(BarcodeCaptureActivity.UseFlash, false);

            startActivityForResult(intent, RC_BARCODE_CAPTURE);
        }
// manualinput
        if (v.getId() == R.id.enter_barcode) {
            //Toast toast= Toast.makeText(getApplicationContext(),"ANYÁDAT NYOMKODD!", Toast.LENGTH_SHORT);
            //toast.setGravity(Gravity.CENTER,0,0); toast.show();

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Írd be a vonalkódot!");

// Set up the input
            final EditText input = new EditText(this);
            // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
            input.setInputType(InputType.TYPE_CLASS_NUMBER);
            builder.setView(input);

// Set up the buttons
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    m_Text = input.getText().toString();
                    manualInput = "YES";
                    getData();
                }
            });
            builder.setNegativeButton("Mégse", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            builder.show();

        }
    }

    /**
     * Called when an activity you launched exits, giving you the requestCode
     * you started it with, the resultCode it returned, and any additional
     * data from it.  The <var>resultCode</var> will be
     * {@link #RESULT_CANCELED} if the activity explicitly returned that,
     * didn't return any result, or crashed during its operation.
     * <p/>
     * <p>You will receive this call immediately before onResume() when your
     * activity is re-starting.
     * <p/>
     *
     * @param requestCode The integer request code originally supplied to
     *                    startActivityForResult(), allowing you to identify who this
     *                    result came from.
     * @param resultCode  The integer result code returned by the child activity
     *                    through its setResult().
     * @param data        An Intent, which can return result data to the caller
     *                    (various data can be attached to Intent "extras").
     * @see #startActivityForResult
     * @see #createPendingResult
     * @see #setResult(int)
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_BARCODE_CAPTURE) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    final MediaPlayer mp = MediaPlayer.create(this, R.raw.sound3);
                    mp.start();

                    String barcode3 = data.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject);
                    //Barcode barcode2 = data.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject);
                    //statusMessage.setText(barcode2.displayValue);
                    //statusMessage.setText(barcode3);
                    getData();
                    //getakcioData();
                    Log.d(TAG, "Vonalkód (MainActivity) " + barcode3);
                } else {
                    tableRow22.setText(R.string.barcode_failure);
                    Log.d(TAG, "No barcode captured, intent data is null");
                }
            } else {
                tableRow22.setText(String.format(getString(R.string.barcode_error),
                        CommonStatusCodes.getStatusCodeString(resultCode)));
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

 //>>>URL Request, kólön RAKTÁR és TÁVOLI URL-el
    private void getData() {


        String ssid = "";
        //WIFI Bekapcsolása, csatlakozás a VLEURO wifihez

        WifiManager wifiManager=(WifiManager)getSystemService(WIFI_SERVICE);

        WifiInfo wifiInfo;
        wifiInfo = wifiManager.getConnectionInfo();
        if (wifiInfo.getSupplicantState() == SupplicantState.COMPLETED) {
            ssid = wifiInfo.getSSID();

        } else {
            ssid = "NON";
                    }

        //Toast toast= Toast.makeText(getApplicationContext(),ssid, Toast.LENGTH_SHORT);
        //toast.setGravity(Gravity.CENTER,0,0); toast.show();

        //if(ssid.compareTo("\"VLEURO\"")==0) {
        if(!ssid.equals("\"VLEURO\"")) {

            tableRow22.setText("NEM CSATLAKOZIK A \"VLEURO\" WIFI HÁLÓZATHOZ!");
            tableRow22.setBackgroundColor(Color.RED);
            tableRow12.setText("");
            tableRow32.setText("");
            tableRow42.setText("");
            tableRow52.setText("");
            return;
        }



        //final TextView barcodeInfo = (TextView) findViewById(R.id.code_info);
        final TextView barcodeInfo = (TextView) findViewById(R.id.status_message);

        String id = barcode3;

        if (manualInput.equals("YES")) {
            id = m_Text;
            manualInput = "NO";
        }

  /*      try {
            String encodedString = URLEncoder.encode(boltnev, "UTF-8");
            boltnev2 = encodedString;
            Log.d("TEST", encodedString);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } */


        if (globaladminMode) {
            url = Config.DATA_RAKTAR_KESZLET_REMOTE_URL + id + "&vkod="+globalvevoKod;
        } else {

            url = Config.DATA_RAKTAR_KESZLET_URL + id + "&vkod="+globalvevoKod;
        }

        //url = Config.DATA_RAKTAR_KESZLET_URL + id + "&vkod="+globalvevoKod;

//        Toast toast= Toast.makeText(getApplicationContext(),url, Toast.LENGTH_LONG);
//        toast.setGravity(Gravity.CENTER,0,0); toast.show();

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
                        Toast.makeText(MainActivity.this, error.getMessage().toString(), Toast.LENGTH_LONG).show();
                    }
                });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
// VONALKOD!!!
        tableRow02.setText(id);
    }


//>>>JSON feldolgozása, adatok kiirasa
    private void showJSON(String response) {

        //final TextView barcodeInfo = (TextView) findViewById(R.id.code_info);
        final TextView barcodeInfo = (TextView) findViewById(R.id.status_message);
        final TextView code_results = (TextView) findViewById(R.id.read_barcode);
        String marka = "";
        String termek = "";
        String ar = "";
        String menny = "";
        String vevonev= "";
        Double netto = 0.00;
        Double afa = 0.00;
        Boolean akcios = false;

//Válasz adatok tárolása
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray result = jsonObject.getJSONArray(Config.JSON_ARRAY);
            JSONObject termekData = result.getJSONObject(0);
            marka = termekData.getString(Config.KEY_MARKA);
            termek = termekData.getString(Config.KEY_TERMEK);
            menny = termekData.getString(Config.KEY_KESZLET);
            netto = termekData.getDouble(Config.KEY_NETTO);
            afa = termekData.getDouble(Config.KEY_AFA);
            akcios = termekData.getBoolean(Config.KEY_AKCIO);
            vevonev = termekData.getString(Config.KEY_VEVONEV);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        String akciosstring;

        if(akcios) {
             akciosstring = "igen";
        } else {
             akciosstring = "nem";
        }

        //Toast toast= Toast.makeText(getApplicationContext(),akciosstring, Toast.LENGTH_LONG);
        //toast.setGravity(Gravity.CENTER,0,0); toast.show();


        if (marka.equals("null")) {
            //barcodeValue.setTextColor(0xFFFF033A);
            tableRow22.setText("A VONALKÓD NINCS A RAKTÁRI RENDSZERBEN");
            tableRow22.setBackgroundColor(Color.RED);
            tableRow12.setText("");
            tableRow32.setText("");
            tableRow42.setText("");
            tableRow52.setText("");

        } else {
            tableRow22.setBackgroundColor(0xFF0C4593);
            Double brutto = 0.00;

            brutto = netto * (afa + 100) / 100;
            //barcodeValue.setTextColor(0xFF00DDFF);


            bruttoRound = String.format("%.2f", brutto);
            nettoRound = String.format("%.2f", netto);

         //   barcodeValue.setText(marka + "\n" + termek);
            //barcodeValue.setText(boltnev);
         //   barcodeValue2.setText("Nettó: " + netto + " Ft" + "\nBruttó: " + bruttoRound + " Ft" + "\nRaktáron: " + menny);
                        tableRow12.setText(marka);
            tableRow22.setText(termek);
            tableRow32.setText(nettoRound + " Ft");
            tableRow42.setText(bruttoRound + " Ft");
            tableRow52.setText(menny + " db");
            if(akcios){
                tableRow62.setBackgroundColor(Color.RED);
                tableRow61.setBackgroundColor(Color.RED);
                tableRow62.setText("IGEN");
            } else {
                tableRow62.setText("NEM");
                tableRow62.setBackgroundColor(0xFF0C4593);
                tableRow61.setBackgroundColor(0xFF0C4593);
            }

        }

    }



    private void getakcioData() {
        String id = globalAroszt;
         url = Config.DATA_RAKTAR_AKCIO_URL + id + "&arkat=" + globalAroszt;

        StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //        loading.dismiss();
                showJSON2
                        (response);
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, error.getMessage().toString(), Toast.LENGTH_LONG).show();
                    }
                });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }



    private void showJSON2(String response) {

        String marka = "";
        String marka1 = "";
        String termek = "";
        String termek1 = "";
        Double akcar = 0.00;
        Double akcar1 = 0.00;
//JSONArray resultArray;

        /* String kinalo = "";
        String karton = "";
        String raklap = ""; */

//Válasz adatok tárolása
        try {

            JSONObject jsonObject = new JSONObject(response);
            JSONArray result = jsonObject.getJSONArray(Config.JSON_ARRAY);

            JSONObject termekData1 = result.getJSONObject(0);
            marka = termekData1.getString(Config.KEY_MARKA);
            termek = termekData1.getString(Config.KEY_TERMEK);
            akcar = termekData1.getDouble(Config.KEY_AKCAR);

            JSONObject termekData2 = result.getJSONObject(1);
            marka1 = termekData2.getString(Config.KEY_MARKA);
            termek1 = termekData2.getString(Config.KEY_TERMEK);
            akcar1 = termekData2.getDouble(Config.KEY_AKCAR);


        } catch (JSONException e) {
            e.printStackTrace();
        }


        //Toast toast= Toast.makeText(getApplicationContext(),marka + "; " + termek + "; " + akcar + "; " + marka1 + "; " + termek1 + "; " + akcar1, Toast.LENGTH_LONG);
        //toast.setGravity(Gravity.CENTER,0,0); toast.show();

        //toptextView.setText(marka + ", " + marka2);
        //tableRow22.setText(termek);
        //tableRow32.setText(akcar);
        //tableRow42.setText(marka);
        //tableRow52.setText(marka2);

        Double afa = 1.27;

        Double brutto = 0.00;

        brutto = akcar1 * (afa + 100) / 100;
        //barcodeValue.setTextColor(0xFF00DDFF);


        akcbruttoRound = String.format("%.2f", brutto);
        akcnettoRound = String.format("%.2f", akcar1);



        akcioValue.setText(marka + " / " + termek + "\n" + akcnettoRound + " Ft (Nettó) / " + akcbruttoRound + " Ft (Bruttó)");
        akcioValue.setSelected(true);
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Main Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }


    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }
}
