package com.cydeo.config;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;
@Component
public class AuthSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        
        Set<String> roles = AuthorityUtils.authorityListToSet(authentication.getAuthorities());

        if (roles.contains("Root User")) {
            response.sendRedirect("/companies/list");
        }
        else if (roles.contains("Admin")) {
            response.sendRedirect("/users/list");
        }
        else if (roles.contains("Manager") || roles.contains("Employee")) {
            response.sendRedirect("/purchaseInvoices/list");
        }
        else {
            // Default redirect for users with no recognized roles
            response.sendError(404, "Role is not recognized");
        }


    }
}
