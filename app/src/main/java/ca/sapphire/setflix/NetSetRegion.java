package ca.sapphire.setflix;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class NetSetRegion extends AsyncTask<String, Void, String> {
    private static final String TAG = "NetSetRegion";
    private static final String ResultOK = "Suceeded";
    private Context context;
    private String region;
    private ProgressBar progressBar;

    public NetSetRegion( Context context, String region, ProgressBar progressBar ) {
        super();
        this.context = context;
        this.region = region;
        this.progressBar = progressBar;
    }

    protected String doInBackground(String... full_url ) {
        return setRegion( full_url[0] );
    }

    protected void onPreExecute() {
        progressBar.setVisibility(ProgressBar.VISIBLE);
    }

    protected void onPostExecute( String str ) {
        progressBar.setVisibility( ProgressBar.INVISIBLE );
        Toast.makeText( context, str, Toast.LENGTH_LONG ).show();
    }

    public String setRegion(String full_url) {
        try {
            URL url = new URL(full_url);
            HttpURLConnection request = (HttpURLConnection) (url.openConnection());

            try {
                int status = request.getResponseCode();

                if( (status == HttpURLConnection.HTTP_MOVED_PERM) || (status == HttpURLConnection.HTTP_MOVED_TEMP) ) {
                    // do a single level of redirect

                    // get redirect url from "location" header field
                    String newUrl = request.getHeaderField("Location");

                    // open the new connnection again
                    request = (HttpURLConnection) new URL(newUrl).openConnection();
                    status = request.getResponseCode();

                    Log.i( TAG, "Redirect: " + newUrl );
                }

                if( status != 200 ) {
                    // did not succeed.
                    Log.i(TAG, "Status :" + status + "  " + request.getResponseMessage());
                    return "Status :" + status + "  " + request.getResponseMessage();
                }

                InputStream in = new BufferedInputStream(request.getInputStream());
                String ret_data = readStream(in);

                Log.i(TAG, ret_data);
                // Succedded
                return "Successfully set region to " + region;

            } finally {
                request.disconnect();
            }

        } catch (MalformedURLException e) {
            Log.e(TAG, "Bad URL: " + e.toString() );
            return "Bad URL: " + e.toString();
        } catch (IOException e) {
            Log.e( TAG, "IO Exception: "+ e.toString() );
            return "IO Exception: "+ e.toString();
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
}
