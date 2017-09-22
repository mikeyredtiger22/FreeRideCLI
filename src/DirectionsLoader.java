
import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.LatLng;
import com.google.maps.model.TravelMode;

import java.io.IOException;
import java.util.concurrent.TimeUnit;


public class DirectionsLoader {

    private static final GeoApiContext GEO_API_CONTEXT = getGeoContext();

    /*
    If you'd like to influence the route using waypoints without adding a stopover,
    prefix the waypoint with via:. Waypoints prefixed with via: will not add an entry
    to the legs array, but will instead route the journey through the provided waypoint.

    Caution: Using the via: prefix to avoid stopovers results in directions that are very strict in their
    interpretation of the waypoint. This may result in severe detours on the route or ZERO_RESULTS in the
    response status code if the Google Maps Directions API is unable to create directions through that point.
     */



    //Each task direction takes about 0.1 seconds to load
    public static DirectionsResult getTaskDirections(Double[] locationLats, Double[] locationLongs,
                                                     boolean ordered, boolean walking) {
        TravelMode travelMode = walking ? TravelMode.WALKING : TravelMode.DRIVING;
        int locationCount = locationLats.length;
        String origin = locationLats[0] + ", " + locationLongs[0];
        String destination = locationLats[locationCount-1] + ", " + locationLongs[locationCount-1];
        String[] waypoints = new String[locationCount-2];
        for (int index = 1; index < locationCount-1; index++) {
            waypoints[index-1] = "via:" + locationLats[index] + ", " + locationLongs[index];
        }
        DirectionsResult result = null;
        try {
            result = DirectionsApi.newRequest(GEO_API_CONTEXT)
                    .mode(travelMode)
                    .alternatives(false)
                    .origin(origin)
                    .waypoints(waypoints)
                    .optimizeWaypoints(!ordered)
                    .destination(destination)
                    .await();
        } catch (ApiException | InterruptedException | IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String getLocationAddress(LatLng location) {
        GeocodingResult[] results = null;
        try {
            results = GeocodingApi.newRequest(GEO_API_CONTEXT)
                    .latlng(location)
                    .language("en-GB")
                    .await();
        } catch (ApiException | InterruptedException | IOException e) {
            e.printStackTrace();
        }

        if (results != null && results.length > 0) {
            return results[0].formattedAddress;
        } else {
            return null;
        }
    }

    /**
     * Should only be called once
     * @return Api Context for app to use google directions api
     */
    private static GeoApiContext getGeoContext() {
        return new GeoApiContext.Builder()
                .queryRateLimit(25)
                .apiKey(VALUES.DIRECTIONS_API_KEY)
                .connectTimeout(3, TimeUnit.SECONDS)
                .readTimeout(3, TimeUnit.SECONDS)
                .writeTimeout(3, TimeUnit.SECONDS)
                .maxRetries(2)
                .build();
    }
}
