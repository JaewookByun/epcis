package test;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/hello")
public class HelloController {

	@RequestMapping(method=RequestMethod.GET)
	public ModelAndView helloGet()
	{
		ModelAndView mv = new ModelAndView();
		mv.setViewName("hello");
		mv.addObject("message", "Hello Spring MVC");
		return mv;
	}
	@RequestMapping(method=RequestMethod.POST)
	public ModelAndView helloPost(@RequestParam(value="name", required=false) String name, @RequestParam(value="age", defaultValue="1") int age)
	{
		ModelAndView mv = new ModelAndView();
		mv.setViewName("hello");
		mv.addObject("message", "Hello Spring MVC");
		mv.addObject("name", name);
		mv.addObject("age", age);
		return mv;
	}
}
