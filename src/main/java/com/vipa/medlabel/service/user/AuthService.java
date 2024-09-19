package com.vipa.medlabel.service.user;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.vipa.medlabel.config.securityconfig.JwtTokenProvider;
import com.vipa.medlabel.dto.request.user.LoginRequest;
import com.vipa.medlabel.dto.response.user.LoginUserInfo;
import com.vipa.medlabel.model.User;
import com.vipa.medlabel.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    private final UserRepository userRepository;

    public LoginUserInfo login(LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginRequest.getUsernameOrEmail(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();

        User user = userRepository.findByUsername(username);

        return new LoginUserInfo(user.getUsername(), user.getEmail(), user.getPhone(), user.getProfileLink(),
        jwtTokenProvider.generateToken(authentication));
    }
}