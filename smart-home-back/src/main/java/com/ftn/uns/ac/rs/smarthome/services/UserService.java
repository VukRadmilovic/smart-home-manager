package com.ftn.uns.ac.rs.smarthome.services;

import com.ftn.uns.ac.rs.smarthome.models.Role;
import com.ftn.uns.ac.rs.smarthome.models.User;
import com.ftn.uns.ac.rs.smarthome.models.dtos.LoginDTO;
import com.ftn.uns.ac.rs.smarthome.models.dtos.TokenDTO;
import com.ftn.uns.ac.rs.smarthome.models.dtos.UserInfoDTO;
import com.ftn.uns.ac.rs.smarthome.models.dtos.UserInfoRegister;
import com.ftn.uns.ac.rs.smarthome.repositories.UserRepository;
import com.ftn.uns.ac.rs.smarthome.services.interfaces.IUserService;
import com.ftn.uns.ac.rs.smarthome.utils.ImageCompressor;
import com.ftn.uns.ac.rs.smarthome.utils.S3API;
import com.ftn.uns.ac.rs.smarthome.utils.SuperadminPasswordGenerator;
import com.ftn.uns.ac.rs.smarthome.utils.TokenUtils;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.MessageSource;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
public class UserService implements IUserService {

    private final UserRepository userRepository;
    private final RoleService roleService;
    private final SuperadminPasswordGenerator passwordGenerator;
    private final MessageSource messageSource;
    private final TokenUtils tokenUtils;
    private final S3API fileServerService;
    private final MailService mailService;
    private BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    public UserService(UserRepository userRepository,
                       RoleService roleService,
                       SuperadminPasswordGenerator passwordGenerator,
                       MessageSource messageSource,
                       TokenUtils tokenUtils,
                       S3API fileServerService,
                       MailService mailService) {
        this.userRepository = userRepository;
        this.roleService = roleService;
        this.passwordGenerator = passwordGenerator;
        this.messageSource = messageSource;
        this.tokenUtils = tokenUtils;
        this.fileServerService = fileServerService;
        this.mailService = mailService;
    }
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isEmpty()) {
            throw new UsernameNotFoundException(String.format("No user found with username '%s'.", username));
        } else {
            return user.get();
        }
    }

    @EventListener(ApplicationReadyEvent.class)
    public void generateSuperadminAccount() {
        if(userRepository.findByUsername("admin").isEmpty()) {
            String randomPassword = passwordGenerator.generateSuperadminPassword();
            String hashedRandomPassword = passwordEncoder().encode(randomPassword);
            List<Role> role = List.of(roleService.getByName("ROLE_SUPERADMIN").get());
            String profilePictureLocation = "http://127.0.0.1:9000/images/profilePictures/admin.jpg";
            User superadmin = new User(1,
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

    @Override
    public TokenDTO login(LoginDTO userInfo) {
        Optional<User> user = this.userRepository.findByUsername(userInfo.getUsername());
        if(user.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, messageSource.getMessage("user.notFound", null, Locale.getDefault()));
        if(!BCrypt.checkpw(userInfo.getPassword(),user.get().getPassword()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, messageSource.getMessage("login.invalid", null, Locale.getDefault()));
        Role userRole = user.get().getRoles().get(0);
        if(userRole.getName().equals("ROLE_SUPERADMIN") && !user.get().getIsConfirmed()) {
            File pwFile = new File("src/main/resources/pwfile.txt");
            pwFile.delete();
            user.get().setIsConfirmed(true);
            userRepository.save(user.get());
        }
        if(!user.get().getIsConfirmed()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, messageSource.getMessage("user.notActivated", null, Locale.getDefault()));
        }
        String token = tokenUtils.generateToken(userInfo.getUsername(), userRole);

        return new TokenDTO(token,tokenUtils.getExpirationDateFromToken(token).getTime());
    }

    @Override
    public void register(UserInfoRegister userInfo) {
        try {
            if(this.userRepository.findByUsername(userInfo.getUsername()).isPresent()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, messageSource.getMessage("username.alreadyUsed", null, Locale.getDefault()));
            }
            Optional<Role> role = this.roleService.getByName(userInfo.getRole());
            if(role.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, messageSource.getMessage("role.notExisting", null, Locale.getDefault()));
            }
            Path filepath = Paths.get("src/main/resources/temp", userInfo.getProfilePicture().getOriginalFilename());
            userInfo.getProfilePicture().transferTo(filepath);
            File file = new File(filepath.toString());
            File compressed = ImageCompressor.compressImage(file, 0.1f, userInfo.getUsername());
            String[] tokens = compressed.getName().split("/");
            String key = tokens[tokens.length - 1];
            String type = userInfo.getProfilePicture().getContentType();
            String bucket = "images";
            String pathToImage = "http://127.0.0.1:9000/" + bucket + '/' + "profilePictures/" + key;
            User toSave = new User(
                    userInfo.getUsername(),
                    userInfo.getEmail(),
                    passwordEncoder().encode(userInfo.getPassword()),
                    false,
                    pathToImage,
                    List.of(role.get())
                    );
            if(role.get().getName().equals("ROLE_ADMIN"))
                toSave.setIsConfirmed(true);
            User saved = this.userRepository.save(toSave);
            if(role.get().getName().equals("ROLE_USER")) {
                String mailMessage = "To activate your account, click on the following link:\n" +
                        "http://127.0.0.1:80/api/user/activate/" + saved.getId();
                boolean sentEmail = this.mailService.sendTextEmail(
                        userInfo.getEmail(),
                        "Account activation",
                        mailMessage
                );
                if (!sentEmail) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, messageSource.getMessage("activation.notSent", null, Locale.getDefault()));
                }
            }
            fileServerService.put(bucket, "profilePictures/" + key, compressed, type)
                    .thenApply(lol -> compressed.delete())
                    .exceptionally(e -> false);

        }
        catch(IOException ex) {
            System.out.println(ex.getMessage());
            throw new ResponseStatusException(HttpStatus.EXPECTATION_FAILED, messageSource.getMessage("compression.error", null, Locale.getDefault()));
        }
    }

    @Override
    public void activate(Integer userId) {
        Optional<User> user = this.userRepository.findById(userId);
        if(user.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, messageSource.getMessage("user.notFound", null, Locale.getDefault()));
        }
        user.get().setIsConfirmed(true);
        this.userRepository.save(user.get());
    }

    @Override
    public UserInfoDTO getUserInfo(Integer userId) {
        Optional<User> user = this.userRepository.findById(userId);
        if(user.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, messageSource.getMessage("user.notFound", null, Locale.getDefault()));
        }
        return new UserInfoDTO(user.get().getUsername(),
                user.get().getEmail(),
                user.get().getProfilePicture(),
                user.get().getRoles().get(0).getName());
    }
}
