package com.example.bank_spring_app.security;

import com.example.bank_spring_app.entity.CustomUser;
import com.example.bank_spring_app.service.BankService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private final BankService bankService;

    public UserDetailsServiceImpl(BankService bankService) {
        this.bankService = bankService;
    }

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        CustomUser customUser = bankService.findByLogin(login);
        if (customUser == null)
            throw new UsernameNotFoundException(login + " not found");

        List<GrantedAuthority> roles = Arrays.asList(
                new SimpleGrantedAuthority(customUser.getRole().toString())
        );

        return new User(customUser.getLogin(), customUser.getPassword(), roles);
    }
}