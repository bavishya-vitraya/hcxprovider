package com.hcx.hcxprovider.repository;

import com.hcx.hcxprovider.model.PreAuthRequest;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PreAuthRequestRepo extends MongoRepository<PreAuthRequest,String> {
}
