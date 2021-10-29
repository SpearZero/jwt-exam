package com.example.jwt.repository;

import com.example.jwt.entity.ERole;
import com.example.jwt.entity.Role;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@DataJpaTest
public class RoleRepositoryTest {

    private final RoleRepository roleRepository;

    @Autowired
    public RoleRepositoryTest(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Test
    void 역할_저장() {
        Role role = new Role(ERole.ROLE_ADMIN);

        Role savedRole = roleRepository.save(role);

        Assertions.assertTrue(savedRole.getId() > 0);
    }
}
