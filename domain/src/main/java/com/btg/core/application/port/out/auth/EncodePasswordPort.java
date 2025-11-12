package com.btg.core.application.port.out.auth;

public interface EncodePasswordPort {

    String encode(String rawPassword);

    boolean matches(String rawPassword, String encodedPassword);
}
