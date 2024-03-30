package ru.vlsu.airline.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.vlsu.airline.dto.ChangeRoleDTO;
import ru.vlsu.airline.dto.CreateUserDTO;
import ru.vlsu.airline.dto.UserDTO;
import ru.vlsu.airline.entities.Role;
import ru.vlsu.airline.entities.User;
import ru.vlsu.airline.repositories.RoleRepository;
import ru.vlsu.airline.repositories.UserRepository;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AdminUserService implements IAdminUserService {

    private static final Logger logger = LoggerFactory.getLogger(AdminUserService.class);

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenService jwtTokenService;

    @Override
    public List<UserDTO> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public String getUserRole(String token){
        String jwtToken = token.substring(7);
        String username = jwtTokenService.extractUsername(jwtToken);
        User user = userRepository.findByEmail(username);
        return user.getRole().getRoleName();
    }

    @Override
    public User getUser(int userId) {
        Optional<User> user = userRepository.findById(userId);
        if(user.isPresent()){
            return user.get();
        }
        return null;
    }

    @Override
    public int deleteUser(int userId) {
        if (userRepository.existsById(userId)) {
            userRepository.deleteById(userId);
            return userId;
        } else {
            return -1;
        }
    }

    @Override
    public int changerUserRole(ChangeRoleDTO userRole) {
        User user = userRepository.findByEmail(userRole.getEmail());
        Role newRole = roleRepository.findByRoleName(userRole.getRole());
        user.setRole(newRole);
        User changedUser = userRepository.save(user);
        return changedUser.getId();
    }

    @Override
    public int createUser(CreateUserDTO newUser) {
        Role userRole = roleRepository.findByRoleName(newUser.getRole());
        User user = new User();
        user.setEmail(newUser.getEmail());
        user.setPassword(passwordEncoder.encode(newUser.getPassword()));
        user.setRole(userRole);
        User savedUser = userRepository.save(user);
        return savedUser.getId();
    }

    private UserDTO convertToDto(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setEmail(user.getUsername());
        userDTO.setRole(user.getRole().getRoleName());
        return userDTO;
    }
}
