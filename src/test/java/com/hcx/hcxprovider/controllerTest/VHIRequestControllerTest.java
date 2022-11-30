package com.hcx.hcxprovider.controllerTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hcx.hcxprovider.configuration.InvalidUserAuthEntryPoint;
import com.hcx.hcxprovider.configuration.SecurityFilter;
import com.hcx.hcxprovider.controllers.VHIRequestController;
import com.hcx.hcxprovider.model.ClaimRequest;
import com.hcx.hcxprovider.model.CoverageEligibilityRequest;
import com.hcx.hcxprovider.model.PreAuthRequest;
import com.hcx.hcxprovider.service.ClaimRequestService;
import com.hcx.hcxprovider.service.CoverageEligibilityService;
import com.hcx.hcxprovider.service.PreAuthService;
import com.hcx.hcxprovider.service.impl.UserServiceImpl;
import com.hcx.hcxprovider.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;

import static org.hamcrest.core.StringContains.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(VHIRequestController.class)
@AutoConfigureMockMvc(addFilters = false)
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
    private BCryptPasswordEncoder passwordEncoder;

    @MockBean
    private InvalidUserAuthEntryPoint authenticationEntryPoint;

    @MockBean
    private SecurityFilter securityFilter;


    @MockBean
    private UserServiceImpl userServiceImpl;

    String token=
            "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJKb3NoaSIsImlhdCI6MTY2OTYxMjA2NSwiZXhwIjoxNjY5Njk4NDY1fQ.USvudyQtxHNQlis0EnKaQaP8pkzyDbNaVN2_RxsEw1EyB2QRgmO8p9I6Dvslw0m_cHmzHIGJ1iPY7ZNm44oeaA";

    @BeforeEach
    public void setToken(){

        com.hcx.hcxprovider.model.User user= new com.hcx.hcxprovider.model.User();
        user.setPassword("Joshi@1999");
        user.setUserName("Joshi");
        UserDetails userDetails= new org.springframework.security.core.userdetails.User(user.getUserName(),user.getPassword(),new ArrayList<>());

        String username="Joshi";

        Mockito.when(userServiceImpl.loadUserByUsername(username)).thenReturn(userDetails);
        Mockito.when(jwtUtil.validateToken(token,userDetails)).thenReturn(true);

    }

    @Test
    public void savePreAuthRequestTest() throws Exception {

        PreAuthRequest preAuthRequest= new PreAuthRequest();
        preAuthRequest.setRequestType("preAuth");
        preAuthRequest.setInsurerCode("1-bdaf4872");

        ObjectMapper objectMapper= new ObjectMapper();
        String json=objectMapper.writeValueAsString(preAuthRequest);

         Mockito.when(preAuthService.savePreAuthRequest(preAuthRequest)).thenReturn("PreAuth request pushed to Queue");
         mvc.perform(post("/hcxProvider/request/savePreAuthReq").header("Authorization",token).contentType(MediaType.APPLICATION_JSON).content(json)).andExpect(status().isOk()).andExpect(MockMvcResultMatchers.content().string(containsString("PreAuth request pushed to Queue")));
    }

  //  @Test
    public void saveCoverageEligibilityRequestTest() throws Exception {

        CoverageEligibilityRequest coverageEligibilityRequest= new CoverageEligibilityRequest();
        coverageEligibilityRequest.setRequestType("CoverageRequest");
        coverageEligibilityRequest.setInsurerCode("1-bdaf4872");
        ObjectMapper objectMapper= new ObjectMapper();
        String json=objectMapper.writeValueAsString(coverageEligibilityRequest);

        Mockito.when(coverageEligibilityService.saveCoverageEligibilityRequest(coverageEligibilityRequest)).thenReturn("Coverage Eligibility request sent successfully");

        mvc.perform(post("/hcxProvider/request/saveCoverageEligibility").header("Authorization",token).contentType(MediaType.APPLICATION_JSON).content(json)).andExpect(status().isOk()).andExpect(MockMvcResultMatchers.content().string(containsString("Coverage Eligibility request sent successfully")));
    }

    @Test
    public void saveClaimRequestTest() throws Exception {

        ClaimRequest claimRequest= new ClaimRequest();
        claimRequest.setRequestType("CoverageRequest");
        claimRequest.setInsurerCode("1-bdaf4872");
        ObjectMapper objectMapper= new ObjectMapper();
        String json=objectMapper.writeValueAsString(claimRequest);

        Mockito.when(claimRequestService.saveClaimRequest(claimRequest)).thenReturn("Claim Request sent successfully");

        mvc.perform(post("/hcxProvider/request/saveClaimRequest").header("Authorization",token).contentType(MediaType.APPLICATION_JSON).content(json)).andExpect(status().isOk()).andExpect(MockMvcResultMatchers.content().string(containsString("Claim Request sent successfully")));
    }



}
