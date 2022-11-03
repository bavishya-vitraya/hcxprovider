package com.hcx.hcxprovider.controllers;

import com.hcx.hcxprovider.dto.ResponseDTO;
import com.hcx.hcxprovider.dto.UserRequestDTO;
import com.hcx.hcxprovider.dto.UserResponseDTO;
import com.hcx.hcxprovider.model.User;
import com.hcx.hcxprovider.repository.UserRepo;
import com.hcx.hcxprovider.service.UserService;
import com.hcx.hcxprovider.service.impl.UserServiceImpl;
import com.hcx.hcxprovider.util.JwtUtil;
import com.sun.net.httpserver.Authenticator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

     @Autowired
     private UserRepo userRepo;

     @Autowired
     private JwtUtil jwtUtil;

     @Autowired
     private UserService userService;

    @Autowired
     private UserServiceImpl userServiceImpl;

     @Autowired
     private AuthenticationManager authenticationManager;

    @PostMapping("/addUser")
    public ResponseEntity<String> addNewUser(@RequestBody User user){
        String id= userService.saveUser(user);
        return  ResponseEntity.ok("User Added"+ id);
    }

    @PostMapping("/login")
    public ResponseEntity<UserResponseDTO> login(@RequestBody UserRequestDTO userRequestDTO){

        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userRequestDTO.getUserName(), userRequestDTO.getPassword()));
        }
        catch(BadCredentialsException e){
           log.error(String.valueOf(e));

        }
        UserDetails userDetails= userServiceImpl.loadUserByUsername(userRequestDTO.getUserName());
        UserResponseDTO userResponseDTO= new UserResponseDTO();
        String token=jwtUtil.generateToken(userDetails);
        userResponseDTO.setToken(token);
        userResponseDTO.setMessage("Success");

       return ResponseEntity.ok(userResponseDTO);
    }
}
