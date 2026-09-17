package org.joonzis.controller;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.joonzis.domain.SubscriptionVO;
import org.joonzis.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class HomeController {
	@Autowired
	private UserService userService;

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String home(Locale locale, Model model) {
		// TODO: 로그인 구현 후 세션의 사용자 ID로 교체
		Long userId = 1L;
		SubscriptionVO activeSubscription = userService.getActiveSubscription(userId);
		model.addAttribute("activeSubscription", activeSubscription);
		return "index";
	}
}
