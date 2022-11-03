package com.hcx.hcxprovider.repository;

import com.hcx.hcxprovider.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepo extends MongoRepository<User,Integer> {

    User findByUserName (String userName);
}
