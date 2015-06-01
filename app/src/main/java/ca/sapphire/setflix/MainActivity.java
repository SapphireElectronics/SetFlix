package ca.sapphire.setflix;

import android.app.Activity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.Iterator;
import java.util.Map;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Set;


public class MainActivity extends ActionBarActivity {
    Map regions = new LinkedHashMap();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        makeDictionary();

        Spinner spinner = (Spinner) findViewById(R.id.regions_spinner);

        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(this,android.R.layout.simple_spinner_dropdown_item);

        Set regionKeys = regions.keySet();

        for (Iterator region = regionKeys.iterator(); region.hasNext();) {
            CharSequence key = (CharSequence) region.next();
            adapter.add(key);
        }


// Create an ArrayAdapter using the string array and a default spinner layout
//        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
//                R.array.regions_array, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        spinner.setAdapter(adapter);
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

    public void makeDictionary() {
        regions.put( "Unknown","");
        regions.put( "Argentina","ar" );
        regions.put( "Australia","au");
        regions.put( "Austria","at");
        regions.put( "Argentina","ar");
        regions.put( "Belgium","be");
        regions.put( "Brazil","br");
        regions.put( "Canada","ca");
        regions.put( "Columbia","co");
        regions.put( "Germany","de");
        regions.put( "Denmark","dk");
        regions.put( "Finland","fi");
        regions.put( "France","fr");
        regions.put( "Ireland","ie");
        regions.put( "Luxemborg","lu");
        regions.put( "Mexico","mx");
        regions.put( "Netherlands","nl");
        regions.put( "New Zealand","nz");
        regions.put( "Norway","no");
        regions.put( "Sweden","se");
        regions.put( "Switzerland","ch");
        regions.put( "UK","gb");
        regions.put( "USA", "us" );
    }

    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)
        result.  String rc = parent.getItemAtPosition(pos).toString();

        result.
    }


}


