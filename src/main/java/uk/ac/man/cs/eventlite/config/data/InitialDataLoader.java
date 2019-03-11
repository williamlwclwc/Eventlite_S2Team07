package uk.ac.man.cs.eventlite.config.data;

import java.text.ParseException;
import java.text.SimpleDateFormat;
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
	
	private SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	private SimpleDateFormat ft1 = new SimpleDateFormat("yyyy-MM-dd");

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {

		try {
			if (eventService.count() > 0) {
				log.info("Database already populated. Skipping data initialization.");
				return;
			}
			
			Venue venueA = new Venue();
			venueA.setName("Venue A");
			venueA.setCapacity(100);
			
			venueService.save(venueA);
			
			Venue venueB = new Venue();
			venueB.setName("Venue B");
			venueB.setCapacity(100);
			
			venueService.save(venueB);
			
			Event alpha = new Event();
			alpha.setName("Event Alpha");
			Date date1 = ft.parse("2019-07-11 12:30");
			alpha.setDate(date1);
			alpha.setTime(date1);
			alpha.setDescription("An event for the masses.");
			alpha.setVenue(venueB);
			
			eventService.save(alpha);	
			
			Event beta = new Event();
			beta.setName("Event Beta");
			Date date2 = ft.parse("2019-07-11 10:30");
			beta.setDate(date2);
			beta.setTime(date2);
			beta.setDescription("The best night in 256 so far.");
			beta.setVenue(venueB);
			
			eventService.save(beta);
			
			Event apple = new Event();
			apple.setName("Event Apple");
			Date date3 = ft1.parse("2019-07-12");
			apple.setDate(date3);
			apple.setTime(date3);
			apple.setDescription("No apple allowed.");
			apple.setVenue(venueA);
			
			eventService.save(apple);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		

		
		

	}
}
