package com.medilabo.webapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security configuration.
 *
 */
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
     * Defines the application’s main security filter chain.
     *
     * <p>This configuration:</p>
     * <ul>
     *     <li>allows public access to the login page and static assets,</li>
     *     <li>requires authentication for all other endpoints,</li>
     *     <li>enables form-login with a custom redirection after success,</li>
     *     <li>configures logout behavior (session invalidation + cookie removal).</li>
     * </ul>
     *
     * @param http the shared {@link HttpSecurity} instance provided by Spring Security
     * @return a fully configured {@code SecurityFilterChain}
     * @throws Exception if an error occurs during configuration
     */    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
            .authorizeHttpRequests(auth -> {
                /*
                Autoriser l'accès à la racine et à la page de login sans authentification.
                Autoriser les fichiers statiques.
                */
                auth.requestMatchers("/login", "/favicon.ico", "/images/**", "/css/**", "/js/**").permitAll();
                /*
                Toute autre requête nécessite une connexion, mais sans restriction de rôle spécifique.
                Un utilisateur sans connexion sera redirigé vers la page de login.
                */
                auth.anyRequest().authenticated();
            })
            .formLogin(form -> form
                    // la page personnalisée.
                    //.loginPage("/login")
                    // toujours rediriger ici.
                    .defaultSuccessUrl("/patients", true) 
                    // Tout le monde peut y accéder
                    .permitAll()
                  )
            .logout(l -> l
                    // la page peronnalisée.
                    //.logoutUrl("/logout")
                    // Redirige vers la page de login après la déconnexion (si page personnalisée)
                    //.logoutSuccessUrl("/login?logout")
                    // Invalide la session
                    .invalidateHttpSession(true)
                    // Supprime le cookie de session
                    .deleteCookies("JSESSIONID")
                    // Tout le monde peut y accéder
                    .permitAll()
                  )            
            .build();
    }   
    
    /**
     * Provides the {@link UserDetailsService} used for authentication.
     *
     * @param encoder  the password encoder used to hash the password
     * @param username the username loaded from application properties
     * @param password the password loaded from application properties
     * @return an initialized {@code UserDetailsService}
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
