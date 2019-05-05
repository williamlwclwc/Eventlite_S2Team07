package uk.ac.man.cs.eventlite.entities;

import java.util.List;
import java.util.Set;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.annotation.Transient;

import com.mapbox.api.geocoding.v5.MapboxGeocoding;
import com.mapbox.api.geocoding.v5.models.CarmenFeature;
import com.mapbox.api.geocoding.v5.models.GeocodingResponse;
import com.mapbox.geojson.Point;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@Entity
@Table(name = "venues")
public class Venue {

	@Id
	@GeneratedValue
	private long id;

    @NotBlank(message = "The name may not be blank.")
    @Size(max=255, message = "Must be less than 256 characters.")
	private String name;
	
	@NotBlank(message = "The Street name may not be blank.")
	@Size(max=299, message = "Must be less than 300 characters.")
	private String roadName;
	@NotBlank(message = "The postcode may not be blank.")
	@Size(min=6, max=8, message = "Make sure the postcode is correct.")
	private String postCode;

	@Transient
	private String address;
	
	@NotBlank
	private String owner;
	
	@PostLoad
	public void postLoad(){
		this.address = roadName + ", " + postCode ;
	}

	@NotNull
	@Min(0)
	private int capacity;
	
	public static final String MAPTOKEN = "pk.eyJ1IjoiZXZlbnRsaXRlaDA3IiwiYSI6ImNqdGN1aXU0dDB5MGQzeXBjMDh0bXBmZWEifQ.cAtpPyEFrf04RlRjdtfc1w";
	
	@Value("${some.key: 0.0}")
	private double longitude;
	
	@Value("${some.key: 0.0}")
	private double latitude;
	
	@OneToMany(mappedBy = "venue", cascade = CascadeType.ALL)
    private Set<Event> events;
	
	public Set<Event> getEvents() {
		return events;
	}

	public Venue() {
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getCapacity() {
		return capacity;
	}

	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}
	
	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public String getAddress() {
		return address;
	}
	
	public void setAddress(String address) {
		this.address = address;
	}
	
	public String getRoadName() {
		return roadName;
	}

	public void setRoadName(String roadName) {
		this.roadName= roadName;
	}
	
	public String getPostCode() {
		return postCode;
	}

	public void setPostCode(String postCode) {
		this.postCode = postCode;
	}
	
	public double getLongitude() {
		return longitude;
	}
	
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	
	public double getLatitude() {
		return latitude;
	}
	
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	
	public void setCoordinates()
	{
		MapboxGeocoding mapboxGeocoding = MapboxGeocoding.builder()
				.accessToken(MAPTOKEN)
				.country("GB")
				.query(this.postCode)
				.build();
		
		mapboxGeocoding.enqueueCall(new Callback<GeocodingResponse>() {
			@Override
			public void onResponse(Call<GeocodingResponse> call, Response<GeocodingResponse> response) {
				List<CarmenFeature> results = response.body().features();
		 
				if (results.size() > 0) {
				  // Log the first results Point.
				  Point firstResultPoint = results.get(0).center();
				  setLatitude(firstResultPoint.latitude());
				  setLongitude(firstResultPoint.longitude());
		 
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
		try {
			Thread.sleep(1000L);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
