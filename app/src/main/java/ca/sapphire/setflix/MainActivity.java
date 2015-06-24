package ca.sapphire.setflix;

//TODO  show favourites selection when in settings
//TODO  updated Favourites button text when selecting a new favourite in settings
//TODO  move HTTP blocking calls into a separate thread

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Set;

public class MainActivity extends ActionBarActivity {
    private Button region_button;
    private Button fave_button;
    private Button last_button;

    // debug mode flag
    private final boolean DEBUG_MODE = false;
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

        fave_button.setText( str );

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
                View promptsView = li.inflate(R.layout.region_dialog_layout, null);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder( context );

                alertDialogBuilder.setView(promptsView);

                // set dialog message

                alertDialogBuilder.setTitle("Select a Region");
                alertDialogBuilder.setIcon(R.drawable.ic_settings_white_24dp);
                // create alert dialog
                final AlertDialog alertDialog = alertDialogBuilder.create();

                final Spinner mSpinner= (Spinner) promptsView.findViewById(R.id.spinner);

                ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(context, android.R.layout.simple_spinner_dropdown_item);
                Set regionKeys = Regions.REGION.keySet();
                for (Iterator region = regionKeys.iterator(); region.hasNext(); ) {
                    CharSequence key = (CharSequence) region.next();
                    adapter.add(Regions.REGION.get(key));
                }

                mSpinner.setAdapter(adapter);
                // reference UI elements from my_dialog_layout in similar fashion

                mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                        if( pos>0 ) {
                            String region = parent.getItemAtPosition(pos).toString();
                            setRegion(region);

                            Log.i(TAG, "Clicked : " + region);

                            alertDialog.dismiss();

                            SharedPreferences.Editor editor = sharedPrefs.edit();
                            editor.putString( "last", region );
                            editor.commit();

                            last_button.setText( region );

                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView parent) {
                        // Do nothing.
                    }
                });

                // show it
                alertDialog.show();
                alertDialog.setCanceledOnTouchOutside(false);

//                String region = String.valueOf(regions_spinner.getSelectedItem());
//                setRegion(region);
                //               String region_code = Regions.REGION.get( region );

                //               Toast.makeText(MainActivity.this,
                //                       "Attempting to set region to: " + region,
                //                               Toast.LENGTH_SHORT).show();
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
//        String api_key = "91b2c3964245d7b403f54fc8b4bdde9eb25b70df0a4ddd0b5865a27af119d830";

        String url_str = "http://unlo.it/";
        String channel = "channel=netflix";
        String country = "country=" + region_code;

        String full_url = url_str + api_key + "?" + country + "&" + channel;
        Log.i(TAG, "URL: " + full_url);

        if( DEBUG_MODE ) {
            Toast.makeText(MainActivity.this, "DEBUG MODE, region not set.", Toast.LENGTH_LONG).show();
            return;
        }

        try {
            URL url = new URL(full_url);
            HttpURLConnection request = (HttpURLConnection) (url.openConnection());

            try {
                int status = request.getResponseCode();

                if( (status == HttpURLConnection.HTTP_MOVED_PERM) || (status == HttpURLConnection.HTTP_MOVED_TEMP) ) {

                    // get redirect url from "location" header field
                    String newUrl = request.getHeaderField("Location");

                    // open the new connnection again
                    request = (HttpURLConnection) new URL(newUrl).openConnection();
                    status = request.getResponseCode();

                    Log.i( TAG, "Redirect: " + newUrl );
                }

                if( status != 200 ) {
                    Log.i(TAG, "Status :" + status + "  " + request.getResponseMessage());
                    Toast.makeText(MainActivity.this, "Status :" + status + "  " + request.getResponseMessage(), Toast.LENGTH_LONG).show();

                    return;
                }

                InputStream in = new BufferedInputStream(request.getInputStream());
                String ret_data = readStream(in);

                Log.i( TAG, ret_data);
                Toast.makeText(MainActivity.this, ret_data, Toast.LENGTH_LONG).show();

            } finally {
                request.disconnect();
            }

        } catch (MalformedURLException e) {
            Toast.makeText(MainActivity.this, "Bad URL\r\n"+ e.toString(), Toast.LENGTH_LONG).show();
            return;
        } catch (IOException e) {

            Toast.makeText(MainActivity.this, "IO Exception\r\n"+ e.toString(), Toast.LENGTH_LONG).show();
            Log.e("SetFlix", "exception", e);
            return;

        }
    }

    private String readStream(InputStream is) {
        try {
            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            int i = is.read();
            while (i != -1) {
                bo.write(i);
                i = is.read();
            }
            return bo.toString();
        } catch (IOException e) {
            return "";
        }
    }

/*
    private class NetworkTask extends AsyncTask<String, Void, HttpResponse> {
        @Override
        protected HttpResponse doInBackground(String... params) {
            String full_url = params[0];

            try {
                URL url = new URL(full_url);
                HttpURLConnection request = (HttpURLConnection) (url.openConnection());

                Toast.makeText(MainActivity.this, "Returned", Toast.LENGTH_LONG).show();


                try {
                    InputStream in = new BufferedInputStream(request.getInputStream());
                    readStream(in);

                    Toast.makeText(MainActivity.this, in.toString(), Toast.LENGTH_LONG).show();
                } finally {
                    request.disconnect();
                }

            } catch (MalformedURLException e) {
                Toast.makeText(MainActivity.this, "Bad URL", Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                Toast.makeText(MainActivity.this, "IO Exception", Toast.LENGTH_LONG).show();
            }
            return null;
        }


        @Override
        protected void onPostExecute(HttpResponse result) {
            //Do something with result
            if (result != null)
                Toast.makeText(MainActivity.this, result.getEntity().toString(), Toast.LENGTH_LONG).show();
        }
    }
*/
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}



