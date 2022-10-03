package com.dorohedoro;

import com.dorohedoro.service.IRoleService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@SpringBootTest
@ExtendWith(SpringExtension.class)
@WithMockUser(username = "rabbit", roles = "MANAGER")
public class MockMvcTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private IRoleService roleService;

    private MockMvc mockMvc;
    
    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }
    
    @Test
    public void getApiGreeting() throws Exception {
        mockMvc.perform(get("/api/greeting/{username}", "rabbit"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void getApiUserByEmail() throws Exception {
        mockMvc.perform(get("/api/user/{email}", "dorohedoro@163.com"))
                .andDo(print())
                .andExpect(status().isOk());
    }
    
    @Test
    public void getRoleHierarchyExpr() {
        log.info("角色权限包含关系: {}", roleService.getRoleHierarchyExpr());
    }
}
