package com.github.kentyeh.context;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

/**
 *
 * @author Kent Yeh
 */
public class CustomAuthFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    private static final Logger logger = LogManager.getLogger(CustomAuthFailureHandler.class);

    @Override
    public void onAuthenticationFailure(final HttpServletRequest request,
            final HttpServletResponse response,
            final AuthenticationException exception) throws IOException, ServletException {
        String val = request.getParameter("username");
        if (val != null && !val.isBlank()) {
            request.getSession().setAttribute("username", val);
            logger.debug("\"" + val + "\"登錄失敗:" + exception.getMessage(), exception);
        } else {
            logger.debug("登錄失敗:" + exception.getMessage(), exception);
        }
        if (request.getParameterMap().containsKey("remeber-me")) {
            request.getSession().setAttribute("remeber-me", request.getParameter("remeber-me"));
        }
        super.onAuthenticationFailure(request, response, exception);
    }
}
