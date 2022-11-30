package com.hcx.hcxprovider.service.impl;

import com.hcx.hcxprovider.model.User;
import com.hcx.hcxprovider.repository.UserRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Slf4j
@Service
public class UserServiceImpl implements  UserDetailsService {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;


    public String saveUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        try {
            userRepo.save(user);
        }
        catch(Exception e){
            log.error("user details could not be saved", e);
        }
        return user.getId();
    }



    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user=userRepo.findByUserName(username);

        if(user==null){
            throw new UsernameNotFoundException("User does not exist");
        }

        return new org.springframework.security.core.userdetails.User(user.getUserName(),user.getPassword(),new ArrayList<>());
    }
}
