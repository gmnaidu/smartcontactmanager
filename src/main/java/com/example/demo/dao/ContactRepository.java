package com.example.demo.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.entities.Contact;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Integer> {

	// current page -page
	// contacts per page -5
    @Query("from Contact as c where c.user.id=:userid")	
	public Page<Contact> getListOfContactsOfUser(@Param("userid") int userid,Pageable pageable);
}
