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
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.google.android.gms.appindexing.AppIndex;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.google.android.gms.oem.raktar.vlscan.Config.DATA_RAKTAR_KESZLET_URL;
import static com.google.android.gms.oem.raktar.vlscan.Config.DATA_RAKTAR_KESZLET_URL_ONLINE;
import static com.google.android.gms.oem.raktar.vlscan.R.id.itemListView;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    //*** MAIN UI ELEMEK ***

    //private TextView tableRow02,tableRow12,tableRow22,tableRow32,tableRow42,tableRow52,tableRow62,tableRow61;
    //aaa
    //GLOBÁLIS VÁLTOZÓK
    String bruttoRound,nettoRound;
    String globalAroszt,url;
    public static String barcode3;
    String globalvevoKod = "";
    private static final int RC_BARCODE_CAPTURE = 9001;

//san suriel ADMIN VEVŐKÓDOK: (egyenlóre a LoginActivity-n is meg kell őket adni)
    String[] adminok = new String[]{"0120401"};
    List<String> adminokList = Arrays.asList(adminok);

    private static final String TAG = "BarcodeMain";
    private String manualInput = "NO";
    boolean devMode = false;

    private String m_Text,m_Text2 = "";

//san suriel global variables for showJSON
String marka = "";
    String termek = "";
    String ar = "";
    String nettoString = "";
    //String realnettoString ="";
    String afaString = "";
    String menny = "";
    String vevonev= "";
    Double netto = 0.00;
    Double afa = 0.00;
    Boolean akcios = false;
    //
    String akciosstring;

