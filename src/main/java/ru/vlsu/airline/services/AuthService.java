package ru.vlsu.airline.services;

import lombok.Builder;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import ru.vlsu.airline.entities.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.vlsu.airline.dto.AuthRequest;
import ru.vlsu.airline.dto.AuthResponse;
import ru.vlsu.airline.dto.RegisterRequest;
import ru.vlsu.airline.entities.Role;
import ru.vlsu.airline.entities.UserRole;
import ru.vlsu.airline.repositories.UserRepository;

import java.util.List;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passEnc;

    private final JwtService jwtService;

    private final AuthenticationManager authenticationManager;

    private final RoleService roleService;

    public AuthService(UserRepository userRepository, PasswordEncoder passEnc, JwtService jwtService, AuthenticationManager authenticationManager, RoleService roleService) {
        this.userRepository = userRepository;
        this.passEnc = passEnc;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.roleService = roleService;
    }

    public AuthResponse register(RegisterRequest request) {

        Role clientRole = roleService.getRoleByName("CLIENT");

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        user.setUserRoles((List<UserRole>) clientRole);

        userRepository.save(user);
        var jwtToken = jwtService.generateToken(user);
        AuthResponse aR = new AuthResponse();
        aR.setToken(jwtToken);
        return aR;
    }

    public AuthResponse authenticate(AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow();
        var jwtToken = jwtService.generateToken(user);
        AuthResponse aR = new AuthResponse();
        aR.setToken(jwtToken);
        return aR;
    }
}
