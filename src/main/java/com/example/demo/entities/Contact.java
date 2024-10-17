package com.example.demo.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "CONTACT")
public class Contact {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int cid;
	private String name;
	private String nickname;
	private String work;
	private String email;
	private String image;
	@Column(length = 1000)
	private String description;
	private String phone;
	
	@ManyToOne
	private User user;
	
	public Contact(int cid, String name, String nickname, String work, String email,String image,
			String description, String phone) {
		super();
		this.cid = cid;
		this.name = name;
		this.nickname = nickname;
		this.work = work;
		this.email = email;
		this.image=image;
		this.description = description;
		this.phone = phone;
	}
	
	public Contact() {
		
	}

	public int getCid() {
		return cid;
	}

	public void setCid(int cid) {
		this.cid = cid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getWork() {
		return work;
	}

	public void setWork(String work) {
		this.work = work;
	}


	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
	
//	@Override
//	public String toString() {
//		return "Contact [cid=" + cid + ", name=" + name + ", nickname=" + nickname + ", work=" + work + ", email="
//				+ email + ", profileImage=" + profileImage + ", description=" + description + ", phone=" + phone
//				+ ", user=" + user + "]";
//	}
	
	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		return this.cid == ((Contact)obj).getCid();
	}

	
	
	
	
	
	
}
