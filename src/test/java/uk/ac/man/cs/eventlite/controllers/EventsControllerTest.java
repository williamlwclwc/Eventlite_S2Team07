package uk.ac.man.cs.eventlite.controllers;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.handler;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.Collections;
import java.util.Date;

import javax.servlet.Filter;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import uk.ac.man.cs.eventlite.config.Security;
import uk.ac.man.cs.eventlite.EventLite;
import uk.ac.man.cs.eventlite.dao.EventService;
import uk.ac.man.cs.eventlite.dao.VenueService;
import uk.ac.man.cs.eventlite.entities.Event;
import uk.ac.man.cs.eventlite.entities.Venue;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = EventLite.class)
@AutoConfigureMockMvc
@DirtiesContext
@ActiveProfiles("test")
public class EventsControllerTest {

	private MockMvc mvc;

	
	private final static String BAD_ROLE = "USER";
	private final static String name = "Event Beta";
	private final static String date = "2019-07-11";
	private final static String time = "10:30";
	private final static String description = "Blah, blah, blah";
	
	
	@Autowired
	private Filter springSecurityFilterChain;

	@Mock
	private Event event;

	@Mock
	private Venue venue;

	@Mock
	private EventService eventService;

	@Mock
	private VenueService venueService;

	@InjectMocks
	private EventsController eventsController;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		mvc = MockMvcBuilders.standaloneSetup(eventsController).apply(springSecurity(springSecurityFilterChain))
				.build();
	}

	@Test
	public void getIndexWhenNoEvents() throws Exception {
		when(eventService.findAllFutureEvents()).thenReturn(Collections.<Event> emptyList());


		mvc.perform(get("/events").accept(MediaType.TEXT_HTML)).andExpect(status().isOk())
				.andExpect(view().name("events/index")).andExpect(handler().methodName("getAllEvents"));

		verify(eventService).findAllFutureEvents();

		verifyZeroInteractions(event);

	}

	@Test
	public void getIndexWithEvents() throws Exception {
		when(eventService.findAllFutureEvents()).thenReturn(Collections.<Event> singletonList(event));
		when(venueService.findAll()).thenReturn(Collections.<Venue> singletonList(venue));

		mvc.perform(get("/events").accept(MediaType.TEXT_HTML)).andExpect(status().isOk())
				.andExpect(view().name("events/index")).andExpect(handler().methodName("getAllEvents"));

		verify(eventService).findAllFutureEvents();

		verifyZeroInteractions(event);

	}
	
	@Test
	public void deleteURIRedirect() throws Exception {
		mvc.perform(MockMvcRequestBuilders.delete("/events/1").with(user("Rob").roles(Security.ADMIN_ROLE))
				.contentType(MediaType.APPLICATION_FORM_URLENCODED).with(csrf()))
				.andExpect(status().isFound())
				.andExpect(view().name("redirect:/events"))
				.andExpect(model().hasNoErrors());
	}
	
	//=================Draft Code=======
	/* Copied code over from venueControllerTests and currently need to figure out how to pass a mock venue as a 
	 * parameter to the event to make it work.*/
//	@Test
//	public void createValidEvent() throws Exception {
//		ArgumentCaptor<Event> arg = ArgumentCaptor.forClass(Event.class);
//		when(venueService.findById(1)).thenReturn(venue);
//		
//		mvc.perform(MockMvcRequestBuilders.post("/events").with(user("Rob").roles(Security.ADMIN_ROLE)).contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
//				.param("name", name)
//				.param("date", date)
//				.param("time", time)
//				.param("description", description)
//				.param("venue", String.valueOf(venue.getId()))
//				.accept(MediaType.TEXT_HTML).with(csrf()))
//			.andExpect(status().isOk()).andExpect(content().string(""))
//			.andExpect(model().hasNoErrors())
//			.andExpect(handler().methodName("createEvent"));
//		
//		verify(eventService).save(arg.capture());
//		assertThat(name, equalTo(arg.getValue().getName()));
//	
//	}

//	@Test
//	public void updateEventNoCsrf() throws Exception {
//		mvc.perform(MockMvcRequestBuilders.post("/events/update").with(user("Rob").roles(Security.ADMIN_ROLE)).contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
//				.param("id", "1")
//				.param("name", name)
//				.param("date", date)
//				.param("time", time)
//				.param("description", description)
//				.accept(MediaType.TEXT_HTML))
//				.andExpect(status().isForbidden());
//		
//		verify(eventService, never()).save(event);
//	}
//	
//	@Test
//	public void updateVenueWithBadRole() throws Exception {
//		mvc.perform(MockMvcRequestBuilders.post("/venues/update").with(user("Rob").roles(BAD_ROLE)).contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
//				.param("id", "1")
//				.param("name", name)
//				.param("roadName", roadName)
//				.param("postCode", postCode)
//				.param("capacity", capacity)
//				.accept(MediaType.TEXT_HTML))
//				.andExpect(status().isForbidden());
//		
//		verify(venueService, never()).save(venue);
//	}
//	

//	
//	@Test
//	public void UpdateWithoutName() throws Exception{
//		InvalidVenueUpdate("", roadName, postCode, capacity, "name");
//	}
//	
//	@Test
//	public void UpdateWithTooLongName() throws Exception{//tested with 260 chars
//		InvalidVenueUpdate("abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyz", 
//				roadName, postCode, capacity, "name");
//	}
//	
//	@Test
//	public void UpdateWithoutRoadName() throws Exception{
//		InvalidVenueUpdate(name, "", postCode, capacity, "roadName");
//	}
//	
//	@Test
//	public void UpdateWithTooLongRoadName() throws Exception{
//		InvalidVenueUpdate(name, 
//				"abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyz", 
//				postCode, capacity, "roadName");
//	}
//	
//	@Test
//	public void UpdateWithoutPostCode() throws Exception{
//		InvalidVenueUpdate(name, roadName, "", capacity, "postCode");
//	}
//	
//	@Test
//	public void UpdateWithoutCapacity() throws Exception{
//		InvalidVenueUpdate(name, roadName, postCode, "", "capacity");
//	}
//	
//	@Test
//	public void UpdateWithNegativeCapacity() throws Exception{
//		InvalidVenueUpdate(name, roadName, postCode, "-1", "capacity");
//	}
//	
//	private void InvalidVenueUpdate(String name1, String roadName1, String postCode1, String capacity1, String errors1) throws Exception{
//		mvc.perform(MockMvcRequestBuilders.post("/venues/update").with(user("Rob").roles(Security.ADMIN_ROLE)).contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
//				.param("id", "1")
//				.param("name", name1)
//				.param("roadName", roadName1)
//				.param("postCode", postCode1)
//				.param("capacity", capacity1)
//				.accept(MediaType.TEXT_HTML).with(csrf()))
//			.andExpect(status().isOk()).andExpect(view().name("venues/update"))
//			.andExpect(model().attributeHasFieldErrors("venue", errors1))
//			.andExpect(handler().methodName("SaveEditedVenue")).andExpect(flash().attributeCount(0));
//		verify(venueService, never()).save(venue);
//	}
//=========================
	@Test
	public void searchURIRedirect() throws Exception {
		when(eventService.findAll()).thenReturn(null);
		mvc.perform(MockMvcRequestBuilders.get("/events/searchResult?Search+for+events=A")
				.accept(MediaType.TEXT_HTML))
				.andExpect(status().isFound())
				.andExpect(view().name("redirect:/events"))
				.andExpect(handler().methodName("resultEvents"));
		verify(eventService).findAll();
	}
}
