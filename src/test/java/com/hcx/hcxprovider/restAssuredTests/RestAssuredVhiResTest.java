package com.hcx.hcxprovider.restAssuredTests;


import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.IOException;

import static io.restassured.RestAssured.given;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class RestAssuredVhiResTest {

   @Test
    public void TestsavePreAuthRespons() throws IOException {
        RestAssured.baseURI = "http://localhost:9090";
        File file = new ClassPathResource("input/responsePayload").getFile();
        String preAuth = FileUtils.readFileToString(file);
        Response response= given().header("Content-Type", "application/json").body(preAuth).when().post("/hcxProvider/response/preauth/on_submit");
        String result= response.getBody().asString();
        Assertions.assertEquals(result,"PreAuth response pushed to Queue");
    }
}
