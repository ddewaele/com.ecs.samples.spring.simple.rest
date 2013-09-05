package com.ecs.samples.spring.simple.rest.controller.web;

import javax.annotation.Resource;

import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.security.authentication.encoding.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ecs.samples.spring.simple.rest.model.User;
import com.ecs.samples.spring.simple.rest.repository.UserRepository;

@Controller
@RequestMapping(value="/user")
public class UserViewController {

	@Resource
	private UserRepository userRepository;
	
	@RequestMapping(value="/list", method=RequestMethod.GET)
	public ModelAndView userListPage() {
		ModelAndView mav = new ModelAndView("user-list");
		Iterable<User> userList = userRepository.findAll();
		mav.addObject("userList", userList);
		return mav;
	}
	
	@RequestMapping(value="/create", method=RequestMethod.GET)
	public ModelAndView newUserPage() {
		ModelAndView mav = new ModelAndView("user-new", "user", new User());
		return mav;
	}
	
	@RequestMapping(value="/create", method=RequestMethod.POST)
	public ModelAndView createNewUser(@ModelAttribute User user,
			BindingResult result,
			final RedirectAttributes redirectAttributes) {
		
		if (result.hasErrors())
			return new ModelAndView("user-new");
		
		ModelAndView mav = new ModelAndView();
		String message = "New user "+user.getEmail()+" was successfully created.";

		PasswordEncoder encoder = new Md5PasswordEncoder();
		String encodedPassword = encoder.encodePassword(user.getPassword(), user.getEmail());
		user.setPassword(encodedPassword);
		user.setRegistered(true);
		userRepository.save(user);
		mav.setViewName("redirect:/user/list");
				
		redirectAttributes.addFlashAttribute("message", message);	
		return mav;		
	}
	
	@RequestMapping(value="/edit/{id}", method=RequestMethod.POST)
	public ModelAndView editUser(@ModelAttribute User user,
			BindingResult result,
			@PathVariable Integer id,
			final RedirectAttributes redirectAttributes) {
		
		if (result.hasErrors())
			return new ModelAndView("user-edit");
		
		ModelAndView mav = new ModelAndView("redirect:/user/list");
		String message = "User was successfully updated.";

		userRepository.save(user);
		
		redirectAttributes.addFlashAttribute("message", message);	
		return mav;
	}
	
	@RequestMapping(value="/delete/{id}", method=RequestMethod.GET)
	public ModelAndView deleteUser(@PathVariable Integer id,
			final RedirectAttributes redirectAttributes)  {
		
		ModelAndView mav = new ModelAndView("redirect:/user/list");		
		
		User user = userRepository.findOne(id);
		userRepository.delete(id);
		String message = "The user "+user.getEmail()+" was successfully deleted.";
		
		redirectAttributes.addFlashAttribute("message", message);
		return mav;
	}	
}
