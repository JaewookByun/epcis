package test;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

/**
 * Copyright (C) 2014 KAIST RESL
 *
 * This file is part of Oliot (oliot.org).
 *
 * @author Jack Jaewook Byun, Ph.D student Korea Advanced Institute of Science
 *         and Technology Real-time Embedded System Laboratory(RESL)
 *         bjw0829@kaist.ac.kr
 */
@Controller
@RequestMapping("/hello")
public class HelloController {

	/**
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView helloGet() {
		ModelAndView mv = new ModelAndView();
		mv.setViewName("hello");
		mv.addObject("message", "Hello Spring MVC");
		return mv;
	}

	@RequestMapping(method = RequestMethod.POST)
	public ModelAndView helloPost(
			@RequestParam(value = "name", required = false) String name,
			@RequestParam(value = "age", defaultValue = "1") int age) {
		ModelAndView mv = new ModelAndView();
		mv.setViewName("hello");
		mv.addObject("message", "Hello Spring MVC");
		mv.addObject("name", name);
		mv.addObject("age", age);
		return mv;
	}
}
