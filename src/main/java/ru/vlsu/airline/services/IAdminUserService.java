package ru.vlsu.airline.services;

import ru.vlsu.airline.dto.ChangeRoleDTO;
import ru.vlsu.airline.dto.CreateUserDTO;
import ru.vlsu.airline.dto.UserDTO;
import ru.vlsu.airline.entities.User;
import java.util.List;
public interface IAdminUserService {
    List<UserDTO> getAllUsers();
    User getUser(int userId);
    int deleteUser (int userId);
    int changerUserRole(ChangeRoleDTO userRole);
    int createUser(CreateUserDTO newUser);
}
