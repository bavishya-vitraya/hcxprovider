package com.hcx.hcxprovider.repository;

import com.hcx.hcxprovider.model.PreAuthResponse;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PreAuthResponseRepo extends MongoRepository<PreAuthResponse,String> {

}
