package com.hbjycl.online;

import com.hbjycl.online.shiro.MyRealm;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.spring.web.config.DefaultShiroFilterChainDefinition;
import org.apache.shiro.spring.web.config.ShiroFilterChainDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Configuration
@ControllerAdvice
@SpringBootApplication
public class OnlineApplication {

	public static void main(String[] args) {
		SpringApplication.run(OnlineApplication.class, args);
	}

	@Bean
	public Realm realm() {
		return new MyRealm();
	}

	@Bean
	public ShiroFilterChainDefinition shiroFilterChainDefinition() {
		DefaultShiroFilterChainDefinition chainDefinition = new DefaultShiroFilterChainDefinition();

		// logged in users with the 'admin' role
		chainDefinition.addPathDefinition("/admin/**", "authc, roles[admin]");

		// logged in users with the 'document:read' permission
		chainDefinition.addPathDefinition("/docs/**", "authc, perms[document:read]");

		// all other paths require a logged in user
		chainDefinition.addPathDefinition("/**", "authc");

		chainDefinition.addPathDefinition("/login.html", "authc"); // need to accept POSTs from the login form
		chainDefinition.addPathDefinition("/logout", "logout");
		return chainDefinition;
	}

	@ExceptionHandler(AuthorizationException.class)
	@ResponseStatus(HttpStatus.FORBIDDEN)
	public String handleException(AuthorizationException e, Model model) {

		// you could return a 404 here instead (this is how github handles 403, so the user does NOT know there is a
		// resource at that location)

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("status", HttpStatus.FORBIDDEN.value());
		map.put("message", "No message available");
		model.addAttribute("errors", map);

		return "error";
	}

}
