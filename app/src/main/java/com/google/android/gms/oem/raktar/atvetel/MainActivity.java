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

package com.google.android.gms.oem.raktar.atvetel;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import static com.google.android.gms.oem.raktar.atvetel.BarcodeCaptureActivity.barcode3;
import static com.google.android.gms.oem.raktar.atvetel.Config.DATA_RAKTAR_KESZLET_URL;

/**
 * Main activity demonstrating how to pass extra parameters to an activity that
 * reads barcodes.
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    // use a compound button so either checkbox or switch widgets work.


    private CompoundButton autoFocus;
    private CompoundButton useFlash;



    private TextView statusMessage;
    private TextView barcodeValue;
    private TextView barcodeValue2;
    String barcode = "barcode";

    private static final int RC_BARCODE_CAPTURE = 9001;
    private static final String TAG = "BarcodeMain";
    String url, boltnev2;
    String boltnev = "Raktár";

    private String m_Text = "";
    private String manualInput = "NO";

    //class members
    String countries[] = {"Raktár", "Erzsébet 1.", "Alkotás", "Bartók 71.", "Békéscsaba 1.", "Békéscsaba 2.", "Bicske", "Csepel", "Csongrád", "Damjanich", "Debrecen 1.", "Debrecen 2.", "Dékán", "Dunaújváros", "Erzsébet 2.", "Fehérvári út", "Ferenc Krt.", "Gyula", "Harminckettesek", "Hattyú", "K?rösi", "Karcag", "Kecskemét 1.", "Kecskemét 2.", "Kisk?rös", "Környe", "Mester u.", "Mez?túr", "Nagyk?rös", "Orosháza", "Oroszlány 1.", "Oroszlány 2.", "Pablo Neruda", "Paks", "Pécs 1.", "Pécs 2", "Piliscsaba", "Püspökladány", "Szeged 1.", "Szeged 2.", "Tatabánya 1.", "Tatabánya 2.", "Tatabánya 3.", "Teréz krt.", "Tolna", "Újhegyi stny.", "Újpest"};
    ArrayAdapter<String> adapter;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);

        //getSupportActionBar().setTitle("Raktári Készletellenörző");
        statusMessage = (TextView) findViewById(R.id.status_message);
        barcodeValue = (TextView) findViewById(R.id.barcode_value);
        barcodeValue2 = (TextView) findViewById(R.id.barcode_value2);
        autoFocus = (CompoundButton) findViewById(R.id.auto_focus);
        useFlash = (CompoundButton) findViewById(R.id.use_flash);
        autoFocus.setChecked(true);

        // Application of the Array to the Spinner

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
            intent.putExtra(BarcodeCaptureActivity.AutoFocus, autoFocus.isChecked());
            intent.putExtra(BarcodeCaptureActivity.UseFlash, useFlash.isChecked());

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
                    statusMessage.setText(barcode3);
                    getData();
                    Log.d(TAG, "Vonalkód (MainActivity) " + barcode3);
                } else {
                    statusMessage.setText(R.string.barcode_failure);
                    Log.d(TAG, "No barcode captured, intent data is null");
                }
            } else {
                statusMessage.setText(String.format(getString(R.string.barcode_error),
                        CommonStatusCodes.getStatusCodeString(resultCode)));
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


 //>>>URL Request, kólön RAKTÁR és BOLTI URL-el

    private void getData() {
        //final TextView barcodeInfo = (TextView) findViewById(R.id.code_info);
        final TextView barcodeInfo = (TextView) findViewById(R.id.status_message);

        String id = barcode3;

        if (manualInput.equals("YES")) {
            id = m_Text;
            manualInput = "NO";
        }

        try {

            String encodedString = URLEncoder.encode(boltnev, "UTF-8");
            boltnev2 = encodedString;
            Log.d("TEST", encodedString);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String url = Config.DATA_URL + id + "&bkod=" + boltnev2;

        if (boltnev.equals("Raktár")) {
            url = DATA_RAKTAR_KESZLET_URL + id;
        }

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
        barcodeInfo.setText(id);
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
        Double netto = 0.00;
        Double afa = 0.00;
        Boolean akcios = false;



        /* String kinalo = "";
        String karton = "";
        String raklap = ""; */

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




          /*  ar = termekData.getString(Config.KEY_KESZLET);
            kinalo = termekData.getString(Config.KEY_KINALO);
            karton = termekData.getString(Config.KEY_KARTON);
            raklap = termekData.getString(Config.KEY_RAKLAP); */


        } catch (JSONException e) {
            e.printStackTrace();
        }

        //textViewResult.setText(getString(R.string.results_marka)+marka+getString(R.string.results_termek) +termek);
        //textViewResult.setText(getString(R.string.results_vonalkod)+barcode+getString(R.string.results_marka)+marka+getString(R.string.results_termek) +termek);

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
        //getSupportActionBar().setTitle(ar+"db ("+termek+")");
        //sleepAwhile();
        //textViewResult.setText(getString(R.string.results_marka)+marka+getString(R.string.results_termek) +termek+ getString(R.string.results_ar)+ ar);
        //barcodeInfo.setText(getString(R.string.results_marka)+marka+getString(R.string.results_termek) +termek+ getString(R.string.results_ar)+ ar);
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
