package com.medilabo.risk_service.config;

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
    /**
     * Configures the main HTTP security filter chain.
     *
     * <p>
     * Configuration details:
     * <ul>
     *   <li><b>CSRF disabled</b> otherwise, POST/PUT/PATCH/DELETE requests won't work (because Spring requires a CSRF token). This mechanism is unnecessary for REST APIs.</li>
     *   <li><b>Authorization rules</b>:
     *     <ul>
     *       <li>requests to <b>/risk/**</b> require authentication,</li>
     *       <li>all other routes are explicitly denied.</li>
     *     </ul>
     *   </li>
     *   <li><b>HTTP Basic authentication enabled</b> with the default configuration.</li>
     * </ul>
     * </p>
     *
     * @param http the HttpSecurity object provided by Spring Security
     * @return the configured security filter chain
     * @throws Exception if the configuration fails
     */       
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
            // Le mécanisme CSRF est désactivé sinon POST/PUT/PATCH/DELETE ne fonctionnent pas (car Spring exisge un jeton CSRF). Ce mécanisme est inutile pour les API REST.
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> {
              auth.requestMatchers("/risk/**").authenticated();
                auth.anyRequest().denyAll();
            })
          .httpBasic(Customizer.withDefaults()) 
            .build();
    }   
    
    /**
     * Creates the in-memory user details service.
     *
     * <p>
     * This configuration defines a single user stored in memory at startup.
     * The password is hashed using BCrypt.  
     * This approach is appropriate for simple microservices protected with HTTP Basic, where no dynamic user management is required.
     * </p>
     *
     * @param encoder  the BCrypt encoder used to hash the password
     * @param username the username loaded from application properties
     * @param password the password loaded from application properties
     * @return an {@link InMemoryUserDetailsManager} containing the configured user
     */
    @Bean
    public UserDetailsService userDetailsService(
            PasswordEncoder encoder,
            SecurityApiProperties securityProps) {
        var user = User.withUsername(securityProps.getUsername())
                .password(encoder.encode(securityProps.getPassword()))
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
