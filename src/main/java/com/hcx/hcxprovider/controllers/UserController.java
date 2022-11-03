package com.hcx.hcxprovider.controllers;

import com.hcx.hcxprovider.model.User;
import com.hcx.hcxprovider.repository.UserRepo;
import com.hcx.hcxprovider.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UsserController {

     @Autowired
     private UserRepo userRepo;

     @Autowired
     UserService userService;

    @PostMapping("/addUser")
    public ResponseEntity<String> addNewUser(@RequestBody User user){
        Integer id= userService.saveUser(user);
        return  ResponseEntity.ok("User Added");
    }
}
