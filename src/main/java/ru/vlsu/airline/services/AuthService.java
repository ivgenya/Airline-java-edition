package ru.vlsu.airline.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.vlsu.airline.dto.AuthRequest;
import ru.vlsu.airline.dto.AuthResponse;
import ru.vlsu.airline.dto.RegisterRequest;
import ru.vlsu.airline.entities.Role;
import ru.vlsu.airline.entities.User;
import ru.vlsu.airline.repositories.RoleRepository;
import ru.vlsu.airline.repositories.UserRepository;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private JwtTokenService jwtService;
    @Autowired
    private AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest request) {
        User user = new User();
        Role role = roleRepository.findByRoleName("CLIENT");
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(role);

        userRepository.save(user);

        String jwtToken = jwtService.generateToken(user);

        AuthResponse authResponse = new AuthResponse();
        authResponse.setToken(jwtToken);
        return authResponse;
    }

    public AuthResponse authentication(AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        User user = userRepository.findByEmail(request.getEmail());

        String jwtToken = jwtService.generateToken(user);

        AuthResponse authResponse = new AuthResponse();
        authResponse.setToken(jwtToken);
        return authResponse;

    }
}
