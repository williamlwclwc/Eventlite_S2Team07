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
import uk.ac.man.cs.eventlite.entities.Venue;

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

		if (eventService.count() > 0) {
			log.info("Database already populated. Skipping data initialization.");
			return;
		}
		
		Venue testVenue = new Venue();
		testVenue.setName("testVenue");
		testVenue.setCapacity(100);
		
		venueService.save(testVenue);
		
		Venue testVenue1 = new Venue();
		testVenue1.setName("256");
		testVenue1.setCapacity(100);
		
		venueService.save(testVenue1);
		
		Event example = new Event();
		example.setName("Apocalypse");
		example.setDate(new Date(System.currentTimeMillis()));
		example.setTime(new Date(System.currentTimeMillis()));
		example.setDescription("An event for the masses.");
		example.setVenue(testVenue);
		
		eventService.save(example);	
		
		Event example1 = new Event();
		example1.setName("256 night out");
		example1.setDate(new Date(System.currentTimeMillis() - 100000000));
		example1.setTime(new Date(System.currentTimeMillis() - 100000000));
		example1.setDescription("The best night in 256 so far.");
		example1.setVenue(testVenue1);
		
		eventService.save(example1);	
		

		
		

	}
}
