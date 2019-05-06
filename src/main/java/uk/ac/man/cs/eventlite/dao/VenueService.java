package uk.ac.man.cs.eventlite.dao;

import java.util.List;

import uk.ac.man.cs.eventlite.entities.Venue;

public interface VenueService {

	public long count();

	public Iterable<Venue> findAll();
	
	public Venue save(Venue v);	
	
	public Venue findById(long id);
	
	public List<Venue> findAllByOwnerName(String owner);
	
	public void delete(long id);

	public void deleteAll();
}
