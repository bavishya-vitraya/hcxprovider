package com.hcx.hcxprovider.repository;


import com.hcx.hcxprovider.model.CoverageEligibilityRequest;
import org.springframework.data.mongodb.repository.MongoRepository;



public interface CoverageEligibilityRequestRepo extends MongoRepository<CoverageEligibilityRequest,String> {
}
