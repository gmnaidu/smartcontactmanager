package com.example.demo.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.expression.Numbers;

import com.example.demo.dao.ContactRepository;
import com.example.demo.dao.UserRepository;
import com.example.demo.entities.Contact;
import com.example.demo.entities.User;
import com.example.demo.helper.Message;

import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;

@Controller
@RequestMapping("/user")
public class UserController {

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private ContactRepository contactRepository;
    
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@ModelAttribute
	public void commonData(Model model, Principal principle) {

		String username = principle.getName();

		User user = userRepository.getUserByUsername(username);
		System.out.println("user: "+user);

		System.out.println("username: "+username);

		model.addAttribute("user", user);

	}

	@RequestMapping(path = "/index", method = RequestMethod.GET)
	public String userdashboard(Model model, Principal principle) {

		model.addAttribute("title", "User Dashboard");
		return "normal/user_dashboard";
	}
	
	
	@RequestMapping("/add_contact")
	public String addContact(Model model) {
		model.addAttribute("title", "Add Contact");
		model.addAttribute("contact",new Contact());
		return "normal/add_contact_form";
	}
	
	@RequestMapping(value="/process-contact",method=RequestMethod.POST )
	public String collectContactData(@ModelAttribute("contact") Contact contact, @RequestParam("profileImage") MultipartFile file,Principal principle, HttpSession session ) {
		
		try {
			String username = principle.getName();		
			User user = this.userRepository.getUserByUsername(username);
			contact.setUser(user);
			
			
			if(file.isEmpty()) {
				
				System.out.println("Files is empty");
				contact.setImage("contact.png");
			}else {
				
				contact.setImage(file.getOriginalFilename());
				File savefile = new ClassPathResource("static/image").getFile();
				Path path = Paths.get(savefile.getAbsolutePath()+File.separator+file.getOriginalFilename());
				Files.copy(file.getInputStream(),path, StandardCopyOption.REPLACE_EXISTING);
				System.out.println("File uploaded successfully");
			}
			
			user.getContacts().add(contact);
			this.userRepository.save(user);
			
			// success message
			
			session.setAttribute("message", new Message("Contact added successfully.. add More", "success"));
			
		} catch (Exception e) {
			System.out.println("Error"+e.getMessage());
			e.printStackTrace();
			
			// error message
			session.setAttribute("message", new Message("some thing error.. try Again...", "danger"));
		}
		
		return "normal/add_contact_form";
		
	}
	// per page -5 contacts[n]
	// currant page - page[page]
	@GetMapping("/showcontacts/{page}")
	public String showConatcts(@PathVariable("page") Integer page, Model model, Principal principle) {
		model.addAttribute("title", "Show Contacts");
		
		
		String username = principle.getName();
		
		User user = this.userRepository.getUserByUsername(username);
		
		
		// current page -page
		// contacts per page -5
		Pageable pagable = PageRequest.of(page, 3);
		
		
		Page<Contact> contacts = contactRepository.getListOfContactsOfUser(user.getId(),pagable);
		
		model.addAttribute("contacts", contacts);
		model.addAttribute("currentpage",page);
		model.addAttribute("totalpages", contacts.getTotalPages());
		
		System.out.println(contacts.getTotalPages());
		
		
		System.out.println("-------------------contacts"+contacts);
		
		
		
		
		return "normal/show_contacts";
	}
	
	
	// showing particular data
	
	@GetMapping("/{cid}/contact")
	public String showContactDetail(@PathVariable("cid") int cid,Model model) {
		
		
		Optional<Contact> byId = this.contactRepository.findById(cid);
		
		Contact contact = byId.get();
		
		model.addAttribute("contact", contact);
		
		System.out.println("Contact Object: "+ contact.getNickname());
		
		
		return "normal/contact_detail";
		
	}
	
