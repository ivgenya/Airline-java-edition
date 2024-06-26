package ru.vlsu.airline.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import ru.vlsu.airline.dto.CreateUserDTO;
import ru.vlsu.airline.dto.RoleDTO;
import ru.vlsu.airline.dto.UserDTO;
import ru.vlsu.airline.entities.Role;

import ru.vlsu.airline.entities.User;
import ru.vlsu.airline.services.IAdminUserService;
import ru.vlsu.airline.services.JwtTokenService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminUserController {

    @Autowired
    private IAdminUserService adminUserService;
    @Autowired
    private JwtTokenService jwtTokenService;

    @GetMapping("/all-users")
    public ResponseEntity<List<UserDTO>> getAllUsers(){
        return ResponseEntity.ok(adminUserService.getAllUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUser(
            @PathVariable int id
    ){
        return ResponseEntity.ok(adminUserService.getUser(id));
    }

    @GetMapping("/roles")
    public ResponseEntity<List<RoleDTO>> getRoles(){
        return ResponseEntity.ok(adminUserService.getRoles());
    }

    @GetMapping("/getuserrole")
    public ResponseEntity<String> getUserRole(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            return null;
        }
        Object principal = auth.getPrincipal();
        User user = (principal instanceof User) ? (User) principal : null;
        if (user != null) {
            Role role = user.getRole();
            if (role != null) {
                return ResponseEntity.ok(role.getRoleName());
            }
        }
        return ResponseEntity.ok("Unauthorized");
    }

    @PostMapping("/add")
    public ResponseEntity<String> addNewUser(
            @Valid @RequestBody CreateUserDTO newUser
    ){
        int result = adminUserService.createUser(newUser);

        if(result > 0){
            return new ResponseEntity<>("Пользователь создан", HttpStatus.CREATED);
        }else{
            return  new ResponseEntity<>("Пользователь не был создан", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping(value = "/{id}", produces = "application/json")
    public ResponseEntity<?> changeUserRole(
            @PathVariable int id, @RequestBody UserDTO userDTO
    ){
        userDTO.setId(id);
        int updatedUser = adminUserService.changerUserRole(userDTO);
        if(updatedUser != -1){
            return ResponseEntity.ok(updatedUser);
        }else{
            return new ResponseEntity<>("Пользователь не найден", HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping(value = "/{id}", produces = "application/json")
    public ResponseEntity<?> deleteUser(
            @PathVariable int id
    ){
        int deletedUser = adminUserService.deleteUser(id);
        if(deletedUser != -1){
            return ResponseEntity.ok(deletedUser);
        }else{
            return new ResponseEntity<>("Пользователь не найден", HttpStatus.NOT_FOUND);
        }
    }

}
