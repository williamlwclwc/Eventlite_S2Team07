package uk.ac.man.cs.eventlite.controllers;

import javax.validation.Valid;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import javax.inject.Inject;

import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.twitter.api.CursoredList;
import org.springframework.social.twitter.api.Twitter;
import org.springframework.social.twitter.api.TwitterProfile;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import uk.ac.man.cs.eventlite.dao.EventService;
import uk.ac.man.cs.eventlite.dao.VenueService;
import uk.ac.man.cs.eventlite.entities.Event;

@Controller
@RequestMapping(value = "/events", produces = { MediaType.TEXT_HTML_VALUE })
public class EventsController {

	@Autowired
	private EventService eventService;

	@Autowired
	private VenueService venueService;
	
	private Twitter twitter;
	
	private ConnectionRepository connectionRepository;

    @Inject
    public EventsController(Twitter twitter, ConnectionRepository connectionRepository) {
        this.twitter = twitter;
        this.connectionRepository = connectionRepository;
    }
    
	@RequestMapping(method = RequestMethod.GET)
	public String getAllEvents(Model model) {

		model.addAttribute("future_events", eventService.findAllFutureEvents());
		model.addAttribute("past_events", eventService.findAllPastEvents());

		return "events/index";
	}
	
	@RequestMapping(value = "/new", method = RequestMethod.GET)
	public String newEvent(Model model) {
		if (!model.containsAttribute("event")) {
			model.addAttribute("event", new Event());
			model.addAttribute("venues", venueService.findAll());
		}

		return "events/new";
	}

	@RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	public String createEvent(@RequestBody @Valid @ModelAttribute Event event,
			BindingResult errors, Model model, RedirectAttributes redirectAttrs, Authentication auth) {

		if (errors.hasErrors()) {
			model.addAttribute("event", event);
			model.addAttribute("venues", venueService.findAll());
			return "events/new";
		}
		event.setOrganiser(auth.getName());
		eventService.save(event);
		redirectAttrs.addFlashAttribute("ok_message", "New event added.");

		return "redirect:/events";
	}

	@RequestMapping(value = "/update/{id}", method = RequestMethod.GET)
	public String editEvent(@PathVariable("id") long id, Model model) {
		
		Event e = eventService.findById(id);
		model.addAttribute("event", e);
				
		model.addAttribute("venues", venueService.findAll());

		return "events/update";
	}
	
	
	@RequestMapping(value = "/update", method = RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	public String updateEvent(@RequestBody @Valid @ModelAttribute Event event,
			BindingResult errors, Model model, RedirectAttributes redirectAttrs) {
		if (errors.hasErrors()) {
			model.addAttribute("event", event);
			model.addAttribute("venues", venueService.findAll());
			return "events/update";
		}

		eventService.save(event);
		redirectAttrs.addFlashAttribute("ok_message", "Event updated.");

		return "redirect:/events";

	}

	@RequestMapping(value = "view/{id}", method = RequestMethod.GET)
	public String viewEvent(@PathVariable("id") long id, Model model, Authentication auth) {
		ArrayList<Event> mapEvents = new ArrayList<Event>();
		Event event = eventService.findById(id);
		model.addAttribute("event", event);
		mapEvents.add(event);
		if (auth != null && auth.isAuthenticated()) {
			mapEvents.addAll(eventService.findAllByOrganiserName(auth.getName()));
		}
		model.addAttribute("map_events", mapEvents);
		
		return "events/view";
    }

	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public String deleteEvent(@PathVariable("id") long id) {
		
			eventService.delete(id);
			return "redirect:/events";
		
		
	}	

	@RequestMapping(value = "/searchResult", method = RequestMethod.GET)
	public String resultEvents(Model model, 
			@RequestParam(value = "Search for events", required = false, defaultValue = "default") 
			String name, RedirectAttributes redirectAttrs) 
	{
		// get current time
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm"); // set format
		SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat time = new SimpleDateFormat("hh:mm");
		String currentTime = df.format(new Date());
		String eventTime;
		
		name = name.toLowerCase();
		int findFlag = 0;
		Iterable<Event> allEvents = new ArrayList<Event>();
		ArrayList<Event> previousEvents = new ArrayList<Event>();
		ArrayList<Event> upcomingEvents = new ArrayList<Event>();
		allEvents = eventService.findAll();
		if(allEvents != null) {
			Iterator<Event> itr = allEvents.iterator();
			while(itr.hasNext()) {
				Event ele = itr.next();
				if(ele.getName().toLowerCase().indexOf(name) != -1) {
					// get event time as string
					eventTime = date.format(ele.getDate()) + " " + time.format(ele.getTime());
					if(eventTime.compareTo(currentTime) < 0) {
						// previous
						previousEvents.add(ele);
					} else {
						// upcoming
						upcomingEvents.add(ele);
					}
					findFlag = 1;
				}
			}
		}
		// if not found any results
		if(findFlag == 0) {
			redirectAttrs.addFlashAttribute("failed_message", "Events not found.");
			return "redirect:/events";
		}
		model.addAttribute("previousResults", previousEvents);
		model.addAttribute("upcomingResults", upcomingEvents);
		return "/events/searchResult";
	}
			
}