	@GetMapping("/contact/{cid}")
	@Transactional
	public String deleteContact(@PathVariable("cid") Integer cid, Principal principle, HttpSession session) {
		
		String username = principle.getName();
		
		User user = this.userRepository.getUserByUsername(username);
		
		
		Optional<Contact> contactoptional = this.contactRepository.findById(cid);
		Contact contact = contactoptional.get();
		
		
		
		if(user.getId() == contact.getUser().getId()) {
			
			//contact.setUser(null);
			
			// delete image
			String imagepath = contact.getImage();
			
			 
			try {
				File savefile = new ClassPathResource("static/image").getFile();
				Path path = Paths.get(savefile.getAbsolutePath()+File.separator+imagepath);
				Files.delete(path);
				System.out.println("Image sucessfully deleted");
				
			} catch (IOException e) {
				
				e.printStackTrace();
			}
			
			
			
			
			
			//this.contactRepository.delete(contact);
			user.getContacts().remove(contact);
			
			userRepository.save(user);
			
		}
		
		session.setAttribute("message", new Message("Contact deleted successfully!!...", "success"));
		
		return "redirect:/user/showcontacts/0";
		
	}
	
	
	// open update form contact
	
	@PostMapping("/update-contact/{cid}")
	public String updateContact(@PathVariable("cid") Integer cid , Model m) {
		
		System.out.println("it is update form");
	    m.addAttribute("title", "updateform");
	    Contact contact = contactRepository.findById(cid).get();
	    m.addAttribute("contact", contact);
	    
		return "normal/updateform";
	}
	
	// save update contact in database
	
	@RequestMapping(value = "/process-update", method = RequestMethod.POST)
	public String updateSaveContact(@ModelAttribute Contact contact, @RequestParam("profileImage") MultipartFile file,Principal principle, HttpSession session ) {
	
		try {
			
			System.out.println("contact Name: "+contact.getName());
			System.out.println("Contact Id "+ contact.getCid());
			
			// old contact data
			Contact oldcontact = contactRepository.findById(contact.getCid()).get();
			
			if(!file.isEmpty()) {
				
				// rewrite file
				
				// delete old photo
				
				File deletefile = new ClassPathResource("static/image").getFile();
				File file1 = new File(deletefile, oldcontact.getImage());
				file1.delete();
				
				// update new photo
					contact.setImage(file.getOriginalFilename());
					File savefile = new ClassPathResource("static/image").getFile();
					Path path = Paths.get(savefile.getAbsolutePath()+File.separator+file.getOriginalFilename());
					Files.copy(file.getInputStream(),path, StandardCopyOption.REPLACE_EXISTING);
				
			}else {
				contact.setImage(oldcontact.getImage());
			}
			
			User user =this.userRepository.getUserByUsername(principle.getName());
			contact.setUser(user);
			contactRepository.save(contact);
			
			session.setAttribute("message", new Message("Your contact is updated... ", "success"));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		return "redirect:/user/"+contact.getCid()+"/contact";
		
	}
	
	
	// your profile
	@GetMapping("/profile")
	public String yourProfile(Model model) {
		
		model.addAttribute("title", "profile");
		return "normal/profile";
	}

	// open settings 
	@GetMapping("/settings")
    public String openSettings(){
		return "normal/settings";
	}


	@PostMapping("/change-password")
	public String changePassword(@RequestParam("oldpassword") String oldpassword, @RequestParam("newpassword") String newpassword, Principal principal,HttpSession session){

		System.out.println("oldpassword: "+oldpassword);
		System.out.println("newpassword: "+newpassword);

		String username = principal.getName();
		User user = this.userRepository.getUserByUsername(username);

		System.out.println("userpassword: "+user.getPassword());

		if(bCryptPasswordEncoder.matches(oldpassword, user.getPassword())){
			// old password
			user.setPassword(bCryptPasswordEncoder.encode(newpassword));
            userRepository.save(user);

			session.setAttribute("message", new Message("password changed successfully...", "success"));
		}else{
			session.setAttribute("message", new Message("Old password is incorrect...", "danger"));
			return "redirect:/user/settings";
		}

       return "redirect:/user/index";
	}

	
	
	
	
	
	
	
	
	
	
	

}
