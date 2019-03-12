package uk.ac.man.cs.eventlite.dao;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import uk.ac.man.cs.eventlite.entities.Venue;


public interface VenueRepository extends CrudRepository<Venue, Long>{
	
	public List<Venue> findAllByOrderByNameAsc(); 

}
