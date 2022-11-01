package com.hcx.hcxprovider.controllers;

import com.hcx.hcxprovider.model.User;
import com.hcx.hcxprovider.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UsserController {

     @Autowired
     private UserRepo userRepo;

    @PostMapping("/addUser")
    public User addNewUser(@RequestBody User user){
        userRepo.save(user);
        return user;
    }
}
