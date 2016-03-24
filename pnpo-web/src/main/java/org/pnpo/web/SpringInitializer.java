package org.pnpo.web;

import java.util.EnumSet;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import javax.xml.ws.Dispatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.DispatcherServlet;

public class SpringInitializer implements WebApplicationInitializer {
	private static final Logger logger = LoggerFactory.getLogger(SpringInitializer.class);

	@Override
	public void onStartup(ServletContext servletContext) throws ServletException {
		logger.info("initialize spring");
		AnnotationConfigWebApplicationContext applicationContext = new AnnotationConfigWebApplicationContext();
		applicationContext.register(ApplicationContext.class);

		DispatcherServlet dispatcherServlet = servletContext.createServlet(DispatcherServlet.class);

		dispatcherServlet.setApplicationContext(applicationContext);

		ServletRegistration.Dynamic dispatcher = servletContext.addServlet("dispatcher", dispatcherServlet);
		dispatcher.setLoadOnStartup(2);
		dispatcher.addMapping("/requests/*");

		CharacterEncodingFilter characterEncodingFilter = servletContext.createFilter(CharacterEncodingFilter.class);
		characterEncodingFilter.setEncoding("UTF-8");
		characterEncodingFilter.setForceEncoding(true);

		FilterRegistration.Dynamic filter = servletContext.addFilter("characterEncodingFilter",
				characterEncodingFilter);

		filter.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), false, "/*");
	}

}
