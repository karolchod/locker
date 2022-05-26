package com.locker.userservice;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.locker.security.SecurityProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(path = "/api/user")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class UserServiceController {

    @Autowired
    private UserService userService;

    SecurityProperties securityProperties = new SecurityProperties();


    @GetMapping("/users") //all users except locker, new
    public ResponseEntity<List<AppUser>> getUsers() {
        return ResponseEntity.ok().body(userService.getAppUsers());
    }

    @GetMapping("/newusers") //new users
    public ResponseEntity<List<AppUser>> getNewUsers() {
        return ResponseEntity.ok().body(userService.getNewAppUsers());
    }

    @GetMapping("/roles") //roles
    public ResponseEntity<List<UserRole>> getRoles() {
        return ResponseEntity.ok().body(userService.getUserRoles());
    }

    @PostMapping("/register") //register user, not restricted by security
    public ResponseEntity<?> saveUser(@RequestBody AppUser appUser) {
        UserRole ur = userService.getUserRole("ROLE_USER");
        appUser.setRole_id(ur.getId());
        userService.saveAppUser(appUser);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/edituser") //edit user,
    public ResponseEntity<?> editUser(@RequestBody AppUser appUser) {
        userService.updateAppUser(appUser);
        return ResponseEntity.ok().build();
    }


    @GetMapping("/appuser") //app user by id
    public ResponseEntity<AppUser> getAppUser(@RequestParam int id) {
        AppUser appUser = userService.getAppUser(id);
        if (appUser == null)
            return ResponseEntity.notFound().build();
        appUser.setPassword("encrypted");
        return ResponseEntity.ok().body(appUser);
    }


    @GetMapping("/bytoken")
    public AppUser getUserByToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        String access_token = authorizationHeader.substring("Bearer ".length());
        Algorithm algorithm = Algorithm.HMAC256(securityProperties.get_token_secret().getBytes());
        JWTVerifier verifier = JWT.require(algorithm).build();
        DecodedJWT decodedJWT = verifier.verify(access_token);
        String username = decodedJWT.getSubject();
        AppUser appUser = userService.getAppUser(username);
        appUser.setPassword("encrypted");
        return appUser;
    }

}


