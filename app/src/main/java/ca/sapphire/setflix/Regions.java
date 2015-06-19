package ca.sapphire.setflix;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by apreston on 6/2/2015.
 */
public class Regions {
    public static final Map<String, String> REGION = createMap();

    private static Map<String, String> createMap() {
        Map<String, String> regions = new LinkedHashMap<String, String>();
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

        return Collections.unmodifiableMap(regions);
    }

    public CharSequence[] getNames()
    {
        CharSequence vals[] = new CharSequence[ REGION.size() ];
        return( REGION.values().toArray( vals ) );
    }

    public CharSequence[] getCodes() {
        CharSequence keys[] = new CharSequence[ REGION.size() ];
        return( REGION.values().toArray( keys ) );
    }
}
