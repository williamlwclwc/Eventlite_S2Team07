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
@RequestMapping(value = "/api/profile", produces = { MediaType.APPLICATION_JSON_VALUE, MediaTypes.HAL_JSON_VALUE })
public class ProfileControllerApi {

	@Autowired
	private VenueService venueService;
	
	@RequestMapping(value = {"", "/venues"}, method = RequestMethod.GET)
	public Resources<Resource<Venue>> getOwnVenues(Authentication auth) {
		Link selfLink = linkTo(methodOn(ProfileControllerApi.class).getOwnVenues(auth)).withSelfRel();
		List<Resource<Venue>> resources = new ArrayList<Resource<Venue>>();
		List<Venue> ownVenues = new ArrayList<Venue>();
		if (auth != null) {
			ownVenues = venueService.findAllByOwnerName(auth.getName());
		} else {
			ownVenues = venueService.findAllByOwnerName("Markel");
		}
		for (final Venue v : ownVenues) {
	        Link selfLinkVenue = linkTo(methodOn(VenuesControllerApi.class).getVenue(v.getId())).withSelfRel();
	        Link next3EventsLink = linkTo(VenuesControllerApi.class).slash(v.getId()).slash("next3events").withRel("next3events");
	        resources.add(new Resource<Venue>(v, selfLinkVenue, next3EventsLink));
	    }
		
		return new Resources<Resource<Venue>>(resources, selfLink);
	}
	
}