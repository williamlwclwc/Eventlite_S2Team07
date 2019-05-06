package uk.ac.man.cs.eventlite.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import uk.ac.man.cs.eventlite.entities.Venue;


public interface VenueRepository extends CrudRepository<Venue, Long>{
	
	public List<Venue> findAllByOrderByNameAsc();
	
	@Query("select v from Venue v where v.owner like %?1")
	public List<Venue> findAllByOwnerName(String owner);

}
