package ca.sapphire.setflix;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.Map;

//public class MainActivity extends ActionBarActivity {
public class MainActivity extends AppCompatActivity {
    NetSetRegion netSetRegion;

    private Button region_button;
    private Button fave_button;
    private Button last_button;

    // debug mode flag
    private final static boolean DEBUG_MODE = false;
    private final String TAG = "SetFlix";

    public SharedPreferences sharedPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addListenerOnRegionButton();
        addListenerOnFaveButton();
        addListenerOnLastButton();

        fave_button = (Button) findViewById(R.id.favourite_button);

        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        String str = sharedPrefs.getString( "favourite", "Canada" );

        fave_button.setText(str);

        region_button = (Button) findViewById(R.id.region_button);

        last_button = (Button) findViewById(R.id.last_button);
        str = sharedPrefs.getString( "last", "Canada" );

        last_button.setText( str );

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

//        Locale.getISOCountries()


    }

    @Override
    protected void onResume() {
        super.onResume();
        fave_button.setText(sharedPrefs.getString("favourite", ""));
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
            Intent intent = new Intent(this, SettingsActivity.class);
//                Intent intent = new Intent(this, DisplayMessageActivity.class);
//                EditText editText = (EditText) findViewById(R.id.edit_message);
//                String message = editText.getText().toString();
//                intent.putExtra(EXTRA_MESSAGE, message);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void addListenerOnRegionButton() {

        region_button = (Button) findViewById(R.id.region_button);
        region_button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Context context = v.getContext();

                LayoutInflater li = LayoutInflater.from(context);
//                View promptsView = li.inflate(R.layout.region_dialog_layout, null);
                View promptsView = li.inflate(R.layout.region_dialog_layout, new RelativeLayout(context), false);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder( context );

                alertDialogBuilder.setView(promptsView);

                // set dialog message

                alertDialogBuilder.setTitle("Select a Region");
                alertDialogBuilder.setIcon(R.drawable.ic_settings_white_24dp);
                // create alert dialog
                final AlertDialog alertDialog = alertDialogBuilder.create();
                final Spinner mSpinner= (Spinner) promptsView.findViewById(R.id.spinner);

                ArrayAdapter<CharSequence> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item);

                for( Map.Entry<String,String> entry : Regions.REGION.entrySet() ) {
                    adapter.add( entry.getValue() );
                }

                mSpinner.setAdapter(adapter);
                // reference UI elements from my_dialog_layout in similar fashion

                mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                        if (pos > 0) {
                            String region = parent.getItemAtPosition(pos).toString();
                            setRegion(region);

                            Log.i(TAG, "Clicked : " + region);

                            alertDialog.dismiss();

                            SharedPreferences.Editor editor = sharedPrefs.edit();

                            // is the newly picked region is not the same as the Favourite button, then update the Last button
                            String str = sharedPrefs.getString("favourite", "Canada");
                            if( str == null )
                                str = "";

                            if (!str.equals(region)) {
                                editor.putString("last", region);
                                editor.apply();

                                last_button.setText(region);
                            }
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView parent) {
                        // Do nothing.
                    }
                });

                alertDialog.setCanceledOnTouchOutside(true);
                alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {

                    @Override
                    public void onShow(DialogInterface dialogInterface) {
                        mSpinner.performClick();
                    }
                });

                // show it
                alertDialog.show();
            }
        });
    }

    public void addListenerOnFaveButton() {
        fave_button = (Button) findViewById(R.id.favourite_button);

        fave_button.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String region = String.valueOf( fave_button.getText() );
                setRegion(region);
            }
        });
    }

    public void addListenerOnLastButton() {
        last_button = (Button) findViewById(R.id.last_button);

        last_button.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String region = String.valueOf( last_button.getText() );
                setRegion(region);
            }
        });
    }


    public void setRegion(String region) {
//        String region_code = Regions.REGION.getKeyByValue(region);
        String region_code = "";

        for( Map.Entry entry: Regions.REGION.entrySet() ) {
            if(region.equals(entry.getValue())) {
                region_code =  (String) entry.getKey();
            }
        }

        String api_key = sharedPrefs.getString("api_key", "");

        if( api_key == null )
            api_key = "";

        if( api_key.equals("") ) {
            Toast.makeText(MainActivity.this, "Please enter an API Key in Settings", Toast.LENGTH_SHORT).show();
            return;
        }

        if (region_code.equals("") ) {
            Toast.makeText(MainActivity.this, "Please select a region to watch.", Toast.LENGTH_SHORT).show();
            return;
        }

        if ( ! isNetworkAvailable() ) {
            Toast.makeText(MainActivity.this, "I don't have a network connection!", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(MainActivity.this, "Attempting to set region to: " + region, Toast.LENGTH_SHORT).show();

// Sample string:   http://unlo.it/79f7a5b712bac07?country=us&channel=netflix

//        String api_key = "06c6942760af088";

        String url_str = "http://unlo.it/";
        String channel = "channel=netflix";
        String country = "country=" + region_code;

        String full_url = url_str + api_key + "?" + country + "&" + channel;
        Log.i(TAG, "URL: " + full_url);

        if( DEBUG_MODE ) {
            Toast.makeText(MainActivity.this, "DEBUG MODE, region not set.", Toast.LENGTH_LONG).show();
            return;
        }

        netSetRegion = new NetSetRegion( this, region, (ProgressBar) findViewById(R.id.progressBar) );
        netSetRegion.execute( full_url ).getStatus();

    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
