package com.google.android.gms.oem.raktar.vlscan;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import static com.google.android.gms.oem.raktar.vlscan.R.id.termekValue;

public class DetailsActivity extends AppCompatActivity {

    private TextView markaValueTextView;
    private TextView termekValueTextView;
    private TextView mennyisegValueTextView;
    private TextView vonalkodValueTextView;
    private String m_Text,m_Text2 = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        setTitle("Termék információk");
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent  = getIntent();
        Bundle extras = intent.getExtras();
/*
intent.putExtra("boltnevExtra",boltnev);
                intent.putExtra("boltnev2Extra",boltnev2);
                intent.putExtra("barcodeExtra",barcodeArray);
                intent.putExtra("dbExtra",mennyisegArray);
                intent.putExtra("markaExtra",markaArray);
                intent.putExtra("termekExtra",termekArray);




 */
        String item = extras.getString("itemExtra");
        int position = extras.getInt("positionExtra");
        String[] barcodeArray = extras.getStringArray("barcodeExtra");
        String[] mennyisegArray = extras.getStringArray("dbExtra");
        String[] markaArray = extras.getStringArray("markaExtra");
        String[] termekArray = extras.getStringArray("termekExtra");

        markaValueTextView = (TextView)findViewById(R.id.markaValue);
        markaValueTextView.setText(markaArray[position]);

        termekValueTextView = (TextView)findViewById(R.id.termekValue);
        termekValueTextView.setText(termekArray[position]);

        mennyisegValueTextView = (TextView)findViewById(R.id.mennyisegValue);
        mennyisegValueTextView.setText(mennyisegArray[position]);

        vonalkodValueTextView = (TextView)findViewById(R.id.vonalkodValue);
        vonalkodValueTextView.setText(barcodeArray[position]);


        //View changeButton = findViewById(R.id.enter_mennyiseg);
        //changeButton.setVisibility(View.GONE);

        }

    public void enter_mennyisegClick(View view) {
        AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
        builder2.setTitle("Rendelés:");
        // Set up the input
        final EditText input2 = new EditText(this);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input2.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder2.setView(input2);
        // Set up the buttons
        builder2.setPositiveButton("Módosítás", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                m_Text2 = input2.getText().toString();
                Toast toast= Toast.makeText(getApplicationContext(),m_Text2, Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER,0,0); toast.show();

            }
        });
        builder2.setNegativeButton("Vissza", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder2.show();
    }

    //ActionBar Back ne nullázza az activity-t!!
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                break;
        }
        return true;
    }
}
