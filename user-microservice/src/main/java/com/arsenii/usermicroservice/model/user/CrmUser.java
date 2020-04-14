package com.arsenii.usermicroservice.model.user;


import com.arsenii.usermicroservice.validation.ValidEmail;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class CrmUser {

    @NotNull(message = "is required")
    @Size(min = 1, message = "must be at least 3 characters")
    private String username;   // it's submitted from the form

    @NotNull(message = "is required")
    @Size(min = 1, message = "must be more than 5 characters")
    private String password;


    @ValidEmail
    @NotNull(message = "is required")
    @Size(min = 1, message = "is required")
    private String email;

    public CrmUser() {

    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
