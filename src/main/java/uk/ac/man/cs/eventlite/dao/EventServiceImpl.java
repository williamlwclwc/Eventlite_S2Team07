package uk.ac.man.cs.eventlite.dao;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uk.ac.man.cs.eventlite.entities.Event;

@Service
public class EventServiceImpl implements EventService {

	@Autowired
	private EventRepository eventRepository;
	
	private final static Logger log = LoggerFactory.getLogger(EventServiceImpl.class);

	private final static String DATA = "data/events.json";
	
	private Date currentDate = new Date(System.currentTimeMillis());

	public long count() {
		return eventRepository.count();
	}

	public Iterable<Event> findAllFutureEvents() {
		return eventRepository.findByDateAfterOrderByDateAscNameAsc(currentDate);
	}
	
	public Iterable<Event> findAllPastEvents() {
		return eventRepository.findByDateBeforeOrderByDateDescNameDesc(currentDate);
	}
	
	public Event save(Event e) {
		return eventRepository.save(e);
	}
	
	public Event findById(long id) {
		return eventRepository.findOne(id);
	}
		
	public void delete(long id) {
		eventRepository.delete(id);
	}

	public void deleteAll() {
		eventRepository.deleteAll();
	}
}
