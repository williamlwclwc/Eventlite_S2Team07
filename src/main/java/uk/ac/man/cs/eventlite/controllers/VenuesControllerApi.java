package uk.ac.man.cs.eventlite.controllers;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import uk.ac.man.cs.eventlite.dao.VenueService;
import uk.ac.man.cs.eventlite.entities.Event;
import uk.ac.man.cs.eventlite.entities.Venue;

@RestController
@RequestMapping(value = "/api/venues", produces = { MediaType.APPLICATION_JSON_VALUE, MediaTypes.HAL_JSON_VALUE })
public class VenuesControllerApi {

	@Autowired
	private VenueService venueService;

	@RequestMapping(method = RequestMethod.GET)
	public Resources<Resource<Venue>> getAllVenues() {

		return venueListToResource(venueService.findAll());
	}
	
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public Resource<Venue> getVenue(@PathVariable("id") long id) {
		Venue venue = venueService.findById(id);
		
		return venueToResource(venue);
	}
	
	@RequestMapping(value = "/{id}/next3events", method = RequestMethod.GET)
	public Resources<Resource<Event>> getNext3Events(@PathVariable("id") long id) {
		Venue venue = venueService.findById(id);
		List<Event> next3Events = new ArrayList<Event>();
		for (Event e : venue.getEvents()) {
			if (e.getDate().compareTo( new Date(System.currentTimeMillis())) > 0)
				next3Events.add(e);
		}
		if (next3Events.size() > 3)
			next3Events = next3Events.subList(0, 3);
		
		Link selfLink = linkTo(methodOn(VenuesControllerApi.class).getNext3Events(id)).withSelfRel();
		List<Resource<Event>> resources = new ArrayList<Resource<Event>>();
		for (final Event e : next3Events) {
	        Link selfLinkEvent = linkTo(methodOn(EventsControllerApi.class).getEvent(e.getId())).withSelfRel();
	        resources.add(new Resource<Event>(e, selfLinkEvent));
	    }
		
		return new Resources<Resource<Event>>(resources, selfLink);
	}
	
	private Resource<Venue> venueToResource(Venue venue) {
		Link selfLink = linkTo(VenuesControllerApi.class).slash(venue.getId()).withSelfRel();
		Link venueLink = linkTo(VenuesControllerApi.class).slash(venue.getId()).withRel("venue");
		Link eventsLink = linkTo(VenuesControllerApi.class).slash(venue.getId()).slash("events").withRel("events");
		Link next3EventsLink = linkTo(VenuesControllerApi.class).slash(venue.getId()).slash("next3events").withRel("next3events");

		return new Resource<Venue>(venue, selfLink, venueLink, eventsLink, next3EventsLink);
	}

	private Resources<Resource<Venue>> venueListToResource(Iterable<Venue> venues) {
		Link selfLink = linkTo(methodOn(VenuesControllerApi.class).getAllVenues()).withSelfRel();
		Link profileLink = linkTo(ProfileControllerApi.class).slash("venues").withRel("profile");

		List<Resource<Venue>> resources = new ArrayList<Resource<Venue>>();
		for (Venue venue : venues) {
			resources.add(venueToResource(venue));
		}

		return new Resources<Resource<Venue>>(resources, selfLink, profileLink);
	}
}