package com.example.jwt.controller;

import com.example.jwt.entity.ERole;
import com.example.jwt.entity.Role;
import com.example.jwt.entity.User;
import com.example.jwt.payload.request.LoginRequest;
import com.example.jwt.payload.request.SignupRequest;
import com.example.jwt.payload.response.JwtResponse;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

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
    private PasswordEncoder encoder;

    @Autowired
    private TestRestTemplate restTemplate;

    @BeforeEach
    public void setUp() {
        Role roleAdmin = new Role(ERole.ROLE_ADMIN);
        Role roleMod = new Role(ERole.ROLE_MODERATOR);
        Role roleUser = new Role(ERole.ROLE_USER);

        roleRepository.save(roleAdmin);
        roleRepository.save(roleMod);
        roleRepository.save(roleUser);

        Set<Role> roles = new HashSet<>();
        roles.add(roleUser);

        User user = new User("test", "test@test.com", encoder.encode("123456"), roles);
        userRepository.save(user);
    }

    @AfterEach
    public void clear() {
        userRepository.deleteAll();
        roleRepository.deleteAll();
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

    @Test
    public void 로그인시_토큰_발급() {
        LoginRequest request = new LoginRequest("test", "123456");
        String url = "http://localhost:" + port + "/api/auth/signin";

        ResponseEntity<JwtResponse> responseEntity = restTemplate.postForEntity(url, request, JwtResponse.class);

        String username = responseEntity.getBody().getUsername();
        String email = responseEntity.getBody().getEmail();
        String role = responseEntity.getBody().getRoles().get(0);
        String token = responseEntity.getBody().getToken();

        assertThat(username).isEqualTo("test");
        assertThat(email).isEqualTo("test@test.com");
        assertThat(role).isEqualTo("ROLE_USER");
        assertThat(token).isNotNull();
    }
}
