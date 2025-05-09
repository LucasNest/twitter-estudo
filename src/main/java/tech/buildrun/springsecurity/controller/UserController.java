package tech.buildrun.springsecurity.controller;

import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import tech.buildrun.springsecurity.controller.dto.*;
import tech.buildrun.springsecurity.entities.User;
import tech.buildrun.springsecurity.repository.UserRepository;
import tech.buildrun.springsecurity.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    private final UserRepository userRepository;

    public UserController(UserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }


    @Transactional
    @PostMapping("")
    public ResponseEntity<Void> newUser(@RequestBody CreateUserDto dto){

        userService.createUser(dto);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/customization")
    public ResponseEntity<Void> updateCustomization(@RequestBody UserCustomizationDto dto,
                                                    JwtAuthenticationToken token){
        userService.updateUserCustomization(dto, token);
        return ResponseEntity.ok().build();
    }

    @GetMapping("customization")
    public ResponseEntity<UserCustomizationDto> listCustomization(JwtAuthenticationToken token){

        UserCustomizationDto customization = userService.getUserCustomization(token);
        return ResponseEntity.ok(customization);
    }


    @GetMapping("/admin")
    @PreAuthorize("hasAuthority('SCOPE_admin')")
    public ResponseEntity<List<User>> listUsers() {

        var users = userRepository.findAll();
        return ResponseEntity.ok(users);
    }

    @GetMapping("")
    public ResponseEntity<UserDto> listSimpleUsers(@RequestParam(value = "page", defaultValue = "0") int page,
                                                         @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
                                                         @RequestParam(value = "search", required = false) String search) {

        UserDto users = userService.listUsers(search, page, pageSize);

        return ResponseEntity.ok(users);
    }
}
