package uk.ac.man.cs.eventlite.controllers;

import java.util.ArrayList;
import java.util.Iterator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import uk.ac.man.cs.eventlite.dao.EventService;
import uk.ac.man.cs.eventlite.entities.Event;
import uk.ac.man.cs.eventlite.entities.Venue;

@Controller
@RequestMapping(value = "/", produces = { MediaType.TEXT_HTML_VALUE })
public class HomepageController {

	@Autowired
	private EventService eventService;


	@RequestMapping(method = RequestMethod.GET)
	public String getAllEvents(Model model) {

		
		
		Iterable<Event> allEvents = new ArrayList<Event>();
		allEvents = eventService.findAllFutureEvents();
		ArrayList<Venue> venues = new ArrayList<Venue>();
		ArrayList<Integer> numberOfEvents = new ArrayList<Integer>();
		
		Iterator<Event> itr = allEvents.iterator();
		while(itr.hasNext()) {
			Event event = itr.next();
			Venue venue = event.getVenue();
			if(!venues.contains(venue))
			{
				venues.add(venue);
				numberOfEvents.add(1);
			}
			else
			{
				numberOfEvents.set(venues.indexOf(venue), numberOfEvents.get(venues.indexOf(venue) + 1));
			}
		}
		numberOfEvents.set(1, 2);
		int max1 = -1, max2 = -1, max3 = -1;
		int index1 = 0, index2 = 0, index3 = 0;
		for(int i = 0; i < venues.size(); i++)
		{
			int currentNumber = numberOfEvents.get(i);
			if( currentNumber > max1)
			{
				max3 = max2;
				index3 = index2;
				
				max2 = max1;
				index2 = index1;
				
				max1 = currentNumber;
				index1 = i;
			}
			else
				if(currentNumber > max2)
				{
					max3 = max2;
					index3 = index2;
					
					max2 = currentNumber;
					index2 = i;
				}
				else
					if(currentNumber > max3)
					{
						max3 = currentNumber;
						index3 = i;
					}
		}
		
		Iterator<Event> newItr = allEvents.iterator();
		
		model.addAttribute("event1", newItr.next());
		model.addAttribute("event2", newItr.next());
		model.addAttribute("event3", newItr.next());
		model.addAttribute("venue1", venues.get(index1));
		model.addAttribute("venue2", venues.get(index2));
		model.addAttribute("venue3", venues.get(index3));
		
		return "/index";
	}
	
	
			
}

