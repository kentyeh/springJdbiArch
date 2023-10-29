package com.github.kentyeh.context;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Optional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 *
 * @author Kent Yeh
 */
public class CaptchaUsernamePasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private static final Logger logger = LogManager.getLogger(CaptchaUsernamePasswordAuthenticationFilter.class);
    private static final String CAPTCHA = "captcha";
    @Autowired
    @Qualifier("messageAccessor")
    MessageSourceAccessor messageAccessor;

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
            HttpServletResponse response) throws AuthenticationException {
        Optional<String> captcha = Optional.ofNullable(request.getParameter(CAPTCHA));
        Optional<Object> sessionCaptcha = Optional.ofNullable(request.getSession().getAttribute(CAPTCHA));
        request.getSession().removeAttribute(CAPTCHA);
        if (!captcha.isPresent() || captcha.orElse("").isBlank() || !sessionCaptcha.isPresent()) {
            throw new AuthenticationServiceException(messageAccessor.getMessage("com.github.kentyeh.context.CaptchaUsernamePasswordAuthenticationFilter.notEmptyCaptcha"));
        } else if(!captcha.orElse("").equalsIgnoreCase("" + sessionCaptcha.orElse(""))){
            logger.debug("captcha:{} not equal session's captcha:{}", captcha, sessionCaptcha.get());
            throw new AuthenticationServiceException(messageAccessor.getMessage("com.github.kentyeh.context.CaptchaUsernamePasswordAuthenticationFilter.captchaNotValid"));
        }
        return super.attemptAuthentication(request, response);
    }
}
