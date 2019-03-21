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
			venueA.setRoadName("23 Manchester Road");
			venueA.setPostCode("E14 3BD");
			venueA.setCapacity(50);
			venueA.setLongitude(-0.012425);
			venueA.setLatitude(51.487696);
			
			venueService.save(venueA);
			
			Venue venueB = new Venue();
			venueB.setName("Venue B");
			venueB.setRoadName("Highland Road");
			venueB.setPostCode("S43 2EZ");
			venueB.setCapacity(1000);
			venueB.setLongitude(-1.399808);
			venueB.setLatitude(53.279673);
			
			venueService.save(venueB);
			
			Venue venueC = new Venue();
			venueC.setName("Venue C");
			venueC.setRoadName("19 Acacia Avenue");
			venueC.setPostCode("WA15 8QY");
			venueC.setCapacity(10);
			venueC.setLongitude(53.381270);
			venueC.setLatitude(-2.338228);
			
			venueService.save(venueC);
			
			Venue barFootage = new Venue();
			barFootage.setName("Bar Footage");
			barFootage.setRoadName("Grosvenor Street");
			barFootage.setPostCode("M1 7DZ");
			barFootage.setCapacity(50);
			barFootage.setLongitude(53.469205);
			barFootage.setLatitude(-2.237385);
			
			venueService.save(barFootage);
			
			Event alpha = new Event();
			alpha.setName("Event Alpha");
			Date date1 = ft.parse("2019-07-11 12:30");
			alpha.setDate(date1);
			alpha.setTime(date1);
			alpha.setDescription("An event for the masses.");
			alpha.setVenue(venueC);
			
			eventService.save(alpha);	
			
			Event beta = new Event();
			beta.setName("Event Beta");
			Date date2 = ft.parse("2019-07-11 10:30");
			beta.setDate(date2);
			beta.setTime(date2);
			beta.setDescription("The best night in 256 so far.");
			beta.setVenue(barFootage);
			
			eventService.save(beta);
			
			Event apple = new Event();
			apple.setName("Event Apple");
			Date date3 = ft1.parse("2019-07-12");
			apple.setDate(date3);
			apple.setTime(date3);
			apple.setDescription("No apple allowed.");
			apple.setVenue(venueA);
			
			eventService.save(apple);
			
			Event former = new Event();
			former.setName("Event Former");
			Date date4 = ft.parse("2018-01-11 11:00");
			former.setDate(date4);
			former.setTime(date4);
			former.setDescription("The best night in 256 so far.");
			former.setVenue(venueB);
			
			eventService.save(former);
			
			Event previous = new Event();
			previous.setName("Event Previous");
			Date date5 = ft.parse("2018-01-11 18:30");
			previous.setDate(date5);
			previous.setTime(date5);
			previous.setDescription("The best night in 256 so far.");
			previous.setVenue(venueA);
			
			eventService.save(previous);
			
			Event past = new Event();
			past.setName("Event Past");
			Date date6 = ft.parse("2018-01-10 17:00");
			past.setDate(date6);
			past.setTime(date6);
			past.setDescription("The best night in 256 so far.");
			past.setVenue(venueA);
			
			eventService.save(past);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		

		
		

	}
}
