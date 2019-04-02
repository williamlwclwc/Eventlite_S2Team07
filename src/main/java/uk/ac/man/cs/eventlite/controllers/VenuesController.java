package uk.ac.man.cs.eventlite.controllers;

import javax.validation.ConstraintViolationException;
import javax.validation.Valid;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
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
import uk.ac.man.cs.eventlite.entities.Venue;

import java.util.List;
import com.mapbox.api.geocoding.v5.MapboxGeocoding;
import com.mapbox.api.geocoding.v5.models.CarmenFeature;
import com.mapbox.api.geocoding.v5.models.GeocodingResponse;
import com.mapbox.geojson.Point;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@Controller
@RequestMapping(value = "/venues", produces = { MediaType.TEXT_HTML_VALUE })
public class VenuesController {

	@Autowired
	private EventService eventService;

	@Autowired
	private VenueService venueService;

	@RequestMapping(method = RequestMethod.GET)
	public String getAllVenues(Model model) {

		model.addAttribute("venues", venueService.findAll());

		return "venues/index";
	}
	
	@RequestMapping(value = "/new", method = RequestMethod.GET)
	public String newVenue(Model model) {
		if (!model.containsAttribute("venue")) {
			model.addAttribute("venue", new Venue());
			model.addAttribute("venues", venueService.findAll());
		}

		return "venues/new";
	}
	
	@RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	public String createEvent(@RequestBody @Valid @ModelAttribute Venue venue,
			BindingResult errors, Model model, RedirectAttributes redirectAttrs) {

		if (errors.hasErrors()) {
			model.addAttribute("venue", venue);
			return "venues/new";
		}

		venueService.save(venue);
		redirectAttrs.addFlashAttribute("ok_message", "New venue added.");
		
		setVenueCoordinates(venue);

		return "redirect:/venues";
	}
	
	@RequestMapping(value = "/update/{id}", method = RequestMethod.GET)
	public String editVenue(@PathVariable("id") long id, Model model) {

		Venue e = venueService.findById(id);
		model.addAttribute("venue", e);
		
		setVenueCoordinates(e);

		return "venues/update";
	}
	
	
	@RequestMapping(value = "/update", method = RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	public String SaveEditedVenue(@RequestBody @Valid @ModelAttribute Venue venue,
			BindingResult errors, Model model, RedirectAttributes redirectAttrs) {
		if (errors.hasErrors()) {
			model.addAttribute("venue", venue);
			return "venues/update";
		}

		venueService.save(venue);
		redirectAttrs.addFlashAttribute("ok_message", "Venue updated.");
		
		setVenueCoordinates(venue);

		return "redirect:/venues";
	}
//	@RequestMapping(value = "/update/{id}", method = RequestMethod.GET)
//	public String editVenue(@PathVariable("id") long id, Model model) {
//
//		Venue e = venueService.findById(id);
//		model.addAttribute("venue", e);
//		model.addAttribute("venues", venueService.findAll());
//
//		return "venues/update";
//	}
//	
//	
//	@RequestMapping(value = "/update", method = RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
//	public String updateVenue(@RequestBody @Valid @ModelAttribute Venue venue,
//			BindingResult errors, Model model, RedirectAttributes redirectAttrs) {
//		if (errors.hasErrors()) {
//			model.addAttribute("venue", venue);
//			model.addAttribute("venues", venueService.findAll());
//			return "venues/update";
//		}
//
//		venueService.save(venue);
//		redirectAttrs.addFlashAttribute("ok_message", "Venue updated.");
//
//		return "redirect:/venues";
//
//	}
//
//	@RequestMapping(value = "view/{id}", method = RequestMethod.GET)
//	public String viewVenue(@PathVariable("id") long id, Model model) {
//		Venue venue = venueService.findById(id);
//		model.addAttribute("venue", venue);
//		return "venues/view";
//    }
//
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public String deleteVenue(@PathVariable("id") long id) {
	
			try {
				venueService.delete(id);
				return "redirect:/venues";
			}
			catch (Exception e) {
				return "redirect:/venues";
			}
		
		
	}	
//
	@RequestMapping(value = "/searchResult", method = RequestMethod.GET)
	public String resultVenues(Model model, 
			@RequestParam(value = "Search for venues", required = false, defaultValue = "default") 
			String name, RedirectAttributes redirectAttrs) 
	{
		name = name.toLowerCase();
		int findFlag = 0;
		Iterable<Venue> allVenues = new ArrayList<Venue>();
		ArrayList<Venue> resultVenues = new ArrayList<Venue>();
		allVenues = venueService.findAll();
		if(allVenues != null) {
			Iterator<Venue> itr = allVenues.iterator();
			while(itr.hasNext()) {
				Venue ele = itr.next();
				if(ele.getName().toLowerCase().indexOf(name) != -1) {
					resultVenues.add(ele);
					findFlag = 1;
				}
			}
		}
		// if not found any results
		if(findFlag == 0) {
			redirectAttrs.addFlashAttribute("failed_message", "Venues not found.");
			return "redirect:/venues";
		}
		model.addAttribute("results", resultVenues);
		return "/venues/searchResult";
	}		
	
	@RequestMapping(value = "view/{id}", method = RequestMethod.GET)
	public String viewVenue(@PathVariable("id") long id, Model model) {
		Venue venue = venueService.findById(id);
		model.addAttribute("venue", venue);
		
		List<Event> upcomingEvents = new ArrayList<Event>();
		for (Event e : venue.getEvents()) {
			if (e.getDate().compareTo( new Date(System.currentTimeMillis())) > 0)
				upcomingEvents.add(e);
		}
			
		model.addAttribute("events", upcomingEvents);
		return "venues/view";
    }
	
	public void setVenueCoordinates(final Venue venue)
	{
		MapboxGeocoding mapboxGeocoding = MapboxGeocoding.builder()
				.accessToken("pk.eyJ1IjoiZXZlbnRsaXRlaDA3IiwiYSI6ImNqdGN1aXU0dDB5MGQzeXBjMDh0bXBmZWEifQ.cAtpPyEFrf04RlRjdtfc1w")
				.country("GB")
				.query(venue.getPostCode())
				.build();
		
		mapboxGeocoding.enqueueCall(new Callback<GeocodingResponse>() {
			@Override
			public void onResponse(Call<GeocodingResponse> call, Response<GeocodingResponse> response) {
				try {
					Thread.sleep(1000L);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				List<CarmenFeature> results = response.body().features();
		 
				if (results.size() > 0) {
		 
				  // Log the first results Point.
				  Point firstResultPoint = results.get(0).center();
				  
				  
				  venue.setLatitude(firstResultPoint.latitude());
				  venue.setLongitude(firstResultPoint.longitude());
				  
				  venueService.save(venue);  
		 
				} else {
		 
				  // No result for your request were found.
				  System.out.println("No result :(");
		 
				}
			}
		 
			@Override
			public void onFailure(Call<GeocodingResponse> call, Throwable throwable) {
				throwable.printStackTrace();
			}
		});
	}
}

