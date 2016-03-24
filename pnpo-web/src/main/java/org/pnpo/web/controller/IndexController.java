package org.pnpo.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.JstlView;
import org.springframework.web.servlet.view.RedirectView;

@Controller
@RequestMapping("/")
public class IndexController {
	private static final Logger logger = LoggerFactory.getLogger(IndexController.class);

//	@RequestMapping("toIndex")
//	public ModelAndView toIndex() {
//		logger.debug("toIndex");
//		ModelAndView modelAndView = new ModelAndView();
//		modelAndView.setView(new RedirectView("pages/index"));
//		return modelAndView;
//	}
//	
//	@RequestMapping("toError")
//	public ModelAndView toError() {
//		logger.debug("toError");
//		ModelAndView modelAndView = new ModelAndView();
//		modelAndView.setView(new RedirectView("pages/error"));
//		return modelAndView;
//	}

	@RequestMapping("/index")
	public ModelAndView index() {
		logger.debug("index");
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setView(new JstlView("/WEB-INF/pages/index.jsp"));
		return modelAndView;
	}

//	@RequestMapping("error")
//	public ModelAndView error() {
//		logger.debug("error");
//		ModelAndView modelAndView = new ModelAndView();
//		modelAndView.setView(new JstlView("/WEB-INF/pages/404.jsp"));
//		return modelAndView;
//	}
}
