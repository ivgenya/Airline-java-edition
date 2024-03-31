package ru.vlsu.airline.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.vlsu.airline.dto.ChangeRoleDTO;
import ru.vlsu.airline.dto.CreateUserDTO;
import ru.vlsu.airline.dto.RoleDTO;
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
        List<User> users = userRepository.findAdminDispatcher();
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
    public UserDTO getUser(int userId) {
        Optional<User> user = userRepository.findById(userId);
        if(user.isPresent()){
            UserDTO userDTO = convertToDto(user.get());
            return userDTO;
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
    public int changerUserRole(UserDTO userDTO) {
        Optional<User> user = userRepository.findById(userDTO.getId());
        if(user.isPresent()){
            User userToChange = user.get();
            Role newRole = roleRepository.findByRoleName(userDTO.getRole());
            userToChange.setRole(newRole);
            User changedUser = userRepository.save(userToChange);
            return changedUser.getId();
        }
        return -1;
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

    @Override
    public List<RoleDTO> getRoles() {
        List<Role> roles = roleRepository.findAll();
        return roles.stream()
                .map(this::convertToDtoRole)
                .collect(Collectors.toList());
    }

    private RoleDTO convertToDtoRole(Role role){
        RoleDTO roleDTO = new RoleDTO();
        roleDTO.setRoleName(role.getRoleName());
        return roleDTO;
    }

    private UserDTO convertToDto(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setEmail(user.getUsername());
        userDTO.setRole(user.getRole().getRoleName());
        return userDTO;
    }
}
