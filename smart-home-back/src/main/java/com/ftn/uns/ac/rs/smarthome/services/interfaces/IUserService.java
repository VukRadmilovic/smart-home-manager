package com.ftn.uns.ac.rs.smarthome.services.interfaces;

import com.ftn.uns.ac.rs.smarthome.models.User;
import com.ftn.uns.ac.rs.smarthome.models.dtos.TokenDTO;
import com.ftn.uns.ac.rs.smarthome.models.dtos.UserInfoRegister;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface IUserService extends UserDetailsService {
    TokenDTO login(User userInfo);
    void register(UserInfoRegister userInfo);
    void activate(Integer userId);
}
