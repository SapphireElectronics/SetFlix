package ca.sapphire.setflix;

/**
 * Created by Admin on 01/06/15.
 */
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Toast;

import java.util.Map;

public class CustomOnItemSelectedListener implements OnItemSelectedListener {

    public Map regions;

    public void setRegions( Map regions ) {
        regions = regions;
    }

    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

        String region = parent.getItemAtPosition(pos).toString();
        String region_code = regions.get( region ).toString();

        Toast.makeText(parent.getContext(),
                "On Item Select : \n" + region + "  key=" + region_code,
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub

    }

}