package uk.ac.man.cs.eventlite.controllers;


import static org.hamcrest.Matchers.endsWith;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

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
public class VenuesControllerTest {

	private MockMvc mvc;
	
	
	private final static String BAD_ROLE = "USER";
	private final static String name = "test";
	private final static String roadName = "100 Oxford Road";
	private final static String postCode = "M33 13P";
	private final static String capacity = "100";
	private final static int capacityAsInt = 100;

	@Autowired
	private Filter springSecurityFilterChain;
	
	

	@Mock
	private Venue venue;

	@Mock
	private VenueService venueService;

	@InjectMocks
	private VenuesController venuesController;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		mvc = MockMvcBuilders.standaloneSetup(venuesController).apply(springSecurity(springSecurityFilterChain))
				.build();
	}

	@Test
	public void getIndexWhenNoVenues() throws Exception {
		when(venueService.findAll()).thenReturn(Collections.<Venue> emptyList());


		mvc.perform(get("/venues").accept(MediaType.TEXT_HTML)).andExpect(status().isOk())
				.andExpect(view().name("venues/index")).andExpect(handler().methodName("getAllVenues"));

		verify(venueService).findAll();

		verifyZeroInteractions(venue);

	}
	
	
	@Test
	public void getIndexWhenVenues() throws Exception {
		when(venueService.findAll()).thenReturn(Collections.<Venue> singletonList(venue));

		mvc.perform(MockMvcRequestBuilders.get("/venues").accept(MediaType.TEXT_HTML)).andExpect(status().isOk())
		.andExpect(view().name("venues/index")).andExpect(handler().methodName("getAllVenues"));

		verifyZeroInteractions(venue);
	}

	@Test
	public void getVenue() throws Exception {
		when(venueService.findById(1)).thenReturn(venue);

		mvc.perform(MockMvcRequestBuilders.get("/venues/view/1").contentType(MediaType.TEXT_HTML)).andExpect(status().isOk())
		.andExpect(view().name("venues/view")).andExpect(handler().methodName("viewVenue"));
		
		verify(venue);
	}
	
	@Test
	public void deleteVenueNoEvent() throws Exception {
		
		Set<Event> emptyEventSet = new HashSet<Event>();
		
		venue.setId(1);
		when(venueService.findById(1)).thenReturn(venue);
		when(venue.getEvents()).thenReturn(emptyEventSet);
		
		mvc.perform(MockMvcRequestBuilders.delete("/venues/1").with(user("Rob").roles(Security.ADMIN_ROLE))
				.contentType(MediaType.APPLICATION_FORM_URLENCODED).with(csrf()))
				.andExpect(status().isFound())
				.andExpect(view().name("redirect:/venues"))
				.andExpect(model().hasNoErrors());
	}
	
	@Test
	public void deleteVenueWithEvent() throws Exception {
		
		Set<Event> nonEmptyEventSet = new HashSet<Event>();
		Event anEvent = new Event();
		nonEmptyEventSet.add(anEvent);

		venue.setId(2);
		when(venueService.findById(2)).thenReturn(venue);
		when(venue.getEvents()).thenReturn(nonEmptyEventSet);
		
		mvc.perform(MockMvcRequestBuilders.delete("/venues/2").with(user("Rob").roles(Security.ADMIN_ROLE))
				.contentType(MediaType.APPLICATION_FORM_URLENCODED).with(csrf()))
				.andExpect(status().isFound())
				.andExpect(view().name("redirect:/venues/view/{id}"))
				.andExpect(model().hasNoErrors());
	}
	
	
	

	// ====TESTS FOR NEW VENUE===========
	@Test
	public void createVenueNoCsrf() throws Exception {
		mvc.perform(MockMvcRequestBuilders.post("/venues/new").with(user("Rob").roles(Security.ADMIN_ROLE)).contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
				.param("name", name)
				.param("roadName", roadName)
				.param("postCode", postCode)
				.param("capacity", capacity)
				.accept(MediaType.TEXT_HTML))
				.andExpect(status().isForbidden());
		verify(venueService, never()).save(venue);
	}

	@Test
	public void CreateVenueWithBadRole() throws Exception {
		mvc.perform(MockMvcRequestBuilders.post("/venues/new").with(user("Rob").roles(BAD_ROLE)).contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
				.param("name", name)
				.param("roadName", roadName)
				.param("postCode", postCode)
				.param("capacity", capacity)
				.accept(MediaType.TEXT_HTML).with(csrf()))
				.andExpect(status().isForbidden());	
		verify(venueService, never()).save(venue);
	}

	@Test
	public void createValidVenue() throws Exception {
		ArgumentCaptor<Venue> arg = ArgumentCaptor.forClass(Venue.class);

		mvc.perform(MockMvcRequestBuilders.post("/venues").with(user("Rob").roles(Security.ADMIN_ROLE)).contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
				.param("name", name)
				.param("roadName", roadName)
				.param("postCode", postCode)
				.param("capacity", capacity)
				.accept(MediaType.TEXT_HTML).with(csrf()))
			.andExpect(status().isFound()).andExpect(content().string(""))
			.andExpect(view().name("redirect:/venues")).andExpect(model().hasNoErrors())
			.andExpect(handler().methodName("createVenue"));

		verify(venueService).save(arg.capture());
		assertThat(name, equalTo(arg.getValue().getName()));

	}
	

	@Test
	public void CreateWithTooLongName() throws Exception{
		String invalid_name = new String(new char[260]).replace("\0", "A");
		InvalidVenueCreate(invalid_name, roadName, postCode, capacity, "name");
	}

	@Test
	public void CreateWithoutRoadName() throws Exception{
		InvalidVenueCreate(name, "", postCode, capacity, "roadName");
	}

	@Test
	public void CreateWithTooLongRoadName() throws Exception{
		String invalid_road_name = new String(new char[310]).replace("\0", "A");
		InvalidVenueCreate(name, invalid_road_name, postCode, capacity, "roadName");
	}

	@Test
	public void CreateWithoutPostCode() throws Exception{
		InvalidVenueUpdate(name, roadName, "", capacity, "postCode");
	}

	@Test
	public void CreateWithoutCapacity() throws Exception{
		InvalidVenueCreate(name, roadName, postCode, "", "capacity");
	}

	@Test
	public void CreateWithNegativeCapacity() throws Exception{
		InvalidVenueCreate(name, roadName, postCode, "-1", "capacity");
	}

	@Test
	public void updateVenueNoCsrf() throws Exception {
		mvc.perform(MockMvcRequestBuilders.post("/venues/update").with(user("Rob").roles(Security.ADMIN_ROLE)).contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
				.param("id", "1")
				.param("name", name)
				.param("roadName", roadName)
				.param("postCode", postCode)
				.param("capacity", capacity)
				.accept(MediaType.TEXT_HTML))
				.andExpect(status().isForbidden());
		
		verify(venueService, never()).save(venue);
	}
	
	@Test
	public void updateVenueWithBadRole() throws Exception {
		mvc.perform(MockMvcRequestBuilders.post("/venues/update").with(user("Rob").roles(BAD_ROLE)).contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
				.param("id", "1")
				.param("name", name)
				.param("roadName", roadName)
				.param("postCode", postCode)
				.param("capacity", capacity)
				.accept(MediaType.TEXT_HTML))
				.andExpect(status().isForbidden());
		
		verify(venueService, never()).save(venue);
	}
	
	@Test
	public void updateValidVenue() throws Exception {
		ArgumentCaptor<Venue> arg = ArgumentCaptor.forClass(Venue.class);
		
		mvc.perform(MockMvcRequestBuilders.post("/venues/update").with(user("Rob").roles(Security.ADMIN_ROLE)).contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
				.param("id", "1")
				.param("name", name)
				.param("roadName", roadName)
				.param("postCode", postCode)
				.param("capacity", capacity)
				.accept(MediaType.TEXT_HTML).with(csrf()))
			.andExpect(status().isFound()).andExpect(content().string(""))
			.andExpect(view().name("redirect:/venues")).andExpect(model().hasNoErrors())
			.andExpect(handler().methodName("SaveEditedVenue"));
		
		verify(venueService).save(arg.capture());
		assertThat(name, equalTo(arg.getValue().getName()));
		assertThat(roadName, equalTo(arg.getValue().getRoadName()));
		assertThat(postCode, equalTo(arg.getValue().getPostCode()));
		assertThat(capacityAsInt, equalTo(arg.getValue().getCapacity()));
	
	}
	
	@Test
	public void UpdateWithoutName() throws Exception{
		InvalidVenueUpdate("", roadName, postCode, capacity, "name");
	}
	
	@Test
	public void UpdateWithTooLongName() throws Exception{//tested with 260 chars
		InvalidVenueUpdate("abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyz", 
				roadName, postCode, capacity, "name");
	}
	
	@Test
	public void UpdateWithoutRoadName() throws Exception{
		InvalidVenueUpdate(name, "", postCode, capacity, "roadName");
	}
	
	@Test
	public void UpdateWithTooLongRoadName() throws Exception{
		InvalidVenueUpdate(name, 
				"abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyz", 
				postCode, capacity, "roadName");
	}
	
	@Test
	public void UpdateWithoutPostCode() throws Exception{
		InvalidVenueUpdate(name, roadName, "", capacity, "postCode");
	}
	
	@Test
	public void UpdateWithoutCapacity() throws Exception{
		InvalidVenueUpdate(name, roadName, postCode, "", "capacity");
	}
	
	@Test
	public void UpdateWithNegativeCapacity() throws Exception{
		InvalidVenueUpdate(name, roadName, postCode, "-1", "capacity");
	}
	
	private void InvalidVenueUpdate(String name1, String roadName1, String postCode1, String capacity1, String errors1) throws Exception{
		mvc.perform(MockMvcRequestBuilders.post("/venues/update").with(user("Rob").roles(Security.ADMIN_ROLE)).contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
				.param("id", "1")
				.param("name", name1)
				.param("roadName", roadName1)
				.param("postCode", postCode1)
				.param("capacity", capacity1)
				.accept(MediaType.TEXT_HTML).with(csrf()))
			.andExpect(status().isOk()).andExpect(view().name("venues/update"))
			.andExpect(model().attributeHasFieldErrors("venue", errors1))
			.andExpect(handler().methodName("SaveEditedVenue")).andExpect(flash().attributeCount(0));
		verify(venueService, never()).save(venue);
	}
	
	private void InvalidVenueCreate(String name1, String roadName1, String postCode1, String capacity1, String errors1) throws Exception{
		mvc.perform(MockMvcRequestBuilders.post("/venues").with(user("Rob").roles(Security.ADMIN_ROLE)).contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
				.param("name", name1)
				.param("roadName", roadName1)
				.param("postCode", postCode1)
				.param("capacity", capacity1)
				.accept(MediaType.TEXT_HTML).with(csrf()))
			.andExpect(status().isOk()).andExpect(view().name("venues/new"))
			.andExpect(model().attributeHasFieldErrors("venue", errors1))
			.andExpect(handler().methodName("createVenue")).andExpect(flash().attributeCount(0));
		verify(venueService, never()).save(venue);
	}

	@Test
	public void searchURIRedirect() throws Exception {
		when(venueService.findAll()).thenReturn(null);
		mvc.perform(MockMvcRequestBuilders.get("/venues/searchResult?Search+for+venues=A")
				.accept(MediaType.TEXT_HTML))
				.andExpect(status().isFound())
				.andExpect(view().name("redirect:/venues"))
				.andExpect(handler().methodName("resultVenues"));
		verify(venueService).findAll();
	}
	
	private static final String roadNameForCoords = "16 Peter St";
	private static final String postCodeForCoords = "M60 2DS";
	private static final double latitude = 53.4771068769154;
	private static final double longitude = -2.24505647734087;
	
	@Test
	public void coordinatesAfterValidCreate() throws Exception {
		ArgumentCaptor<Venue> arg = ArgumentCaptor.forClass(Venue.class);
		mvc.perform(MockMvcRequestBuilders.post("/venues").with(user("Rob").roles(Security.ADMIN_ROLE)).contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
				.param("name", name)
				.param("roadName", roadNameForCoords)
				.param("postCode", postCodeForCoords)
				.param("capacity", capacity)
				.accept(MediaType.TEXT_HTML).with(csrf()))
			.andExpect(status().isFound()).andExpect(content().string(""))
			.andExpect(view().name("redirect:/venues")).andExpect(model().hasNoErrors())
			.andExpect(handler().methodName("createVenue"));

		verify(venueService).save(arg.capture());
		assertThat(name, equalTo(arg.getValue().getName()));
		assertThat(roadNameForCoords,equalTo(arg.getValue().getRoadName()));
		assertThat(postCodeForCoords, equalTo(arg.getValue().getPostCode()));
		assertThat(latitude, equalTo(arg.getValue().getLatitude()));
		assertThat(longitude, equalTo(arg.getValue().getLongitude()));

	}
	
	@Test
	public void coordinatesAfterValidUpdate() throws Exception {
		ArgumentCaptor<Venue> arg = ArgumentCaptor.forClass(Venue.class);
		
		mvc.perform(MockMvcRequestBuilders.post("/venues/update").with(user("Rob").roles(Security.ADMIN_ROLE)).contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
				.param("id", "1")
				.param("name", name)
				.param("roadName", roadNameForCoords)
				.param("postCode", postCodeForCoords)
				.param("capacity", capacity)
				.accept(MediaType.TEXT_HTML).with(csrf()))
			.andExpect(status().isFound()).andExpect(content().string(""))
			.andExpect(view().name("redirect:/venues")).andExpect(model().hasNoErrors())
			.andExpect(handler().methodName("SaveEditedVenue"));
		
		verify(venueService).save(arg.capture());
		assertThat(name, equalTo(arg.getValue().getName()));
		assertThat(roadNameForCoords,equalTo(arg.getValue().getRoadName()));
		assertThat(postCodeForCoords, equalTo(arg.getValue().getPostCode()));
		assertThat(latitude, equalTo(arg.getValue().getLatitude()));
		assertThat(longitude, equalTo(arg.getValue().getLongitude()));
	
	}
}



