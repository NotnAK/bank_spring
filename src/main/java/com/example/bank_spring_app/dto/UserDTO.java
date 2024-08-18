package com.example.bank_spring_app.dto;

import com.example.bank_spring_app.entity.UserRole;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/*
@Getter
@Setter
public class UserDTO {
    private Integer id;
    private String name;
    private String address;
    private List<AccountDTO> accounts;  // Список аккаунтов пользователя

    public UserDTO() {
    }

    public UserDTO(Integer id, String name, String address, List<AccountDTO> accounts) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.accounts = accounts;
    }
}
*/
@Getter
@Setter
public class UserDTO {
    private Integer id;
    private String login;
    private String password;
    private String name;
    private String address;
    private UserRole role;
    private List<AccountDTO> accounts;

    public UserDTO() {
    }

    public UserDTO(Integer id, String login, String password, UserRole role,String name, String address, List<AccountDTO> accounts) {
        this.id = id;
        this.login = login;
        this.password = password;
        this.name = name;
        this.address = address;
        this.role = role;  // Инициализация роли
        this.accounts = accounts;
    }

    public UserDTO(String login, String password, UserRole role, String name, String address) {
        this.login = login;
        this.password = password;
        this.name = name;
        this.address = address;
        this.role = role;  // Инициализация роли

    }
}