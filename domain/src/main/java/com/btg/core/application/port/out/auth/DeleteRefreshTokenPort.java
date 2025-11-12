package com.btg.core.application.port.out.auth;

public interface DeleteRefreshTokenPort {

    void deleteByToken(String token);

    void deleteByUserId(Long userId);
}
