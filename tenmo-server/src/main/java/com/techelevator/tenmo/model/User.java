package com.techelevator.tenmo.model;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class User {

   private Long id;
   private String username;
   private String password;
   private BigDecimal balance;
   private boolean activated;
   private Set<Authority> authorities = new HashSet<>();

   public User() {
      this.activated = true;
   }

   public User(Long id, String username, String password, BigDecimal balance, String authorities) {
      this();
      this.id = id;
      this.username = username;
      this.password = password;
      this.balance = balance;
      setAuthorities(authorities);
   }


   public Long getId() {
      return id;
   }

   public void setId(Long id) {
      this.id = id;
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

   public BigDecimal getBalance() {
      return balance;
   }

   public void setBalance(BigDecimal balance) {
      this.balance = balance;
   }

   public void setBalance(int balance) {
      this.balance = BigDecimal.valueOf(balance);
   }

   public boolean isActivated() {
      return activated;
   }

   public void setActivated(boolean activated) {
      this.activated = activated;
   }

   public Set<Authority> getAuthorities() {
      return authorities;
   }

   public void setAuthorities(Set<Authority> authorities) {
      this.authorities = authorities;
   }

   public void setAuthorities(String authorities) {
      this.authorities.clear();
      String[] roles = authorities.split(",");
      for (String role : roles) {
         this.authorities.add(new Authority("ROLE_" + role.trim()));
      }
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      User user = (User) o;
      return activated == user.activated &&
              Objects.equals(id, user.id) &&
              Objects.equals(username, user.username) &&
              Objects.equals(password, user.password) &&
              Objects.equals(balance, user.balance) &&
              Objects.equals(authorities, user.authorities);
   }

   @Override
   public int hashCode() {
      return Objects.hash(id, username, password, balance, activated, authorities);
   }

   @Override
   public String toString() {
      return "User{" +
              "id=" + id +
              ", username='" + username + '\'' +
              ", activated=" + activated +
              ", balance=" + balance +
              ", authorities=" + authorities +
              '}';
   }
}