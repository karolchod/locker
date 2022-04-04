package com.locker.boxservice;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data //getters and setters
@NoArgsConstructor
@AllArgsConstructor
public class Box {
    private int id;
    private int locker_id;
    private boolean isused;
    private boolean isopen;

    public boolean getIsused() {
        return isused;
    }

    public void setIsused(boolean used) {
        isused = used;
    }

    public boolean getIsopen() {
        return isopen;
    }

    public void setIsopen(boolean open) {
        isopen = open;
    }


}