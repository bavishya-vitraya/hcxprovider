package com.hcx.hcxprovider.restAssuredTests;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hcx.hcxprovider.dto.UserRequestDTO;
import com.hcx.hcxprovider.model.ClaimRequest;
import com.hcx.hcxprovider.model.CoverageEligibilityRequest;
import com.hcx.hcxprovider.model.PreAuthRequest;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.apache.commons.io.FileUtils;
import org.json.JSONException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class RestAssuredVhiReqTest {
    String token="";
    @BeforeEach
    public void setUp() throws JsonProcessingException {
        UserRequestDTO userRequestDTO= new UserRequestDTO();
        userRequestDTO.setUserName("Joshi");
        userRequestDTO.setPassword("Joshi1999");

        ObjectMapper objectMapper= new ObjectMapper();
        String json=objectMapper.writeValueAsString(userRequestDTO);
        RestAssured.baseURI = "http://localhost:9090";
        token=  given().header("Content-Type", "application/json").body(json).when().post("/user/login").jsonPath().get("token");


    }

   @Test
    public void TestSavePreAuth() throws IOException, JSONException {
        Map<String,String> headers= new HashMap<>();
        headers.put("Authorization",token);
        headers.put("Content-Type","application/json");


         File requestfile = new ClassPathResource("input/preAuthInput.txt").getFile();
         String json = FileUtils.readFileToString(requestfile);

        RequestSpecification request = given();
        request.headers(headers);
        request.body(json);
        Response response= request.post("/hcxProvider/request/savePreAuthReq");

        String result= response.getBody().asString();
        System.out.println(result);
        Assertions.assertEquals(result,"PreAuth request pushed to Queue");

    }

    @Test
    public void TestSaveCoverage() throws IOException, JSONException {
        Map<String,String> headers= new HashMap<>();
        headers.put("Authorization",token);
        headers.put("Content-Type","application/json");

       CoverageEligibilityRequest coverageEligibilityRequest= new CoverageEligibilityRequest();
        coverageEligibilityRequest.setRequestType("coverageRequest");
        coverageEligibilityRequest.setInsurerCode("1-434d79f6-aad8-48bc-b408-980a4dbd90e2");
        coverageEligibilityRequest.setSenderCode("1-bdaf4872");

        RequestSpecification request = given();

        ObjectMapper objectMapper= new ObjectMapper();
        String json=objectMapper.writeValueAsString(coverageEligibilityRequest);


        request.headers(headers);
        request.body(json);
        Response response= request.post("/hcxProvider/request/saveCoverageEligibility");

        String result= response.getBody().asString();
        System.out.println(result);
        Assertions.assertEquals(result,"Coverage Eligibility request sent successfully");

    }

    @Test
    public void TestClaim() throws IOException, JSONException {
        Map<String,String> headers= new HashMap<>();
        headers.put("Authorization",token);
        headers.put("Content-Type","application/json");

        ClaimRequest claimRequest= new ClaimRequest();
        claimRequest.setRequestType("claimRequest");
        claimRequest.setInsurerCode("1-434d79f6-aad8-48bc-b408-980a4dbd90e2");
        claimRequest.setSenderCode("1-bdaf4872");

        RequestSpecification request = given();

        ObjectMapper objectMapper= new ObjectMapper();
        String json=objectMapper.writeValueAsString(claimRequest);


        request.headers(headers);
        request.body(json);
        Response response= request.post("/hcxProvider/request/saveClaimRequest");

        String result= response.getBody().asString();
        System.out.println(result);
        Assertions.assertEquals(result,"Claim Request sent successfully");

    }



}
