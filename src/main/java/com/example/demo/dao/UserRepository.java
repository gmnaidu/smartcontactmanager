package com.example.demo.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.entities.User;


public interface UserRepository extends JpaRepository<User, Integer>{

	 @Query("select u from User u where u.email= :email")
	 public User getUserByUsername(String email);
		 
	 
}
