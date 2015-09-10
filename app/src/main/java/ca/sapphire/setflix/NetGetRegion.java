package ca.sapphire.setflix;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Xml;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Admin on 09/09/15.
 */
public class NetGetRegion extends AsyncTask<Void, Void, Void> {


        private final String ns = null;
        String country;

    public NetGetRegion( Context context ) {
        super();
//        this.context = context;
//        this.region = region;
//        this.progressBar = progressBar;
    }


    protected String doInBackground( String... ) {

            try {
                URL url = new URL( "http://api-global.netflix.com/apps/applefuji/config" );
                HttpURLConnection conn = (HttpURLConnection) (url.openConnection());

                XmlPullParser parser = Xml.newPullParser();
                try {
                    parser.setInput( conn.getInputStream(), null );
//                        parser.nextTag();
                    parser.require(XmlPullParser.START_TAG, ns, "feed");
                    while (parser.next() != XmlPullParser.END_TAG) {
                        if (parser.getEventType() != XmlPullParser.START_TAG) {
                            continue;
                        }
                        if (parser.getName().equals("country")) {
                            country = parser.getText();
                        }
                    }
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                }





            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

    protected void onPreExecute() {

    }

    protected void onPostExecute( String str ) {
    }
}
