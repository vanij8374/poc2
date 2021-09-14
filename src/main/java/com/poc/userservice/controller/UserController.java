package com.poc.userservice.controller;

import com.poc.userservice.model.ErrorDetails;
import com.poc.userservice.model.User;
import com.poc.userservice.model.UsersResponse;
import com.poc.userservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("userservice")
public class UserController {

    @Autowired
    private UserService service;

    @PutMapping("user")
    public ResponseEntity<String> createUser(@Valid @RequestBody User user){
        service.createUser(user);
        return ResponseEntity.ok("User Created SuccessFully");
    }
    @PatchMapping("user/{id}")
    public ResponseEntity<String> updateUser(@NotNull @PathVariable String id,@Valid @RequestBody User user){
        user.setId(id);
        service.updateUser(user);
        return ResponseEntity.ok("User Updated SuccessFully");
    }

    @GetMapping("users")
    public ResponseEntity<UsersResponse> getUsers(@RequestParam(required = false) String surname,
                                                  @RequestParam(required = false) String name,
                                                  @RequestParam(required = false) Integer pinCode){
        List<User> users;
        UsersResponse response = new UsersResponse();
        if (name!=null){
            users = service.searchUserByName(name);
        }else if(surname!=null){
            users = service.searchUserBySurname(surname);
        }else if(pinCode!=null) {
            users = service.searchUserByPinCode(pinCode);
        }else {
            Map<String,String> errors = new HashMap<>();
            errors.put("Condition Required","surname or name or pinCode required");
            ErrorDetails errorDetails = new ErrorDetails(new Date(),"Condition Required",errors);
            response.setErrorDetails(errorDetails);
            return ResponseEntity.badRequest().body(response);
        }
        response.setUsers(users);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("user/{id}")
    public ResponseEntity<String> deleteUser(@NotNull @PathVariable String id,
                                             @RequestParam(value = "softDelete",required = false) boolean isSoftDelete){
        if(isSoftDelete){
            service.softDeleteUser(id);
        }else {
            service.deleteUser(id);
        }
        return ResponseEntity.ok("User deleted SuccessFully");
    }
}
