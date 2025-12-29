package com.nexsol.tpa.core.domain;

public interface PasswordEncoder {

    String encode(String password);

    boolean matches(String rawPassword, String encodedPassword);

}
