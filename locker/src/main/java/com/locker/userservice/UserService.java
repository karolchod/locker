package com.locker.userservice;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    //    private PasswordEncoder passwordEncoder;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        String sql = "SELECT * FROM appuser WHERE username = '" + username + "'";
        AppUser appUser = jdbcTemplate.queryForObject(sql, BeanPropertyRowMapper.newInstance(AppUser.class));
        if (appUser == null) {
            throw new UsernameNotFoundException("\"User \"+username+\" not found in database.\"");
        }
        String sqlRole = "SELECT * FROM userrole WHERE id = " + appUser.getRole_id();
        UserRole userRole = jdbcTemplate.queryForObject(sqlRole, BeanPropertyRowMapper.newInstance(UserRole.class));
        if (userRole == null) {
            throw new UsernameNotFoundException("\"User \"+username+\" has no role.\"");
        }
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(userRole.getName());
        return new User(appUser.getUsername(), appUser.getPassword(), Collections.singleton(authority));
    }

    ////

    public void saveAppUser(AppUser appUser) {
        System.out.println("Saving new user " + appUser.getName() + " to the database");
        appUser.setPassword(bCryptPasswordEncoder.encode(appUser.getPassword()));
        SimpleJdbcInsert insertActor = new SimpleJdbcInsert(jdbcTemplate);
        insertActor.withTableName("appuser").usingColumns("name", "username", "password", "role_id");
        BeanPropertySqlParameterSource param = new BeanPropertySqlParameterSource(appUser);
        insertActor.execute(param);
    }

    public void updateAppUser(AppUser appUser) {
        System.out.println("Updating appuser " + appUser.getUsername());
        String sql;
        if(appUser.getPassword().equals("encrypted")){ //keyword for not updating password, e.g. when approving by admin
            sql = "UPDATE appuser SET name = :name, username = :username, role_id = :role_id  WHERE id = :id";
        }else{
            appUser.setPassword(bCryptPasswordEncoder.encode(appUser.getPassword()));  //new password
            sql = "UPDATE appuser SET name = :name, username = :username, password = :password, role_id = :role_id  WHERE id = :id";
        }
        BeanPropertySqlParameterSource param = new BeanPropertySqlParameterSource(appUser);
        NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(jdbcTemplate);
        template.update(sql, param);
    }


    public AppUser getAppUser(String username) {//password cannot be removed because method is used by security
        System.out.println("Fetching appuser " + username);
        String sql = "SELECT * FROM appuser WHERE username = '" + username + "'";
        return jdbcTemplate.queryForObject(sql, BeanPropertyRowMapper.newInstance(AppUser.class));
    }

    public AppUser getAppUser(int id) {
        System.out.println("Fetching appuser id " + id);
        String sql = "SELECT * FROM appuser WHERE id = " + id;
        return jdbcTemplate.queryForObject(sql, BeanPropertyRowMapper.newInstance(AppUser.class));
    }

    public List<AppUser> getAppUsers() {
        System.out.println("Fetching all users with ROLE_USER without passwords");
        String sql = "SELECT * FROM appuser WHERE role_id NOT IN (3, 4)";//exclude ROLE_LOCKER and ROLE_NEW //todo get id based on role name
        List<AppUser> users = jdbcTemplate.query(sql, BeanPropertyRowMapper.newInstance(AppUser.class));
        users.forEach(
                user -> {
                    user.setPassword("encrypted");
                }
        );
        return users;
    }

    public List<AppUser> getNewAppUsers() {
        System.out.println("Fetching new users without passwords");
        String sql = "SELECT * FROM appuser WHERE role_id = 4"; //only ROLE_NEW //todo get id based on role name
        List<AppUser> users = jdbcTemplate.query(sql, BeanPropertyRowMapper.newInstance(AppUser.class));
        users.forEach(
                user -> {
                    user.setPassword("encrypted");
                }
        );
        return users;
    }

    public List<UserRole> getUserRoles() {
        System.out.println("Fetching all roles");
        String sql = "SELECT * FROM userrole";
        List<UserRole> roles = jdbcTemplate.query(sql, BeanPropertyRowMapper.newInstance(UserRole.class));
        return roles;
    }

    public UserRole getUserRole(int id) {
        System.out.println("Fetching role id " + id);
        String sql = "SELECT * FROM userrole WHERE id = " + id;
        return jdbcTemplate.queryForObject(sql, BeanPropertyRowMapper.newInstance(UserRole.class));
    }

    public UserRole getUserRole(String name) {
        System.out.println("Fetching role name " + name);
        String sql = "SELECT * FROM userrole WHERE name = '" + name + "'";
        return jdbcTemplate.queryForObject(sql, BeanPropertyRowMapper.newInstance(UserRole.class));
    }




}
