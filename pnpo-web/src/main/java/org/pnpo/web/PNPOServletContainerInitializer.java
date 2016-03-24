package org.pnpo.web;

import java.util.Set;

import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PNPOServletContainerInitializer implements ServletContainerInitializer {
	private static final Logger logger = LoggerFactory.getLogger(PNPOServletContainerInitializer.class);

	@Override
	public void onStartup(Set<Class<?>> classes, ServletContext context) throws ServletException {
		logger.info("onStartup");
		// TODO
	}

}
