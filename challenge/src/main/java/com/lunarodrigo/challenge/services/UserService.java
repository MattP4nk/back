package com.lunarodrigo.challenge.services;

import java.util.ArrayList;

import javax.management.BadAttributeValueExpException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.lunarodrigo.challenge.dtos.ChangePasswordDTO;
import com.lunarodrigo.challenge.dtos.LoginDTO;
import com.lunarodrigo.challenge.dtos.RegistrationDTO;
import com.lunarodrigo.challenge.dtos.ResponseDTO;
import com.lunarodrigo.challenge.models.Role;
import com.lunarodrigo.challenge.models.UserModel;
import com.lunarodrigo.challenge.repositories.IUserRepository;
import com.lunarodrigo.challenge.security.jwt.JWTService;

@Service
public class UserService {

    ResponseDTO response = new ResponseDTO();

    @Autowired
    IUserRepository userRepository;

    @Autowired
    AuthenticationManager authManager;

    @Autowired
    JWTService jWTService;

    @Autowired
    PasswordEncoder passwordEncoder;

    public ArrayList<UserModel> getUsers() {
        return (ArrayList<UserModel>) userRepository.findAll();
    }

    public ResponseDTO register(RegistrationDTO registrationDTO) {

        UserModel userModel = UserModel.builder()
                .username(registrationDTO.username)
                .password(passwordEncoder.encode(registrationDTO.password))
                .email(registrationDTO.email)
                .role(Role.USER)
                .build();
        try {
            userRepository.save(userModel);
            userModel.setPassword("password");
            response.setStatus("OK");
            response.setPack(userModel);
        } catch (Exception e) {
            if (e.getLocalizedMessage().contains("Email is not valid")) {
                response.setStatus("FAILED. Please check email format.");
            } else if (e.getLocalizedMessage().contains("Password is too short")) {
                response.setStatus("FAILED. Password is too short");
            } else if (e.getLocalizedMessage().contains("Duplicate entry")) {
                response.setStatus("FAILED. Username or Email already in use.");
            } else {
                response.setStatus(e.getLocalizedMessage());
            }
        }
        return response;
    }

    public ResponseDTO login(LoginDTO loginDTO) {
        try {
            authManager
                    .authenticate(
                            new UsernamePasswordAuthenticationToken(loginDTO.getUsername(), loginDTO.getPassword()));
            UserDetails user = userRepository.findByUsername(loginDTO.getUsername());
            String token = jWTService.getToken(user);
            response.setToken(token);
            response.setStatus("OK");
            ((UserModel) user).setPassword("Password");
            response.setPack(((UserModel) user));
        } catch (AuthenticationException e) {
            if (e.getLocalizedMessage().contains("Bad credentials")) {
                response.setStatus("FAILED. Wrong username or password");
            } else {
                response.setStatus("FAILED.");
                response.setPack(e.getLocalizedMessage());
            }
        }
        return response;
    }

    public ResponseDTO deleteUser(String target) {
        try {
            UserModel user = userRepository.findByUsername(target);
            if (user == null) {
                throw new UsernameNotFoundException("Not found");
            }
            userRepository.delete(user);
            response.setStatus("OK. FAILED. " + target + " not found in users list.");
        } catch (UsernameNotFoundException e) {
            response.setStatus("FAILED. " + target + " not found in users list.");
        }
        return response;
    }

    public ResponseDTO upgradeUser(String target) {
        try {
            UserModel user = userRepository.findByUsername(target);
            if (user == null) {
                throw new UsernameNotFoundException("Not found");
            }
            if (user.getRole() != Role.USER) {
                throw new BadAttributeValueExpException("" + target + " is an admin already");
            }
            user.setRole(Role.ADMIN);
            userRepository.save(user);
            response.setStatus("OK");
            response.setPack(user);
        } catch (UsernameNotFoundException | BadAttributeValueExpException e) {
            response.setStatus("FAILED. Incorrect or nonexistent user");
        }
        return response;
    }

    public ResponseDTO downgradeAdmin(String target) {
        try {
            UserModel admin = userRepository.findByUsername(target);
            if (admin == null) {
                throw new UsernameNotFoundException("Not found");
            }
            if (admin.getRole() != Role.ADMIN) {
                throw new BadAttributeValueExpException("" + target + " is an user already");
            }
            admin.setRole(Role.USER);
            userRepository.save(admin);
            response.setStatus("OK");
            response.setPack(admin);
        } catch (UsernameNotFoundException | BadAttributeValueExpException e) {
            response.setStatus("FAILED. Incorrect or nonexistent Admin");
        }
        return response;
    }

    public ResponseDTO changePassword(ChangePasswordDTO credentials) {
        try {
            UserModel user = userRepository.findByUsername(credentials.getUsername());
            if (user == null) {
                throw new UsernameNotFoundException("Not found");
            }
            if (user.getPassword() != credentials.getOldPassword()) {
                throw new BadCredentialsException("Wrong Password");
            }
            user.setPassword(credentials.newPassword);
            userRepository.save(user);
            response.setStatus("OK");
            response.setPack(user);
        } catch (UsernameNotFoundException | BadCredentialsException e) {
            response.setStatus("FAILED. Incorrect username or password");
        }
        return response;
    }

}
