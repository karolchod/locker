package com.locker.lockerservice;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data //getters and setters
@NoArgsConstructor
@AllArgsConstructor
public class Locker {
    private int id;
    private String name;
}
