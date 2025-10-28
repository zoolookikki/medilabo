package com.medilabo.webapp.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

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
