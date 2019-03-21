package uk.ac.man.cs.eventlite.entities;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.annotation.Transient;

import uk.ac.man.cs.eventlite.dao.GeocodeImpl;

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
	
	@PostLoad
	public void postUpdate(){
		this.address = roadName + ", " + postCode ;
		
		final Venue thisVenue = this;
		GeocodeImpl.setVenueCoordinates(thisVenue);
		try {
			Thread.sleep(1000L);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@NotNull
	@Min(0)
	private int capacity;
	
	@Value("${some.key: 0.0}")
	private double longitude;
	
	@Value("${some.key: 0.0}")
	private double latitude;
	
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
}