//suriel vl_shop ListView array-ek
    ArrayList<String> barcodeList = new ArrayList<String>();
    ArrayList<String> markaList = new ArrayList<String>();
    ArrayList<String> termekList = new ArrayList<String>();
    ArrayList<String> mennyisegList = new ArrayList<String>();
    ArrayList<String> shopammountList = new ArrayList<String>();
    ArrayList<String> nettoStringList = new ArrayList<String>();
    ArrayList<String> afaStringList = new ArrayList<String>();
    ArrayList<String> vevonevList = new ArrayList<String>();
    ArrayList<Boolean> akciosList = new ArrayList<Boolean>();
    //mergedlist
    ArrayList<String> mergedList = new ArrayList<String>();
    //extra ???
    ArrayList<String> barcodeListExtra = new ArrayList<String>();

    //String Arrays
    String barcodeArray[];
    String barcodeExtraArray[];
    String mennyisegArray[];
    String markaArray[];
    String termekArray[];
    String shopammountArray[];
    String nettoStringArray[];
    String afaStringArray[];
    String vevonevArray[];
    Boolean akciosArray[];

    //mergedArray
    String mergedArray[];


    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    //todo: visszaírás mysql-be (php, direkt, ...?)

    //*** MAIN ONCREATE ***
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
          //getSupportActionBar().setTitle("Raktári Készletellenörző");

        setContentView(R.layout.activity_main);
          TextView toptextView = (TextView) findViewById(R.id.toptextView);

        //tablerows
        /*
        tableRow02 = (TextView) findViewById(R.id.table02);
        tableRow12 = (TextView) findViewById(R.id.table12);
        tableRow22 = (TextView) findViewById(R.id.table22);
        tableRow32 = (TextView) findViewById(R.id.table32);
        tableRow42 = (TextView) findViewById(R.id.table42);
        tableRow52 = (TextView) findViewById(R.id.table52);
        tableRow61 = (TextView) findViewById(R.id.table61);
        tableRow62 = (TextView) findViewById(R.id.table62);
*/

        //todo: nem hiszem, hogy átjön a vevőnév! ???
        Intent intent = getIntent();
        if(intent.hasExtra("intentvevonev")) {
            String vevonev = getIntent().getExtras().getString("intentvevonev");
            globalAroszt = getIntent().getExtras().getString("intentaroszt");
            globalvevoKod = getIntent().getExtras().getString("intentvevokod");
            toptextView.setText("MEGRENDELT TERMÉKEK LISTÁJA");
        }

        //*** MAIN ONCLICK LISTENERS ***
        findViewById(R.id.read_barcode).setOnClickListener(this);
        findViewById(R.id.enter_barcode).setOnClickListener(this);

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */


    //*** MAIN ON CLICK ***
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.read_barcode) {
            //BrcodeCaptureActivity INDÍTÁSA
            Intent intent = new Intent(this, BarcodeCaptureActivity.class);
            intent.putExtra(BarcodeCaptureActivity.AutoFocus, true);
            intent.putExtra(BarcodeCaptureActivity.UseFlash, false);
            startActivityForResult(intent, RC_BARCODE_CAPTURE);
        }
        //MANUAL INPUT
        if (v.getId() == R.id.enter_barcode) {
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
                    //átrakom a getData-ba, ha nem jó akkor a showJSON-ba...
                    // fillTables();
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

    //*** ON ACTIVITY RESULT: RC_BARCODE_CAPTURE ***
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_BARCODE_CAPTURE) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    final MediaPlayer mp = MediaPlayer.create(this, R.raw.sound3);
                    mp.start();

                    barcode3 = data.getStringExtra("barcode3");
                    //tableRow02.setText(barcode3);
                    getData();
                    Log.d(TAG, "Vonalkód (MainActivity) " + barcode3);
                } else {
                    //tableRow22.setText(R.string.barcode_failure);
                    Log.d(TAG, "No barcode captured, intent data is null");
                }
            } else {
                //tableRow22.setText(String.format(getString(R.string.barcode_error),
                        CommonStatusCodes.getStatusCodeString(resultCode);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    //*** MAIN GETDATA ***
    private void getData() {
        //tableRow02.setText("");
//san suriel a bruttó számítást a "netto" miatt kikapcsolom egy kicsit...
        /*
        Double brutto = 0.00;
        brutto = netto * (afa + 100) / 100;
        bruttoRound = String.format("%.2f", brutto);
        nettoRound = String.format("%.2f", netto);
        */

//TODO: LEHET, HOGY VISSZA KELLENE RAKNI A "VLEURO" WIFI ELLENŐRZÉST, CSAK NE TABLEROW-BA ADJA A HIBÁT!!!
        //String ssid = "";
        //WIFI Bekapcsolása, csatlakozás a VLEURO wifihez
/*
        WifiInfo wifiInfo;
        wifiInfo = wifiManager.getConnectionInfo();
        if (wifiInfo.getSupplicantState() == SupplicantState.COMPLETED) {
            ssid = wifiInfo.getSSID();
        } else {
            ssid = "NON";
        }

           if (!ssid.equals("\"VLEURO\"")) {

                if (devMode) {
                } else {

                    tableRow22.setText("NEM CSATLAKOZIK A \"VLEURO\" WIFI HÁLÓZATHOZ!");
                    tableRow22.setBackgroundColor(Color.RED);
                    tableRow12.setText("");
                    tableRow32.setText("");
                    tableRow42.setText("");
                    tableRow52.setText("");
                    return;
                }
        }*/

        String id = barcode3;
        // Toast.makeText(MainActivity.this, barcode3, Toast.LENGTH_LONG).show();
        if (manualInput.equals("YES")) {
            id = m_Text;
            barcode3 = m_Text;
            manualInput = "NO";
        }
//san suriel
        if(adminokList.contains(globalvevoKod)){
            url = DATA_RAKTAR_KESZLET_URL_ONLINE + id + "&vkod=" + globalvevoKod;
        } else {
            url = DATA_RAKTAR_KESZLET_URL + id + "&vkod=" + globalvevoKod;
        }

//            url = Config.DATA_RAKTAR_KESZLET_URL + id + "&vkod="+globalvevoKod;

        //Toast toast= Toast.makeText(getApplicationContext(), url , Toast.LENGTH_LONG);
        //toast.setGravity(Gravity.BOTTOM,0,20); toast.show();

        StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //        loading.dismiss();
                showJSON(response);
//san suriel
//a table k szanálva, helyette az arrayList-et kell szépen megformázni, feltölteni...
                //fillTables();
                shopAmmount();
                //fillArrays();
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, "Hibás lekérdezés!", Toast.LENGTH_LONG).show();
                        //Toast.makeText(MainActivity.this, error.getMessage().toString(), Toast.LENGTH_LONG).show();
                    }
                });
        RequestQueue requestQueue = Volley.newRequestQueue(this);

