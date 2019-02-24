package uk.ac.man.cs.eventlite.controllers;

import java.util.ArrayList;
import java.util.Iterator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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

	@RequestMapping(method = RequestMethod.GET)
	public String getAllEvents(Model model) {

		model.addAttribute("events", eventService.findAll());

		return "events/index";
	}
	
	@RequestMapping(value = "/searchResult", method = RequestMethod.GET)
	public String resultEvents(Model model, 
			@RequestParam(value = "Search for events", required = false, defaultValue = "default") 
			String name, RedirectAttributes redirectAttrs) {
		int findFlag = 0;
		Iterable<Event> allEvents = new ArrayList<Event>();
		ArrayList<Event> resultEvents = new ArrayList<Event>();
		allEvents = eventService.findAll();
		Iterator<Event> itr = allEvents.iterator();
		while(itr.hasNext()) {
			Event ele = itr.next();
			if(ele.getName().indexOf(name) != -1) {
				resultEvents.add(ele);
				findFlag = 1;
			}
		}
		// if not found any results
		if(findFlag == 0) {
			redirectAttrs.addFlashAttribute("failed_message", "Events not found.");
			return "redirect:/events";
		}
		model.addAttribute("results", resultEvents);
		return "/events/searchResult";
	}

}
