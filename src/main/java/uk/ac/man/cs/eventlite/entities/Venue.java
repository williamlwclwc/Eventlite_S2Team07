package uk.ac.man.cs.eventlite.entities;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.data.annotation.Transient;

@Entity
@Table(name = "venues")
public class Venue {

	@Id
	@GeneratedValue
	private long id;

	private String name;
	
	@NotNull
	@Size(min=1, max=256)
	private String roadName;
	@NotNull
	@Size(min=6, max=8)
	private String postCode;

	@NotNull
	@Transient
	@Size(min=7, max=256)
	private String address = roadName + "\n" + postCode;
	
	@PostLoad
	public void postLoad(){
		this.address = roadName + "\n, " + postCode ;
	}
	
	

	@NotNull
	@Min(0)
	private int capacity;

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
	
}
