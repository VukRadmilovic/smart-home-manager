package com.ftn.uns.ac.rs.smarthome.services.interfaces;

import com.ftn.uns.ac.rs.smarthome.models.User;
import com.ftn.uns.ac.rs.smarthome.models.dtos.*;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface IUserService extends UserDetailsService {
    TokenDTO login(LoginDTO userInfo);
    void register(UserInfoRegister userInfo);
    void activate(Integer userId);
    void sendPasswordResetEmail(String email);
    void resetPassword(PasswordResetDTO newPassword);
    UserInfoDTO getUserInfo(Integer id);
}
