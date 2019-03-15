package uk.ac.man.cs.eventlite.controllers;

import static org.hamcrest.Matchers.endsWith;
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

import javax.servlet.Filter;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
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
	
//	
//	private final static String BAD_ROLE = "USER";
//	private final static String name = "";
//	private final static String roadName = "100 Oxford Road";
//	private final static String postCode = "M33 13P";
//	private final static int capacity = 100;

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

//	@Test
//	public void getVenue() throws Exception {
//		when(venueService.findById(1)).thenReturn(venue);
//
//		mvc.perform(MockMvcRequestBuilders.get("/venues/1").contentType(MediaType.TEXT_HTML)).andExpect(status().isOk())
//		.andExpect(view().name("venues/1")).andExpect(handler().methodName("editVenue"));
//
//		verify(venue).getName();
//	}
	
	

//	@Test
//	public void updateVenueNoCsrf() throws Exception {
//		mvc.perform(MockMvcRequestBuilders.post("/venues/1").contentType(MediaType.APPLICATION_FORM_URLENCODED)
//				.param("name", name)
//				.param("roadName", roadName)
//				.param("postCode", postCode)
//				.param("capacity", "100")
//				.accept(MediaType.TEXT_HTML))
//				.andExpect(status().isForbidden());
//		
//		verify(venueService, never()).save(venue);
//		
//	
//	}
	
//	@Test
//	public void updateVenue() throws Exception {
//		mvc.perform(MockMvcRequestBuilders.post("/venues/1").contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
//				.param("name", name)
//				.param("roadName", roadName)
//				.param("postCode", postCode)
//				.param("capacity", "100")
//				.param("id", "1")
//				.accept(MediaType.TEXT_HTML).with(csrf()))
//				.andExpect(status().isFound()).andExpect(content().string(""))
//				.andExpect(view().name("redirect:/venues")).andExpect(model().hasNoErrors())
//				.andExpect(handler().methodName("saveEditedVenue"));
//		
//		
//		
//	
//	}
	
	



}


