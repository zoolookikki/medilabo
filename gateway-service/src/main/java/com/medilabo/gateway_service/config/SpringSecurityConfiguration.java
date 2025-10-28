package com.medilabo.gateway_service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

// https://www.sfeir.dev/back/securisez-vos-api-avec-spring-security-basic-auth/
@Configuration
public class SpringSecurityConfiguration {
    /*
    @Bean est automatiquement détecté et exécuté par Spring lors du démarrage de l’application ==>
    Dans le démarrage de Spring Security, Spring cherche une configuration personnalisée.
    Il regarde dans le contexte Spring s’il y a un @Bean de type SecurityFilterChain.
    S’il en trouve un, il l’applique.
    S’il n’en trouve pas, il applique une configuration de sécurité par défaut (tout est protégé).
   */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
            // Le mécanisme CSRF est désactivé sinon POST/PUT/PATCH/DELETE ne fonctionnent pas (car Spring exisge un jeton CSRF). Ce mécanisme est inutile pour les API REST.
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> {
              auth.requestMatchers("/api/**").authenticated();
                auth.anyRequest().denyAll();
            })
            // active l’authentification HTTP Basic avec la config par défaut.
          .httpBasic(Customizer.withDefaults()) 
            .build();
    }   
    
    
    @Bean
    public UserDetailsService userDetailsService(
            PasswordEncoder encoder,
            @Value("${security.api.username}") String username, 
            @Value("${security.api.password}") String password) {
        var user = User.withUsername(username)
                .password(encoder.encode(password))
                .build();

        // les comptes sont conservés dans une map en mémoire.
        return new InMemoryUserDetailsManager(user);
    }

    /**
     * Provides a password encoder based on the BCrypt algorithm.
     *
     * <p>
     * BCrypt is a strong hashing algorithm, recommended for secure password storage.
     * </p>
     *
     * @return an instance of BCryptPasswordEncoder
     */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