// san suriel
// StringRequest Timeout, empty cache
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                3000,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            stringRequest.setShouldCache(false);
        requestQueue.add(stringRequest);
        //VONALKOD
        //tableRow02.setText(id);
    }

    //*** MAIN SHOW JSON ***
    private void showJSON(String response) {
        //Toast.makeText(MainActivity.this, "showJSON()", Toast.LENGTH_SHORT).show();

//san suriel
        /*
        String marka = "";
        String termek = "";
        String ar = "";
        String menny = "";
        String vevonev= "";
        Double netto = 0.00;
        Double afa = 0.00;
        Boolean akcios = false;
        */

        //Válasz adatok tárolása
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray result = jsonObject.getJSONArray(Config.JSON_ARRAY);
            JSONObject termekData = result.getJSONObject(0);
                    marka = termekData.getString(Config.KEY_MARKA);
            termek = termekData.getString(Config.KEY_TERMEK);
            menny = termekData.getString(Config.KEY_KESZLET);
            nettoString = termekData.getString(Config.KEY_NETTO);
            //realnettoString = String.valueOf(nettoString);
            afaString = termekData.getString(Config.KEY_AFA);

            akcios = termekData.getBoolean(Config.KEY_AKCIO);
            vevonev = termekData.getString(Config.KEY_VEVONEV);

//            Toast toast = Toast.makeText(getApplicationContext(),"marka:" + marka + " termek:" + termek + " menny:" + menny + " netto:" + netto + " afa:" + afa, Toast.LENGTH_LONG);
            //toast.setGravity(Gravity.CENTER, 0, 0);
            //toast.show();

        } catch (JSONException e) {
            e.printStackTrace();
        }

//san suriel
//a fő fügvényből nem akar lefutni a fillTables(), de miert?

//átmehetne alfügvénybe valahogy... (fillArrays)
        //finals for intent extras
        final String ar1 = ar;
        final String barcode1 = barcode3;
        final String marka1 = marka;
        final String termek1 = termek;


    }
//-----------------------------------------------------------------

