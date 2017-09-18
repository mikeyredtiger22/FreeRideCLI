
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


    //Each task direction takes about 0.1 seconds to load
    public static DirectionsResult getTaskDirections(double startLat, Double startLon, Double endLat, Double endLon){
        DirectionsResult result = null;
        try {
            result = DirectionsApi.newRequest(GEO_API_CONTEXT)
                    .mode(TravelMode.DRIVING)
                    .alternatives(false)
                    .origin(new LatLng(startLat, startLon))
                    .destination(new LatLng(endLat, endLon))
                    .await();
        } catch (ApiException | InterruptedException | IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String getLocationAddress(double startLat, Double startLon) {
        LatLng location = new LatLng(startLat, startLon);
        GeocodingResult[] results = null;
        try {
            results = GeocodingApi.newRequest(GEO_API_CONTEXT)
                    .latlng(location)
                    .language("en-GB")
                    .await();
        } catch (ApiException | InterruptedException | IOException e) {
            e.printStackTrace();
        }

        if (results.length > 0) {
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
