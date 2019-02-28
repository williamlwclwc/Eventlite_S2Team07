package uk.ac.man.cs.eventlite.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.thymeleaf.spring4.view.ThymeleafViewResolver;

@Configuration
public class ContentNegotiation extends WebMvcConfigurerAdapter {

	@Override
	public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
		configurer.favorPathExtension(false).favorParameter(false).ignoreAcceptHeader(false).useJaf(false)
		.defaultContentType(MediaType.TEXT_HTML).mediaType("html", MediaType.TEXT_HTML)
		.mediaType("json", MediaType.APPLICATION_JSON);
		
		// using the Thymeleaf templates
		ThymeleafViewResolver  resolver = new ThymeleafViewResolver();
		resolver.setCache(false);
	}
}
