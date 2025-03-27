package com.expenses.tracker.service;

import com.expenses.tracker.entity.User;
import com.expenses.tracker.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    public UserService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder,
                       AuthenticationManager authenticationManager, JwtService jwtService) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    public ResponseEntity<Object> signup(User user) {
        if(user.getEmail() == null || user.getUsername() == null || user.getPassword() == null) {
            return new ResponseEntity<>("All Fields are Required" , HttpStatus.EXPECTATION_FAILED);
        }

        User u = userRepository.findByEmail(user.getEmail());
        if(!Objects.isNull(u)) {
            return new ResponseEntity<>("User with " + user.getEmail() + " already Existed", HttpStatus.NOT_FOUND);
        }
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        return new ResponseEntity<>(userRepository.save(user), HttpStatus.ACCEPTED);
    }

    public ResponseEntity<Object> login(User user) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        user.getEmail(),
                        user.getPassword()
                )
        );
        if(authentication.isAuthenticated()) {
            //Generate JWT Token
            String token = jwtService.generateToken(user);
            Map<String, String> response = new HashMap<>();
            response.put("accessToken", token);

            return ResponseEntity.ok(response);
        }
        return new ResponseEntity<>("FAILED", HttpStatus.NOT_ACCEPTABLE);
    }

    public User profile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication.isAuthenticated()) {
            String email = authentication.getName();
            if(email == null) {
                throw new RuntimeException("USER NOT FOUND");
            }
            return userRepository.findByEmail(email);
        }
        return null;
    }
}
