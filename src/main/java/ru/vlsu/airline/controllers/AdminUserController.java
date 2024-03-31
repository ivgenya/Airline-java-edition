package ru.vlsu.airline.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.vlsu.airline.dto.ChangeRoleDTO;
import ru.vlsu.airline.dto.CreateUserDTO;
import ru.vlsu.airline.dto.UserDTO;
import ru.vlsu.airline.entities.User;
import ru.vlsu.airline.repositories.RoleRepository;
import ru.vlsu.airline.services.AdminUserService;
import ru.vlsu.airline.services.IAdminUserService;
import ru.vlsu.airline.services.JwtTokenService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminUserController {

    @Autowired
    private IAdminUserService adminUserService;

    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers(){
        return ResponseEntity.ok(adminUserService.getAllUsers());
    }

    @GetMapping("/getuserrole")
    public ResponseEntity<String> getUserRole(@RequestHeader("Authorization") String token){
        if (token == null || !token.startsWith("Bearer ")) {
            return ResponseEntity.ok("Unauthorized");
        }
        return ResponseEntity.ok(adminUserService.getUserRole(token));
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

    @PostMapping("/changerole")
    public ResponseEntity<?> changeUserRole(
            @RequestBody ChangeRoleDTO userRole
    ){
        int updatedUser = adminUserService.changerUserRole(userRole);
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
