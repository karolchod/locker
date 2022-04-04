package com.locker.userservice;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data //getters and setters
@NoArgsConstructor
@AllArgsConstructor
public class AppUser {
    private int id;
    private String name;
    private String username;
    private String password;
    private int role_id;

}
