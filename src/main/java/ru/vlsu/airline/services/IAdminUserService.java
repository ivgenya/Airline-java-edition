package ru.vlsu.airline.services;

import ru.vlsu.airline.dto.CreateUserDTO;
import ru.vlsu.airline.dto.RoleDTO;
import ru.vlsu.airline.dto.UserDTO;

import java.util.List;
public interface IAdminUserService {
    List<UserDTO> getAllUsers();
    UserDTO getUser(int userId);
    String getUserRole(String token);
    int deleteUser (int userId);
    int changerUserRole(UserDTO userDTO);
    int createUser(CreateUserDTO newUser);

    List<RoleDTO> getRoles();
}
