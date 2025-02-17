package uk.ac.man.cs.eventlite.dao;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uk.ac.man.cs.eventlite.entities.Venue;

@Service
public class VenueServiceImpl implements VenueService {

	private final static Logger log = LoggerFactory.getLogger(VenueServiceImpl.class);

	private final static String DATA = "data/venues.json";

	@Autowired
	private VenueRepository venueRepository;
	
	
	@Override
	public long count() {
		return venueRepository.count();
	}

	@Override
	public Iterable<Venue> findAll() {
		return venueRepository.findAllByOrderByNameAsc();
	}

	@Override
	public Venue save(Venue v){
		return venueRepository.save(v);
	}

	@Override
	public Venue findById(long id) {
		return venueRepository.findOne(id);
	}
	
	@Override
	public List<Venue> findAllByOwnerName(String organiser) {
		return venueRepository.findAllByOwnerName(organiser);
	}

	@Override
	public void delete(long id) {
		venueRepository.delete(id);
		
	}

	@Override
	public void deleteAll() {
		venueRepository.deleteAll();
		
	}
}
