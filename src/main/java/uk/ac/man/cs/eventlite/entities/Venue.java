package uk.ac.man.cs.eventlite.entities;

import javax.persistence.*;

@Entity
@Table(name = "venues")
public class Venue {

	@Id
	@GeneratedValue
	private long id;

	private String name;
	
	private String address;

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
	
	
}
