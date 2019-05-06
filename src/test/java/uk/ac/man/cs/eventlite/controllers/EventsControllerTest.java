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

import java.text.SimpleDateFormat;
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
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.twitter.api.Twitter;
import org.springframework.social.twitter.api.impl.TwitterTemplate;
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
	private final static String venueId = "1";
	
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
	
	@Mock
	private ConnectionRepository connectionRepo;
	  
	@Mock
	Connection<Twitter> twitterConn;
	  
    String consumerKey = "LLykm2UhFxxpnimiclbZYz8yy"; 
	String consumerSecret = "H0wOV6cKR7JQjPIgyg26Nqk0BxYfGHECIXL68hMJExjh1Tgecw"; 
	String accessToken = "1108906765444354048-mZhs2J5y6yXGBFB3ky7phLefFsWU8d"; 
	String accessTokenSecret = "1F54ktRbYG94qdGzemi2FabtasM2guVlv7WzYaLroRP8c";
	  
	@Spy
	Twitter twitter = new TwitterTemplate(consumerKey, consumerSecret, accessToken, accessTokenSecret);

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
	  
	@Test
	public void testTwitterPost() throws Exception {
	    
		String localTime = String.valueOf((java.time.LocalTime.now()));
		String tooLongTweet = "mvc.perform(MockMvcRequestBuilders.get(\"/events/view/0/0\").with(user(\"Rob\").roles(Security.ADMIN_ROLE))\n" + 
				"	        .contentType(MediaType.APPLICATION_FORM_URLENCODED).with(csrf())\n" + 
				"	          .accept(MediaType.TEXT_HTML)\n" + 
				"	          .param(\"Share event\", \"Time: \" + localTime))\n" + 
				"	      .andExpect(status().is(302))\n" + 
				"	      .andExpect(handler().methodName(\"tweetEvent\"))\n" + 
				"	      .andExpect(flash().attributeExists(\"tweet_success\"))\n" + 
				"	      .andExpect(view().name(\"redirect:/events/view/{id}\")); \n" + 
				"	    \n" + 
				"	    // Test for duplicate tweet\n" + 
				"	    mvc.perform(MockMvcRequestBuilders.get(\"/events/view/0/0\").with(user(\"Rob\").roles(Security.ADMIN_ROLE))\n" + 
				"		        .contentType(MediaType.APPLICATION_FORM_URLENCODED).with(csrf())\n" + 
				"		          .accept(MediaType.TEXT_HTML)\n" + 
				"		          .param(\"Share event\", \"Time: \" + localTime))\n" + 
				"		      .andExpect(status().is(302))\n" + 
				"		      .andExpect(handler().methodName(\"tweetEvent\"))\n" + 
				"		      .andExpect(flash().attributeExists(\"tweet_failed\"))\n" + 
				"		      .andExpect(view().name(\"redirect:/events/view/{id}\"));\n" + 
				"	    \n" + 
				"	    // Test for too long tweet\n" + 
				"	    mvc.perform(MockMvcRequestBuilders.get(\"/events/view/0/0\").with(user(\"Rob\").roles(Security.ADMIN_ROLE))\n" + 
				"		        .contentType(MediaType.APPLICATION_FORM_URLENCODED).with(csrf())\n" + 
				"		          .accept(MediaType.TEXT_HTML)\n" + 
				"		          .param(\"Share event\", \"Time: \" + tooLongTweet))\n" + 
				"		      .andExpect(status().is(302))\n" + 
				"		      .andExpect(handler().methodName(\"tweetEvent\"))\n" + 
				"		      .andExpect(flash().attributeExists(\"tweet_failed\"))\n" + 
				"		      .andExpect(view().name(\"redirect:/events/view/{id}\")); ";
		
	    // TEST FOR WHEN USER IS AUTHORISED (LOGGED IN)
	    when(twitter.isAuthorized()).thenReturn(true);
	    
	    mvc.perform(MockMvcRequestBuilders.get("/events/view/0/0").with(user("Rob").roles(Security.ADMIN_ROLE))
	        .contentType(MediaType.APPLICATION_FORM_URLENCODED).with(csrf())
	          .accept(MediaType.TEXT_HTML)
	          .param("Share event", "Time: " + localTime))
	      .andExpect(status().is(302))
	      .andExpect(handler().methodName("tweetEvent"))
	      .andExpect(flash().attributeExists("tweet_success"))
	      .andExpect(view().name("redirect:/events/view/{id}")); 
	    
	    // Test for duplicate tweet
	    mvc.perform(MockMvcRequestBuilders.get("/events/view/0/0").with(user("Rob").roles(Security.ADMIN_ROLE))
		        .contentType(MediaType.APPLICATION_FORM_URLENCODED).with(csrf())
		          .accept(MediaType.TEXT_HTML)
		          .param("Share event", "Time: " + localTime))
		      .andExpect(status().is(302))
		      .andExpect(handler().methodName("tweetEvent"))
		      .andExpect(flash().attributeExists("tweet_failed"))
		      .andExpect(view().name("redirect:/events/view/{id}"));
	    
	    // Test for too long tweet
	    mvc.perform(MockMvcRequestBuilders.get("/events/view/0/0").with(user("Rob").roles(Security.ADMIN_ROLE))
		        .contentType(MediaType.APPLICATION_FORM_URLENCODED).with(csrf())
		          .accept(MediaType.TEXT_HTML)
		          .param("Share event", "Time: " + tooLongTweet))
		      .andExpect(status().is(302))
		      .andExpect(handler().methodName("tweetEvent"))
		      .andExpect(flash().attributeExists("tweet_failed"))
		      .andExpect(view().name("redirect:/events/view/{id}")); 
	}
	
	@Test
	public void testTwitterNoContent() throws Exception {
	    
	    // TEST FOR WHEN USER IS AUTHORISED (LOGGED IN)
	    when(twitter.isAuthorized()).thenReturn(true);
	    
	    mvc.perform(MockMvcRequestBuilders.get("/events/view/0/0").with(user("Rob").roles(Security.ADMIN_ROLE))
	        .contentType(MediaType.APPLICATION_FORM_URLENCODED).with(csrf())
	          .accept(MediaType.TEXT_HTML)
	          .param("Share event", "Time: " + ""))
	      .andExpect(status().is(302))
	      .andExpect(handler().methodName("tweetEvent"))
	      .andExpect(view().name("redirect:/events/view/{id}")); 
	}
	
	@Test
	public void testTwitterRedirectConnectionPage() throws Exception {
	    // TEST FOR WHEN USER IS NOT AUTHORISED (NOT LOGGED IN)
	    when(twitter.isAuthorized()).thenReturn(false);
	    
	    mvc.perform(MockMvcRequestBuilders.get("/events/view/0/0").with(user("Rob").roles(Security.ADMIN_ROLE))
	        .contentType(MediaType.APPLICATION_FORM_URLENCODED).with(csrf())
	          .accept(MediaType.TEXT_HTML)
	          .param("Share event", "Time: " + String.valueOf((java.time.LocalTime.now()))))
	      .andExpect(status().is(302))
	      .andExpect(handler().methodName("tweetEvent"))
	      .andExpect(view().name("redirect:/connect/twitter"));  
	    
	}
	@Test
	public void UpdateValidEvent() throws Exception {
		
		mvc.perform(MockMvcRequestBuilders.post("/events/update").with(user("Rob").roles(Security.ADMIN_ROLE)).contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
				.param("id", "1")	
				.param("name", name)
				.param("date", date)
				.param("time", time)
				.param("description", description)
				.param("venue.id", venueId)
				.accept(MediaType.TEXT_HTML).with(csrf()))
			.andExpect(status().isFound())
			.andExpect(view().name("redirect:/events"))
			.andExpect(model().hasNoErrors())
			.andExpect(handler().methodName("updateEvent"));

	}

	@Test
	public void eventStillUpdatedWithoutDescription() throws Exception {
		
		mvc.perform(MockMvcRequestBuilders.post("/events/update").with(user("Rob").roles(Security.ADMIN_ROLE)).contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
				.param("id", "1")	
				.param("name", name)
				.param("date", date)
				.param("time", time)
				.param("description", "")
				.param("venue.id", venueId)
				.accept(MediaType.TEXT_HTML).with(csrf()))
			.andExpect(status().isFound())
			.andExpect(view().name("redirect:/events"))
			.andExpect(model().hasNoErrors())
			.andExpect(handler().methodName("updateEvent"));

	}
	
	@Test
	public void eventStillUpdatedWithoutTime() throws Exception {
		
		mvc.perform(MockMvcRequestBuilders.post("/events/update").with(user("Rob").roles(Security.ADMIN_ROLE)).contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
				.param("id", "1")	
				.param("name", name)
				.param("date", date)
				.param("time", "")
				.param("description", description)
				.param("venue.id", venueId)
				.accept(MediaType.TEXT_HTML).with(csrf()))
			.andExpect(status().isFound())
			.andExpect(view().name("redirect:/events"))
			.andExpect(model().hasNoErrors())
			.andExpect(handler().methodName("updateEvent"));

	}
	
	@Test
	public void createValidEvent() throws Exception {
		
		mvc.perform(MockMvcRequestBuilders.post("/events").with(user("Rob").roles(Security.ADMIN_ROLE)).contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
				.param("name", name)
				.param("date", date)
				.param("time", time)
				.param("description", description)
				.param("venue.id", venueId)
				.accept(MediaType.TEXT_HTML).with(csrf()))
			.andExpect(status().isFound())
			.andExpect(view().name("redirect:/events"))
			.andExpect(model().hasNoErrors())
			.andExpect(handler().methodName("createEvent"));

	}

	@Test
	public void eventStillCreatedWithoutDescription() throws Exception {
		
		mvc.perform(MockMvcRequestBuilders.post("/events").with(user("Rob").roles(Security.ADMIN_ROLE)).contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
				.param("name", name)
				.param("date", date)
				.param("time", time)
				.param("description", "")
				.param("venue.id", venueId)
				.accept(MediaType.TEXT_HTML).with(csrf()))
			.andExpect(status().isFound())
			.andExpect(view().name("redirect:/events"))
			.andExpect(model().hasNoErrors())
			.andExpect(handler().methodName("createEvent"));

	}
	@Test
	public void eventStillCreatedWithoutTime() throws Exception {
		
		mvc.perform(MockMvcRequestBuilders.post("/events").with(user("Rob").roles(Security.ADMIN_ROLE)).contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
				.param("name", name)
				.param("date", date)
				.param("time", "")
				.param("description", description)
				.param("venue.id", venueId)
				.accept(MediaType.TEXT_HTML).with(csrf()))
			.andExpect(status().isFound())
			.andExpect(view().name("redirect:/events"))
			.andExpect(model().hasNoErrors())
			.andExpect(handler().methodName("createEvent"));

	}
	
	@Test
	public void updateEventNoCsrf() throws Exception {
	
		mvc.perform(MockMvcRequestBuilders.post("/events/update").with(user("Rob").roles(Security.ADMIN_ROLE)).contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)	
			.param("id", "1")	
			.param("name", name)
			.param("date", date)
			.param("time", time)
			.param("description", description)
			.param("venue.id", venueId)
			.accept(MediaType.TEXT_HTML))
		.andExpect(status().isForbidden());

}
	
	
	@Test
	public void createEventNoCsrf() throws Exception {
	
		mvc.perform(MockMvcRequestBuilders.post("/events").with(user("Rob").roles(Security.ADMIN_ROLE)).contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)		
			.param("name", name)
			.param("date", date)
			.param("time", time)
			.param("description", description)
			.param("venue.id", venueId)
			.accept(MediaType.TEXT_HTML))
		.andExpect(status().isForbidden());

}

	@Test
	public void updateVenueWithBadRole() throws Exception {
		mvc.perform(MockMvcRequestBuilders.post("/events/update").with(user("Rob").roles(BAD_ROLE)).contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
				.param("id", "1")
				.param("name", name)
				.param("date", date)
				.param("time", time)
				.param("description", description)
				.param("venue.id", venueId)
				.accept(MediaType.TEXT_HTML).with(csrf()))
			.andExpect(status().isForbidden());

		verify(eventService, never()).save(event);
	}
	
	
	@Test
	public void createVenueWithBadRole() throws Exception {
		mvc.perform(MockMvcRequestBuilders.post("/events").with(user("Rob").roles(BAD_ROLE)).contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
				.param("name", name)
				.param("date", date)
				.param("time", time)
				.param("description", description)
				.param("venue.id", venueId)
				.accept(MediaType.TEXT_HTML).with(csrf()))
			.andExpect(status().isForbidden());

		verify(eventService, never()).save(event);
	}
	
	
	@Test
	public void updateWithoutName() throws Exception{
		invalidEventUpdate("", date, time, description, venueId, "name");
	}
	
	@Test
	public void updateWithoutDate() throws Exception{
		invalidEventUpdate(name, "", time, description, venueId, "date");
	}
	
	@Test
	public void updateWithoutVenue() throws Exception{
		invalidEventUpdate(name, date, time, description, "", "venue.id");
	}
	
	@Test
	public void updateWithNameTooLong() throws Exception{//tested with 260 chars
		invalidEventUpdate("abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyz", 
				date, time, description, venueId, "name");
	}
	
	@Test
	public void updateWithPastDate() throws Exception{
		invalidEventUpdate(name, "2000-08-01", time, description, venueId, "date");
	}
	
	@Test
	public void updateWithDescriptionTooLong() throws Exception{//tested with 520 chars
		invalidEventUpdate(name, date, time, "abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyz", 
				venueId, "description");
	}
	
	private void invalidEventUpdate(String name1, String date1, String time1, String description1, String venue_id1, String errors1) throws Exception{
		mvc.perform(MockMvcRequestBuilders.post("/events/update").with(user("Rob").roles(Security.ADMIN_ROLE)).contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
				.param("id", "1")	
				.param("name", name1)
				.param("date", date1)
				.param("time", time1)
				.param("description", description1)
				.param("venue.id", venue_id1)
				.accept(MediaType.TEXT_HTML).with(csrf()))
			.andExpect(status().isOk()).andExpect(view().name("events/update"))
			.andExpect(model().attributeHasFieldErrors("event", errors1))
			.andExpect(handler().methodName("updateEvent")).andExpect(flash().attributeCount(0));
		verify(eventService, never()).save(event);
	}

	@Test
	public void createWithoutName() throws Exception{
		invalidEventCreate("", date, time, description, venueId, "name");
	}
	
	@Test
	public void createWithoutDate() throws Exception{
		invalidEventCreate(name, "", time, description, venueId, "date");
	}
	
	@Test
	public void createWithoutVenue() throws Exception{
		invalidEventCreate(name, date, time, description, "", "venue.id");
	}
	
	@Test
	public void createWithNameTooLong() throws Exception{//tested with 260 chars
		invalidEventCreate("abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyz", 
				date, time, description, venueId, "name");
	}
	
	@Test
	public void createWithPastDate() throws Exception{
		invalidEventCreate(name, "2000-08-01", time, description, venueId, "date");
	}
	
	@Test
	public void createWithDescriptionTooLong() throws Exception{//tested with 520 chars
		invalidEventCreate(name, date, time, "abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyz", 
				venueId, "description");
	}
	
	private void invalidEventCreate(String name1, String date1, String time1, String description1, String venue_id1, String errors1) throws Exception{
		mvc.perform(MockMvcRequestBuilders.post("/events").with(user("Rob").roles(Security.ADMIN_ROLE)).contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)	
				.param("name", name1)
				.param("date", date1)
				.param("time", time1)
				.param("description", description1)
				.param("venue.id", venue_id1)
				.accept(MediaType.TEXT_HTML).with(csrf()))
			.andExpect(status().isOk()).andExpect(view().name("events/new"))
			.andExpect(model().attributeHasFieldErrors("event", errors1))
			.andExpect(handler().methodName("createEvent")).andExpect(flash().attributeCount(0));
		verify(eventService, never()).save(event);
	}
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
