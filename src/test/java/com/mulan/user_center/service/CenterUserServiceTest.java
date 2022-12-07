package com.mulan.user_center.service;

import com.mulan.user_center.model.domain.CenterUser;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CenterUserServiceTest {
    @Autowired
    private CenterUserService centerUserService;

    @Test
    void testSearchUser(){
        CenterUser centerUser = centerUserService.getById(2);
        System.out.println(centerUser);
        assertEquals(2,centerUser.getId());
    }

}