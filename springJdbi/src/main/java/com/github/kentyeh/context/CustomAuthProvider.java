package com.github.kentyeh.context;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 *
 * @author kent
 */
public class CustomAuthProvider implements AuthenticationProvider {

    private static final Logger logger = LogManager.getLogger(CustomAuthProvider.class);

    private MessageSourceAccessor messageAccessor;
    private final UserDetailsService userDetailsService;
    private ObjectMapper objectMapper;

    public CustomAuthProvider(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Autowired
    @Qualifier("messageAccessor")
    protected void setMessageAccessor(MessageSourceAccessor messageAccessor) {
        this.messageAccessor = messageAccessor;
    }

    @Autowired
    protected void setObjectMapper(final ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public Authentication authenticate(Authentication auth) throws AuthenticationException {
        String account = auth.getName();
        String passwd = auth.getCredentials().toString();
        if (null == account || account.isBlank()) {
            throw new UsernameNotFoundException(messageAccessor.getMessage("BindAuthenticator.badCredentials"));
        } else {
            logger.debug("{}: {}/{}", messageAccessor.getMessage("com.github.kentyeh.context.CustomAuthProvider.validateAccount"), account, passwd);
            ObjectNode json = objectMapper.createObjectNode();
            json.put("account", account);
            json.put("passwd", passwd);
            UserDetails userDetails = userDetailsService.loadUserByUsername(json.toString());
            if (userDetails == null) {
                throw new UsernameNotFoundException(messageAccessor.getMessage("BindAuthenticator.badCredentials"));
            } else if (!userDetails.isEnabled()) {
                throw new DisabledException(messageAccessor.getMessage("com.github.kentyeh.context.CustomUserService.userNotEnabled"));
            } else {
                logger.debug("{}'s roles is {}", account, userDetails.getAuthorities());
            }
            //Authenticate
            return new UsernamePasswordAuthenticationToken(userDetails, auth.getCredentials().toString(), userDetails.getAuthorities());
        }
    }

    @Override
    public boolean supports(Class<?> type) {
        return UsernamePasswordAuthenticationToken.class.equals(type);
    }

}
