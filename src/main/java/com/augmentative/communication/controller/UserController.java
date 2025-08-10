package com.augmentative.communication.controller;

import com.augmentative.communication.dto.LoginRequest;
import com.augmentative.communication.dto.LoginResponse;
import com.augmentative.communication.dto.UserDTO;
import com.augmentative.communication.model.User; // Import User entity
import com.augmentative.communication.service.UserService;
import com.augmentative.communication.util.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;


/**
 * REST Controller for User-related operations.
 * Handles user registration, login, and retrieval.
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    public UserController(UserService userService, AuthenticationManager authenticationManager, JwtUtil jwtUtil, UserDetailsService userDetailsService) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    /**
     * Registers a new user.
     * The password will be hashed by the UserService.
     *
     * @param userDTO The user DTO to register.
     * @return The registered user DTO with HTTP status 201 (Created).
     */
    @PostMapping("/register")
    public ResponseEntity<UserDTO> registerUser(@RequestBody UserDTO userDTO) {
        UserDTO savedUser = userService.save(userDTO);
        return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
    }

    /**
     * Authenticates a user and returns a JWT token upon successful login.
     *
     * @param loginRequest The login request containing username and password.
     * @return A LoginResponse containing the JWT token and user ID.
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> loginUser(@RequestBody LoginRequest loginRequest) {
        try {
            // Authenticate the user using Spring Security's AuthenticationManager
            Authentication authenticate = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
            );

            // If authentication is successful, generate a JWT token
            final UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getUsername());
            final String token = jwtUtil.generateToken(userDetails.getUsername()); // Renamed jwt to token

            // Retrieve the User entity to get the ID
            User user = userService.findByUsername(loginRequest.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found after authentication."));

            return ResponseEntity.ok(new LoginResponse(token, user.getId())); // Include userId in response, renamed jwt to token

        } catch (Exception e) {
            // Return unauthorized status if authentication fails
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    /**
     * Retrieves a user by their ID. Requires authentication.
     *
     * @param id The ID of the user.
     * @return The user DTO if found, or HTTP status 404 (Not Found).
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        return userService.findById(id)
                .map(userDTO -> new ResponseEntity<>(userDTO, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}