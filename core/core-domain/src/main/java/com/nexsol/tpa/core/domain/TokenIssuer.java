package com.nexsol.tpa.core.domain;

public interface TokenIssuer {

    String issueToken(Long id, String role);

}
