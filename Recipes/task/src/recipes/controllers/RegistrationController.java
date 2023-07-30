package recipes.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import recipes.model.UserAccount;
import recipes.repo.UserRepository;

import javax.validation.Valid;
import java.util.logging.Logger;

@RestController
@RequestMapping("api")
public class RegistrationController {

    private final UserRepository userRepo;

    private final PasswordEncoder encoder;

    public RegistrationController(UserRepository userRepo, PasswordEncoder encoder) {
        this.userRepo = userRepo;
        this.encoder = encoder;
    }

    @PostMapping("/register")
    public ResponseEntity register(@Valid @RequestBody UserAccount user) {

        user.setPassword(encoder.encode(user.getPassword()));
        if (userRepo.existsByEmail(user.getEmail())) {
            return ResponseEntity.badRequest().build();
        }
        userRepo.save(user);
        return ResponseEntity.ok().build();
    }
}