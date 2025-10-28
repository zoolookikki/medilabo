package com.medilabo.gateway_service.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.medilabo.gateway_service.config.SpringSecurityConfiguration;

@WebMvcTest(GatewayTestController.class)
//comme c'est un test unitaire, il faut importer explicitement la configuration de sécurité pour pouvoir la tester.
@Import(SpringSecurityConfiguration.class)
class GatewaySecurityTest {

    // @Autowired pour Junit5, c'est plus simple.
    @Autowired MockMvc mvc;

    @Value("${security.api.username}") private String username;
    @Value("${security.api.password}") private String password; 

    private RequestPostProcessor basicAuthentication() {
        return httpBasic(username, password);
    }
    
    private RequestPostProcessor badBasicAuthentication() {
        return httpBasic("hs", "hs");
    }

    @Test
    void badPath() throws Exception {
        mvc.perform(get("/").with(basicAuthentication()))
        .andExpect(status().isForbidden());   // 403
        mvc.perform(get("/"))            
        .andExpect(status().isUnauthorized()); // 401
    }    

    @Test
    void unauthorizedWhenNoAuth() throws Exception {
        mvc.perform(get("/api/simulate500").with(badBasicAuthentication()))
        .andExpect(status().isUnauthorized());
    }
}
