package com.ftn.uns.ac.rs.smarthome.services;

import com.ftn.uns.ac.rs.smarthome.models.Role;
import com.ftn.uns.ac.rs.smarthome.models.User;
import com.ftn.uns.ac.rs.smarthome.repositories.UserRepository;
import com.ftn.uns.ac.rs.smarthome.services.interfaces.IUserService;
import com.ftn.uns.ac.rs.smarthome.utils.SuperadminPasswordGenerator;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
public class UserService implements IUserService {

    private final UserRepository userRepository;
    private final RoleService roleService;
    private final SuperadminPasswordGenerator passwordGenerator;
    private BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return null;
    }

    public UserService(UserRepository userRepository,
                       RoleService roleService,
                       SuperadminPasswordGenerator passwordGenerator) {
        this.userRepository = userRepository;
        this.roleService = roleService;
        this.passwordGenerator = passwordGenerator;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void generateSuperadmin() {
        if(userRepository.findByUsername("admin").isEmpty()) {
            String randomPassword = passwordGenerator.generateSuperadminPassword();
            String hashedRandomPassword = passwordEncoder().encode(randomPassword);
            List<Role> role = List.of(roleService.getByName("ROLE_SUPERADMIN").get());
            String profilePictureLocation = "127.0.0.1:9000/images/profilePictures/super-admin-icon.jpg";
            User superadmin = new User(1L,
                    "admin",
                    "smarthome.superadmin@no-reply.com",
                    hashedRandomPassword,
                    false,
                    profilePictureLocation,
                    role
                    );
            userRepository.save(superadmin);

            try {
                PrintWriter writer = new PrintWriter("src/main/resources/pwfile.txt", StandardCharsets.UTF_8);
                writer.println(randomPassword);
                writer.close();
            }
            catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }
}
