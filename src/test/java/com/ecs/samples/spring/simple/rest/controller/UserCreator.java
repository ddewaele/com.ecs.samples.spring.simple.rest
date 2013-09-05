package com.ecs.samples.spring.simple.rest.controller;

import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.security.authentication.encoding.PasswordEncoder;

import com.ecs.samples.spring.simple.rest.model.User;

public class UserCreator {

	private static final int NR_OF_USERS_TO_CREATE = 100;

	public static void main(String[] args) {

		UserCreator userCreator = new UserCreator();
		
		
		for (int i=1 ; i<= NR_OF_USERS_TO_CREATE ; i++) {
			User user = userCreator.createIndexUser(i);
			System.out.println(userCreator.getSqlString(user));
		}
		
	}
	
	private String createIndexedUserName(int i) {
		return "user" + i + "@gmail.com"; 
	}
	
	private String createIndexedPassword(int i) {
		return "admin" + i; 
	}

	private User createIndexUser(int i) {
		return createUser(i,createIndexedUserName(i), createIndexedPassword(i));
		
	}
	
	private User createUser(int id,String email,String pwd) {
		User user = new User(email);
		user.setId(id);
		PasswordEncoder encoder = new Md5PasswordEncoder();
		String encodedPassword = encoder.encodePassword(pwd, user.getEmail());
		user.setPassword(encodedPassword);
		return user;

	}
	
	private String getSqlString(User user) {
		int id = user.getId();
		return "/* password = " + createIndexedPassword(id) + " */ \nINSERT INTO USERS (id,authority,email,password,registered) VALUES (" + id + ",'ROLE_USER','" + user.getEmail() + "','" + user.getPassword() + "',true);";
	}
}
