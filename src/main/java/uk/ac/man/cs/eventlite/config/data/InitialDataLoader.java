package uk.ac.man.cs.eventlite.config.data;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import uk.ac.man.cs.eventlite.dao.EventService;
import uk.ac.man.cs.eventlite.dao.VenueService;
import uk.ac.man.cs.eventlite.entities.Event;

@Component
@Profile({ "default", "test" })
public class InitialDataLoader implements ApplicationListener<ContextRefreshedEvent> {

	private final static Logger log = LoggerFactory.getLogger(InitialDataLoader.class);

	@Autowired
	private EventService eventService;

	@Autowired
	private VenueService venueService;

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {

		Event example = new Event();
		example.setId(1);
		example.setName("Apocalypse");
		example.setDate(new Date(System.currentTimeMillis()));
		example.setTime(new Date(System.currentTimeMillis()));
		example.setVenue(1);
		
		eventService.save(example);	
		if (eventService.count() > 0) {
			log.info("Database already populated. Skipping data initialization.");
			return;
		}

	}
}
