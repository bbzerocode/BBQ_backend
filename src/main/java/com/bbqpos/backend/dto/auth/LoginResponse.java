package com.bbqpos.backend.dto.auth;

import com.bbqpos.backend.model.User;
import lombok.Data;

@Data
public class LoginResponse {

    private String accessToken;
    private String refreshToken;
    private UserDto user;

    public LoginResponse(String accessToken, String refreshToken, User user) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.user = new UserDto(user.getId(), user.getUsername(), user.getRole().name());
    }

    @Data
    public static class UserDto {
        private Long id;
        private String username;
        private String role;

        public UserDto(Long id, String username, String role) {
            this.id = id;
            this.username = username;
            this.role = role;
        }
    }

}
