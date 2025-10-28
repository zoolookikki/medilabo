package com.medilabo.webapp.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.medilabo.webapp.config.SpringSecurityConfiguration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@WebMvcTest(WebAppSecurityTest.class)
//comme c'est un test unitaire, il faut importer explicitement la configuration de sécurité pour pouvoir la tester.
@Import(SpringSecurityConfiguration.class)
public class WebAppSecurityTest {

    // @Autowired pour Junit5, c'est plus simple.
    @Autowired MockMvc mvc;
    
    @Test
    void pathForUnauthorizedAccessRedirectToLoginTest() throws Exception {
      mvc.perform(get("/patients"))
         .andExpect(redirectedUrlPattern("**/login"));
    }
    
    @Test
    @WithMockUser
    void postWithoutCsrfIsForbidden() throws Exception {
        mvc.perform(post("/patients")).andExpect(status().isForbidden()); // 403
    }

    @Test
    void pathForLoginSuccessTest() throws Exception {
        // when then
        mvc.perform(get("/login")).andExpect(status().isOk());
    }
}
