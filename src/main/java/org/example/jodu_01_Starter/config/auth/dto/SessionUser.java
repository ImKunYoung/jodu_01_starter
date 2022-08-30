package org.example.jodu_01_Starter.config.auth.dto;

import lombok.Getter;
import org.example.jodu_01_Starter.domain.user.User;

@Getter
public class SessionUser {

    private String name;
    private String email;
    private String picture;

    public SessionUser(User user) {
        this.name = user.getName();
        this.email = user.getEmail();
        this.picture = user.getPicture();
    }

}
