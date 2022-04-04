package com.locker.security;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class SecurityProperties {

    private Properties prop;

    public SecurityProperties() {

        //System.out.println("config");
        this.prop = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("token.properties")) {
            this.prop.load(input);
            System.out.println("---Token values read from file token.properties");
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    public int get_access_token_validity() {
        return Integer.parseInt(this.prop.getProperty("access_token_validity"));
    }

    public int get_refresh_token_validity() {
        return Integer.parseInt(this.prop.getProperty("refresh_token_validity"));
    }

    public String get_token_secret() {
        return this.prop.getProperty("token_secret");
    }

}
