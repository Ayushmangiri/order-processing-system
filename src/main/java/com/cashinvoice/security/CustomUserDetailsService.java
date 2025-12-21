package com.cashinvoice.security;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final InMemoryUserDetailsManager manager;

    public CustomUserDetailsService(PasswordEncoder passwordEncoder) {
        this.manager = new InMemoryUserDetailsManager();

        manager.createUser(
                User.withUsername("admin")
                        .password(passwordEncoder.encode("admin123"))
                        .roles("ADMIN")
                        .build()
        );

        manager.createUser(
                User.withUsername("user")
                        .password(passwordEncoder.encode("user123"))
                        .roles("USER")
                        .build()
        );
    }

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {
        return manager.loadUserByUsername(username);
    }
}
