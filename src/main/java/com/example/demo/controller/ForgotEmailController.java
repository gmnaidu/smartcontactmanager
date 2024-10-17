package com.example.demo.controller;

import java.util.Random;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ForgotEmailController {
    

    Random random= new Random(1000);
    // open email id form

    @RequestMapping("/forgot")
    public String openEmailForm(){
        return "forgot_email_form";
    }

    @PostMapping("/send-otp")
    public String sendOtp(@RequestParam("email") String email){

        System.out.println("----: "+ email);

        // generate Random Otp

       

        int otp= random.nextInt(999999);
        System.out.println("otp: "+ otp);
        return "verify_otp";
    }


   // send-otp
}
