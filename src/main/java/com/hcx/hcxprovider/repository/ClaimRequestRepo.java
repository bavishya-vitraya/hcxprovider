package com.hcx.hcxprovider.repository;

import com.hcx.hcxprovider.model.ClaimRequest;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ClaimRequestRepo extends MongoRepository<ClaimRequest,String> {
}
