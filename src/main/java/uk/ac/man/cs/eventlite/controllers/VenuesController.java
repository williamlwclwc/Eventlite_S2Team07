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

		return "redirect:/venues";
	}
	
	@RequestMapping(value = "/update/{id}", method = RequestMethod.GET)
	public String editVenue(@PathVariable("id") long id, Model model) {

		Venue e = venueService.findById(id);
		model.addAttribute("venue", e);

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
				System.out.println();
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
		Iterator<Venue> itr = allVenues.iterator();
		while(itr.hasNext()) {
			Venue ele = itr.next();
			if(ele.getName().toLowerCase().indexOf(name) != -1) {
				resultVenues.add(ele);
				findFlag = 1;
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
		return "venues/view";
    }
}

