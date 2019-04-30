package uk.ac.man.cs.eventlite.dao;

import java.util.List;

import uk.ac.man.cs.eventlite.entities.Event;

public interface EventService {

	public long count();

	public Iterable<Event> findAllFutureEvents();
	
	public Iterable<Event> findAllPastEvents();
	
	public List<Event> findAllByOrganiserName(String organiser);
	
	public Iterable<Event> findAll();
	
	public Event save(Event e);

	public Event findById(long id);

	public void delete(long id);

	public void deleteAll();
}
