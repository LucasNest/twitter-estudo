package tech.buildrun.springsecurity.service;

import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import tech.buildrun.springsecurity.controller.dto.CreateUserDto;
import tech.buildrun.springsecurity.controller.dto.SimpleUserDto;
import tech.buildrun.springsecurity.controller.dto.UserCustomizationDto;
import tech.buildrun.springsecurity.controller.dto.UserDto;
import tech.buildrun.springsecurity.entities.Role;
import tech.buildrun.springsecurity.entities.User;
import tech.buildrun.springsecurity.entities.UserAppearence;
import tech.buildrun.springsecurity.repository.RoleRepository;
import tech.buildrun.springsecurity.repository.UserAppearenceRepository;
import tech.buildrun.springsecurity.repository.UserRepository;

import java.util.Set;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserAppearenceRepository userAppearenceRepository;
    private final PasswordEncoder passwordEncoder;


    public UserService(UserRepository userRepository, RoleRepository roleRepository, UserAppearenceRepository userAppearenceRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userAppearenceRepository = userAppearenceRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void createUser(CreateUserDto dto){

        var userFromDb = userRepository.findByUsername(dto.username());


        if (userFromDb.isPresent()) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Username already taken");
        }

        var basicRole = roleRepository.findByName(Role.Values.basic.name());

        var user = new User();
        user.setUsername(dto.username());
        user.setPassword(passwordEncoder.encode(dto.password()));
        user.setRoles(Set.of(basicRole));

        userRepository.save(user);

        var customization = new UserAppearence();
        customization.setUser(user);
        customization.setThemeColor(null);
        customization.setDarkMode(false);
        customization.setBio("Ola Não te conhecemos muito bem ainda que tal montar uma BIO?!");
        customization.setBackgroundImageUrl(null);
        customization.setProfileImageUrl(null);

        userAppearenceRepository.save(customization);
    }

    public UserDto listUsers (String search, Integer page, Integer pageSize){
        Page<User> users;

        if(search != null && !search.isBlank()){
            users = userRepository.findByUsernameContainingIgnoreCase(search, PageRequest.of(page, pageSize));
        } else {
            users = userRepository.findAll(
                    PageRequest.of(page, pageSize)
            );
        }

        var simpleUsers = users.map( user ->
                new SimpleUserDto(
                        user.getUserId(),
                        user.getUsername()
                )
        );

        return new UserDto(
                simpleUsers.getContent(),
                page,
                pageSize,
                simpleUsers.getTotalPages(),
                simpleUsers.getTotalElements()
        );
    }

    @Transactional
    public void updateUserCustomization (UserCustomizationDto dto, JwtAuthenticationToken token){

        UUID userId = UUID.fromString(token.getName());

        UserAppearence customization = userAppearenceRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Personalização não encontrada"));


        customization.setThemeColor(dto.themeColor());
        customization.setDarkMode(dto.darkMode());
        customization.setBio(dto.bio());
        customization.setProfileImageUrl(dto.profileImageUrl());
        customization.setBackgroundImageUrl(dto.backgroundImageUrl());

        userAppearenceRepository.save(customization);

    }

    public UserCustomizationDto getUserCustomization (JwtAuthenticationToken token){
        UUID userId = UUID.fromString(token.getName());

        var customization = userAppearenceRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Personalização não encontrada"));

        return new UserCustomizationDto(
                customization.getThemeColor(),
                customization.getBio(),
                customization.getProfileImageUrl(),
                customization.getBackgroundImageUrl(),
                customization.isDarkMode()
        );

    }

}