//san suriel - új fügvény a bevásárlólista tömbjeinek feltöltésére...
private void fillArrays() {
    Toast toast = Toast.makeText(getApplicationContext(),"fillArrays()" + m_Text2, Toast.LENGTH_LONG);
    toast.setGravity(Gravity.CENTER, 0, 0);
    toast.show();
    if (barcodeList.contains(barcode3)) {
        toast = Toast.makeText(getApplicationContext(),"MÁR SZEREPEL A LISTÁN!", Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
        //error already scanned vibrate
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        long[] pattern = {0, 200, 100, 200};
        assert v != null;
        v.vibrate(pattern, -1);
    } else if (marka == "null") {
        toast = Toast.makeText(getApplicationContext(),"ISMERETLEN VONALKÓD!", Toast.LENGTH_LONG);
        toast.setGravity(Gravity.BOTTOM, 0, 300);
        toast.show();
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        long[] pattern = {0, 50, 50, 50, 50, 50};
        assert v != null;
        v.vibrate(pattern, -1);
    } else {
        toast = Toast.makeText(getApplicationContext(),"marka:" + marka + " termek:" + termek + " menny:" + menny + " netto:" + netto + " afa:" + afa, Toast.LENGTH_LONG);


        barcodeList.add(barcode3);
        mennyisegList.add(menny);
        termekList.add(termek);
        markaList.add(marka);
        shopammountList.add(m_Text2);
//san suriel
        nettoStringList.add(nettoString);
        afaStringList.add(afaString);
        vevonevList.add(vevonev);
        akciosList.add(akcios);

        mergedList.add(marka + "\n" + termek + "\n" + ar + "db" + "\n" + m_Text2 + "db");
    }

    if (akcios) {
        akciosstring = "igen";
    } else {
        akciosstring = "nem";
    }

    barcodeArray = barcodeList.toArray(new String[barcodeList.size()]);
    mennyisegArray = mennyisegList.toArray(new String[mennyisegList.size()]);
    termekArray = termekList.toArray(new String[termekList.size()]);
    markaArray = markaList.toArray(new String[markaList.size()]);
    shopammountArray = shopammountList.toArray(new String[shopammountList.size()]);
    nettoStringArray = nettoStringList.toArray(new String[nettoStringList.size()]);
    afaStringArray = afaStringList.toArray(new String[afaStringList.size()]);
    vevonevArray = vevonevList.toArray(new String[vevonevList.size()]);
    //akciosArray = akciosList.toArray(new Boolean[akciosList.size()]);

    mergedArray = mergedList.toArray(new String[mergedList.size()]);



    ListView itemsListView = (ListView) findViewById(itemListView);
    ArrayAdapter<String> listViewAdapter =
            //new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mergedArray);
            new ArrayAdapter<String>(this, android.R.layout.simple_selectable_list_item, mergedArray);
    itemsListView.setAdapter(listViewAdapter);

    itemsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            String item = ((TextView)view).getText().toString();
            Intent intent = new Intent(view.getContext(),DetailsActivity.class);

            intent.putExtra("positionExtra",position);
            intent.putExtra("itemExtra",item);
            intent.putExtra("barcodeExtra",barcodeArray);
            intent.putExtra("dbExtra",mennyisegArray);
            intent.putExtra("markaExtra",markaArray);
            intent.putExtra("termekExtra",termekArray);
            intent.putExtra("shopammountExtra",shopammountArray);

            intent.putExtra("nettoStringExtra",nettoStringArray);
            intent.putExtra("afaStringExtra",afaStringArray);
            intent.putExtra("vevonevExtra",vevonevArray);
            intent.putExtra("akciosExtra",akciosArray);

            //Toast toast = Toast.makeText(getApplicationContext(),"1:"+marka1+","+termek1+","+ar1+", Toast.LENGTH_LONG);
            //toast.setGravity(Gravity.CENTER, 0, 0);
            //toast.show();

            startActivity(intent);
        }
    });


} //fillArrays!


//san suriel - új fügvény, bekéri a vásárolt mennyiséget...
private void shopAmmount() {
    AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
    builder2.setTitle("Írja be a vásárolt mennyiséget:");
    // Set up the input
    final EditText input2 = new EditText(this);
    // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
    input2.setInputType(InputType.TYPE_CLASS_NUMBER);
    builder2.setView(input2);
    // Set up the buttons
    builder2.setPositiveButton("Rendben", new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            m_Text2 = input2.getText().toString();
            fillArrays();
            Toast toast= Toast.makeText(getApplicationContext(),"ok pressed" + m_Text2, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER,0,0); toast.show();

        }
    });
    builder2.setNegativeButton("Mégsem rendelek", new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            dialog.cancel();
        }
    });
    builder2.show();
    //Toast.makeText(MainActivity.this, m_Text2, Toast.LENGTH_SHORT).show();
}

//san suriel - új fügvény, a showJSON-ból kibontva, úgy néz ki getData-ból meghívva működik...
private void fillTables() {
//
// Toast.makeText(MainActivity.this, "fillTables()", Toast.LENGTH_SHORT).show();
    //   String akciosstring;

/*

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
        bruttoRound = String.format("%.2f", brutto);
        nettoRound = String.format("%.2f", netto);
        tableRow12.setText(marka);
        tableRow22.setText(termek);
        tableRow32.setText(nettoRound + " Ft");
        tableRow42.setText(bruttoRound + " Ft");
        tableRow52.setText(menny + " db");
        if (akcios) {
            tableRow62.setBackgroundColor(Color.RED);
            tableRow61.setBackgroundColor(Color.RED);
            tableRow62.setText("IGEN");
        } else {
            tableRow62.setText("NEM");
            tableRow62.setBackgroundColor(0xFF0C4593);
            tableRow61.setBackgroundColor(0xFF0C4593);
        }


    }*/
}






    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */


    @Override //onBackPressed
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

    @Override //index
    public void onStart() {
        super.onStart();
    }

    @Override //index
    public void onStop() {
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
