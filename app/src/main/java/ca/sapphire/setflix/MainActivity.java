package ca.sapphire.setflix;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
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
import java.util.Map;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Set;

public class MainActivity extends ActionBarActivity {
    private Spinner regions_spinner;
    private Button submit_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        regions_spinner = (Spinner) findViewById(R.id.regions_spinner);

// Create an ArrayAdapter using a default spinner layout
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_dropdown_item);

        Set regionKeys = Regions.REGION.keySet();

        for (Iterator region = regionKeys.iterator(); region.hasNext(); ) {
            CharSequence key = (CharSequence) region.next();
            adapter.add(key);
        }

// Apply the adapter to the spinner
        regions_spinner.setAdapter(adapter);

//        addListenerOnSpinnerItemSelection();
        addListenerOnSubmitButton();


        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }



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


    public void addListenerOnSpinnerItemSelection() {

        regions_spinner.setOnItemSelectedListener(new CustomOnItemSelectedListener());
    }

//   public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
//       // An item was selected. You can retrieve the selected item using
//       // parent.getItemAtPosition(pos)
//       result.  String rc = parent.getItemAtPosition(pos).toString();
//
//        result.
//    }

    public void addListenerOnSubmitButton() {
        regions_spinner = (Spinner) findViewById(R.id.regions_spinner);
        submit_button = (Button) findViewById(R.id.submit_button);

        submit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String region = String.valueOf(regions_spinner.getSelectedItem());
                setRegion(region);
                //               String region_code = Regions.REGION.get( region );

                //               Toast.makeText(MainActivity.this,
                //                       "Attempting to set region to: " + region,
                //                               Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void setRegion(String region) {

        String region_code = Regions.REGION.get(region);

        if (region_code == "") {
            Toast.makeText(MainActivity.this, "Please select a region to watch.", Toast.LENGTH_SHORT).show();
            return;
        }

        if ( ! isNetworkAvailable() ) {
            Toast.makeText(MainActivity.this, "I don't have a network connection!", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(MainActivity.this, "Attempting to set region to: " + region, Toast.LENGTH_SHORT).show();

        String url_str = "https://unlocator.com/tool/api.php?api_key=";
//        String api_key = "06c6942760af088";
        String api_key = "zzzzzzzzzzzzzzz";
        String channel = "channel=netflix";
        String country = "country=" + region_code;

        String full_url = url_str + api_key + "?" + country + "&" + channel;

        Toast.makeText(MainActivity.this, "String: " + full_url, Toast.LENGTH_LONG).show();

//        new NetworkTask().execute(full_url);

/*
*/

        try {

            URL url = new URL(full_url);
            HttpURLConnection request = (HttpURLConnection) (url.openConnection());


            Toast.makeText(MainActivity.this, "Returned", Toast.LENGTH_LONG).show();


            try {
                InputStream in = new BufferedInputStream(request.getInputStream());
                readStream(in);

                Toast.makeText(MainActivity.this, "Return data: " + in.toString(), Toast.LENGTH_LONG).show();
            } finally {
                request.disconnect();
            }

        } catch (MalformedURLException e) {
            Toast.makeText(MainActivity.this, "Bad URL\r\n"+ e.toString(), Toast.LENGTH_LONG).show();
            return;
        } catch (IOException e) {
            Toast.makeText(MainActivity.this, "IO Exception\r\n"+ e.toString(), Toast.LENGTH_LONG).show();
            return;

        }
/**/
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

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}



