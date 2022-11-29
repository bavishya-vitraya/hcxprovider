package com.hcx.hcxprovider.controllerTest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hcx.hcxprovider.controllers.VHIRequestController;
import com.hcx.hcxprovider.error.ProviderException;
import com.hcx.hcxprovider.model.PreAuthRequest;
import com.hcx.hcxprovider.service.ClaimRequestService;
import com.hcx.hcxprovider.service.CoverageEligibilityService;
import com.hcx.hcxprovider.service.PreAuthService;
import com.hcx.hcxprovider.service.impl.UserServiceImpl;
import com.hcx.hcxprovider.util.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;

import java.util.ArrayList;
import java.util.Objects;
import java.util.stream.Stream;

import static org.hamcrest.core.StringContains.containsString;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class VHIRequestControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private  PreAuthService preAuthService;

    @MockBean
    private CoverageEligibilityService coverageEligibilityService;

    @MockBean
    private ClaimRequestService claimRequestService;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private UserServiceImpl userServiceImpl;

//    @ParameterizedTest
//    @MethodSource("getPreAuthRequestParams")
    public <T> void savePreAuthRequestTest(PreAuthRequest preAuthRequest, UserDetails user,T expected) throws Exception {
        String token=
                "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJKb3NoaSIsImlhdCI6MTY2OTYxMjA2NSwiZXhwIjoxNjY5Njk4NDY1fQ.USvudyQtxHNQlis0EnKaQaP8pkzyDbNaVN2_RxsEw1EyB2QRgmO8p9I6Dvslw0m_cHmzHIGJ1iPY7ZNm44oeaA";

         String username="Joshi";
        ObjectMapper objectMapper= new ObjectMapper();
        String json=objectMapper.writeValueAsString(preAuthRequest);

          Mockito.when(userServiceImpl.loadUserByUsername(username)).thenReturn(user);
          Mockito.when(jwtUtil.validateToken(token,user)).thenReturn(true);
          Mockito.when(preAuthService.savePreAuthRequest(preAuthRequest)).thenReturn("passed");
          mvc.perform(post("/hcxProvider/request/savePreAuthReq").header("Authorization",token).contentType(MediaType.APPLICATION_JSON).content(json)).andExpect(status().isOk()) .andExpect((ResultMatcher) containsString("passed"));
    }

    public static Stream<Arguments> getPreAuthRequestParams(){
        return  Stream.of(Arguments.of(getPreAuthRequest(),getUserDetails(), true));
    }

    public static PreAuthRequest getPreAuthRequest(){

        PreAuthRequest preAuthRequest= new PreAuthRequest();
        preAuthRequest.setRequestType("preAuth");
        preAuthRequest.setInsurerCode("1-bdaf4872");

        return preAuthRequest;

    }
    public static UserDetails getUserDetails(){
        com.hcx.hcxprovider.model.User user= new com.hcx.hcxprovider.model.User();
        user.setPassword("Joshi@1999");
        user.setUserName("Joshi");
        UserDetails userDetails= new org.springframework.security.core.userdetails.User(user.getUserName(),user.getPassword(),new ArrayList<>());
        return userDetails;
    }

}
