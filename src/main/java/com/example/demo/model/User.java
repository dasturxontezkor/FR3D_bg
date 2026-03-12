package com.example.demo.model;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String username;

    private String phone;    // email o'rniga phone
    private String password;

    public Long getId()                  { return id; }
    public void setId(Long id)           { this.id = id; }

    public String getUsername()              { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPhone()             { return phone; }
    public void setPhone(String phone)   { this.phone = phone; }

    public String getPassword()              { return password; }
    public void setPassword(String password) { this.password = password; }

    @Override
    public String toString() {
        return "User{username='" + username + "', phone='" + phone + "'}";
    }
}