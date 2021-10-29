package com.example.jwt.controller;

import com.example.jwt.entity.ERole;
import com.example.jwt.entity.Role;
import com.example.jwt.entity.User;
import com.example.jwt.payload.request.SignupRequest;
import com.example.jwt.payload.response.MessageResponse;
import com.example.jwt.repository.RoleRepository;
import com.example.jwt.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AuthControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private TestRestTemplate restTemplate;

    @BeforeEach
    public void setUp() {
        User user = new User("test", "test@test.com", "123456", null);
        userRepository.save(user);
    }

    @AfterEach
    public void clear() {
        userRepository.deleteAll();
    }

    @Test
    public void 중복된_유저네임_거절() {
        SignupRequest signupRequest = new SignupRequest("test", "test@test.com", "123456", null);
        String url = "http://localhost:" + port + "/api/auth/signup";

        ResponseEntity<MessageResponse> responseEntity = restTemplate.postForEntity(url, signupRequest, MessageResponse.class);

        String msg = responseEntity.getBody().getMessage();
        assertThat(msg).isEqualTo("Error : Username is already taken!");
    }

    @Test
    public void 중복된_이메일_거절() {
        SignupRequest signupRequest = new SignupRequest("notdup", "test@test.com", "123456", null);
        String url = "http://localhost:" + port + "/api/auth/signup";

        ResponseEntity<MessageResponse> responseEntity = restTemplate.postForEntity(url, signupRequest, MessageResponse.class);

        String msg = responseEntity.getBody().getMessage();
        assertThat(msg).isEqualTo("Error: Email is already in use!");
    }

    @Test
    public void 회원가입_ROLE_미존재_가입성공() {
        SignupRequest signupRequest = new SignupRequest("notdup", "test123@test.com", "123456", null);
        String url = "http://localhost:" + port + "/api/auth/signup";

        ResponseEntity<MessageResponse> responseEntity = restTemplate.postForEntity(url, signupRequest, MessageResponse.class);

        String msg = responseEntity.getBody().getMessage();
        assertThat(msg).isEqualTo("User registered successfully!");
    }

    @Test
    public void 회원가입_ROLE_존재_가입성공() {
        Set<String> roles = new HashSet<>();
        roles.add("admin");

        SignupRequest signupRequest = new SignupRequest("notdup", "test123@test.com", "123456", roles);
        String url = "http://localhost:" + port + "/api/auth/signup";

        ResponseEntity<MessageResponse> responseEntity = restTemplate.postForEntity(url, signupRequest, MessageResponse.class);

        String msg = responseEntity.getBody().getMessage();
        assertThat(msg).isEqualTo("User registered successfully!");
    }
}
