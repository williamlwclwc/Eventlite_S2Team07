package uk.ac.man.cs.eventlite.dao;

import java.util.List;

import com.mapbox.api.geocoding.v5.MapboxGeocoding;
import com.mapbox.api.geocoding.v5.models.CarmenFeature;
import com.mapbox.api.geocoding.v5.models.GeocodingResponse;
import com.mapbox.geojson.Point;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.ac.man.cs.eventlite.entities.Venue;

public class GeocodeImpl {
	
	public static void setVenueCoordinates(Venue venue)
	{
		MapboxGeocoding mapboxGeocoding = MapboxGeocoding.builder()
				.accessToken("pk.eyJ1IjoiZXZlbnRsaXRlaDA3IiwiYSI6ImNqdGN2anNwMDA3MGM0NG84bm9wNzRyNDAifQ.5iHqgi5_wYd_gb8eLLgHfQ")
				.country("GB")
				.query(venue.getPostCode())
				.build();
		
		mapboxGeocoding.enqueueCall(new Callback<GeocodingResponse>() {
			@Override
			public void onResponse(Call<GeocodingResponse> call, Response<GeocodingResponse> response) {
				try {
					Thread.sleep(1000L);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				List<CarmenFeature> results = response.body().features();
		 
				if (results.size() > 0) {
		 
				  // Log the first results Point.
				  Point firstResultPoint = results.get(0).center();
				  
				  venue.setLatitude(firstResultPoint.latitude());
				  venue.setLongitude(firstResultPoint.longitude());
		 
				} else {
		 
				  // No result for your request were found.
				  System.out.println("No result :(");
		 
				}
			}
		 
			@Override
			public void onFailure(Call<GeocodingResponse> call, Throwable throwable) {
				throwable.printStackTrace();
			}
		});
	}
}
