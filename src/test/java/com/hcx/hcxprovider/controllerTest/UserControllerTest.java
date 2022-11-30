package com.hcx.hcxprovider.controllerTest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hcx.hcxprovider.configuration.InvalidUserAuthEntryPoint;
import com.hcx.hcxprovider.configuration.SecurityFilter;
import com.hcx.hcxprovider.controllers.UserController;
import com.hcx.hcxprovider.dto.UserRequestDTO;
import com.hcx.hcxprovider.dto.UserResponseDTO;
import com.hcx.hcxprovider.model.User;
import com.hcx.hcxprovider.repository.UserRepo;
import com.hcx.hcxprovider.service.impl.UserServiceImpl;
import com.hcx.hcxprovider.util.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.Collection;

import static org.hamcrest.core.StringContains.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
public class UserControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private UserRepo userRepo;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private UserServiceImpl userServiceImpl;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private SecurityFilter securityFilter;

    @MockBean
    private BCryptPasswordEncoder passwordEncoder;


    @MockBean
    private InvalidUserAuthEntryPoint authenticationEntryPoint;

    @MockBean
    Authentication authentication;


    @Test
    public void addUserTest() throws Exception {
        User user= new User();
        user.setId("890");
        ObjectMapper objectMapper= new ObjectMapper();
        String json=objectMapper.writeValueAsString(user);
        Mockito.when(userServiceImpl.saveUser(user)).thenReturn("890") ;
        mvc.perform(post("/user/addUser").contentType(MediaType.APPLICATION_JSON).content(json)).andExpect(status().isOk()).andExpect(MockMvcResultMatchers.content().string(containsString("890")));

    }

    @Test
    public void loginTest() throws Exception {
        UserRequestDTO userRequestDTO= new UserRequestDTO();
        userRequestDTO.setUserName("nhms");
        userRequestDTO.setPassword("bsskn");

        com.hcx.hcxprovider.model.User user= new com.hcx.hcxprovider.model.User();
        user.setPassword("Joshi@1999");
        user.setUserName("Joshi");
        UserDetails userDetails= new org.springframework.security.core.userdetails.User(user.getUserName(),user.getPassword(),new ArrayList<>());

        String token=
                "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJKb3NoaSIsImlhdCI6MTY2OTYxMjA2NSwiZXhwIjoxNjY5Njk4NDY1fQ.USvudyQtxHNQlis0EnKaQaP8pkzyDbNaVN2_RxsEw1EyB2QRgmO8p9I6Dvslw0m_cHmzHIGJ1iPY7ZNm44oeaA";

        ObjectMapper objectMapper= new ObjectMapper();
        String json=objectMapper.writeValueAsString(userRequestDTO);


        Mockito.when(authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userRequestDTO.getUserName(), userRequestDTO.getPassword()))).thenReturn(authentication);
      Mockito.when( userServiceImpl.loadUserByUsername(userRequestDTO.getUserName())).thenReturn(userDetails);
      Mockito.when(jwtUtil.generateToken(userDetails)).thenReturn(token);

        mvc.perform(post("/user/login").contentType(MediaType.APPLICATION_JSON).content(json)).andExpect(status().isOk());
    }


}
