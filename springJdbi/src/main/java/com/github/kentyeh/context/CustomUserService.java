package com.github.kentyeh.context;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.kentyeh.manager.MemberManager;
import com.github.kentyeh.model.Member;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 *
 * @author Kent Yeh
 */
@Service("customUserService")
public class CustomUserService implements UserDetailsService, AuthenticationUserDetailsService<UsernamePasswordAuthenticationToken> {

    private static final Logger logger = LogManager.getLogger(CustomUserService.class);
    private ObjectMapper objectMapper;
    private PasswordEncoder encoder;
    private MessageSourceAccessor messageAccessor;
    private MemberManager memberManager;

    @Autowired
    protected void setEncoder(PasswordEncoder encoder) {
        this.encoder = encoder;
    }

    @Autowired
    @Qualifier("messageAccessor")
    protected void setMessageAccessor(MessageSourceAccessor messageAccessor) {
        this.messageAccessor = messageAccessor;
    }

    @Autowired
    protected void setMemberManager(MemberManager memberManager) {
        this.memberManager = memberManager;
    }

    @Autowired
    protected void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    protected String jsonAsText(JsonNode json, String attr) {
        JsonNode jn = json.get(attr);
        return jn == null || jn instanceof com.fasterxml.jackson.databind.node.NullNode
                ? "" : jn.asText();
    }

    /**
     * @param jsonstring
     * @return
     */
    @Override
    public UserDetails loadUserByUsername(String jsonstring) throws UsernameNotFoundException {
        //Find user data,找到用戶資料
        jsonstring = jsonstring == null ? "" : jsonstring.trim();
        if (jsonstring.startsWith("{") && jsonstring.endsWith("}")) {
            try {
                logger.debug("loadUserByUsername({})", jsonstring);
                JsonNode json = objectMapper.readValue(jsonstring, JsonNode.class);
                String account = jsonAsText(json, "account");
                String passwd = jsonAsText(json, "passwd");
                Member member = memberManager.findByPrimaryKey(account);
                if (member == null || !encoder.matches(passwd, member.getPassword())) {
                    throw new UsernameNotFoundException(messageAccessor.getMessage("exception.invalidCredentials"));
                } else if (!"Y".equals(member.getEnabled())) {
                    throw new UsernameNotFoundException(messageAccessor.getMessage("com.github.kentyeh.context.CustomUserService.userNotEnabled"));
                } else {
                    return new CustomUserInfo(member);
                }
            } catch (JsonProcessingException ex) {
                throw new UsernameNotFoundException(messageAccessor.getMessage("com.github.kentyeh.context.CustomUserService.userNotEnabled"), ex);
            }
        } else {
            Member member = memberManager.findByPrimaryKey(jsonstring);
            if (member == null) {
                throw new UsernameNotFoundException(messageAccessor.getMessage("com.github.kentyeh.context.CustomUserService.userNotEnabled"));
            } else if (!"Y".equals(member.getEnabled())) {
                throw new UsernameNotFoundException(messageAccessor.getMessage("com.github.kentyeh.context.CustomUserService.userNotEnabled"));
            } else {
                return new CustomUserInfo(member);
            }
        }
    }

    /**
     *
     * @param token
     * @return
     */
    @Override
    public UserDetails loadUserDetails(UsernamePasswordAuthenticationToken token) throws UsernameNotFoundException {
        ObjectNode json = objectMapper.createObjectNode();
        json.put("account", token.getName());
        json.put("passwd", token.getCredentials().toString());
        try {
            return loadUserByUsername(objectMapper.writeValueAsString(json));
        } catch (JsonProcessingException ex) {
            throw new UsernameNotFoundException(messageAccessor.getMessage("com.github.kentyeh.context.CustomUserService.userNotEnabled"));
        }
    }
}
