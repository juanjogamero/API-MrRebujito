package mrRebujito.MrRebujito.controller;

import java.util.Map;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import mrRebujito.MrRebujito.entity.ActorLogin;
import mrRebujito.MrRebujito.security.JWTUtils;

@RestController
public class AuthController {
    private final AuthenticationManager authManager;
    private final JWTUtils jwt;
    public AuthController(AuthenticationManager authManager,JWTUtils jwt){
        this.authManager=authManager;this.jwt=jwt;
    }
    @PostMapping("/login")
    public ResponseEntity<Map<String,String>> login(@Valid @RequestBody ActorLogin credentials){
        var auth=authManager.authenticate(
            new UsernamePasswordAuthenticationToken(credentials.getUsername(),credentials.getPassword()));
        return ResponseEntity.ok(Map.of("token",jwt.generateToken(auth)));
    }
}
