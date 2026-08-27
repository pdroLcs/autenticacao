package dev.pdrolcs.autenticacao.service;

import dev.pdrolcs.autenticacao.dto.response.MeResponse;
import dev.pdrolcs.autenticacao.entity.User;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    public MeResponse me(Authentication authentication) {
        var user = (User) authentication.getPrincipal();
        return new MeResponse(user.getName(), user.getEmail());
    }

}
