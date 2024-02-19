package ru.vlsu.airline.services;

import org.springframework.stereotype.Service;
import ru.vlsu.airline.entities.Role;

@Service
public class RoleService {

    public Role getRoleByName(String name){
        if(name == "CLIENT"){
            Role clRole = new Role();
            clRole.setRoleName("CLIENT");
            return clRole;
        }else{
            Role adRole = new Role();
            adRole.setRoleName("ADMIN");
            return adRole;
        }
    }
}
