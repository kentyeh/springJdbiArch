package com.github.kentyeh.context;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;

/**
 * Real session timeout setting here.
 *
 * @author Kent Yeh
 */
public class CustomAuthSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    private static final Logger logger = LogManager.getLogger(CustomAuthSuccessHandler.class);

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
            HttpServletResponse response, Authentication authentication) throws ServletException, IOException {
        SecurityContextHolder.getContext().setAuthentication(authentication);
        request.getSession().setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, SecurityContextHolder.getContext());
        super.onAuthenticationSuccess(request, response, authentication);
        logger.debug("Loing info:{}", authentication);
    }

}
