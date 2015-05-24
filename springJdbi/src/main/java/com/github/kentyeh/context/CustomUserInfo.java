package com.github.kentyeh.context;

import com.github.kentyeh.model.Member;
import java.util.ArrayList;
import java.util.Objects;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;

/**
 *
 * @author Kent Yeh
 */
public class CustomUserInfo extends User {

    private static final long serialVersionUID = -2209416924912982094L;

    private final Member member;

    public CustomUserInfo(Member member, String roles) {
        super(member.getAccount(), member.getPasswd(), true, true, true, true, roles == null || roles.isEmpty()
                ? new ArrayList<GrantedAuthority>(0) : AuthorityUtils.commaSeparatedStringToAuthorityList(roles));
        this.member = member;
    }

    public Member getMember() {
        return member;
    }

    @Override
    public boolean isEnabled() {
        return "Y".equals(member.getEnabled());
    }

    @Override
    public String getUsername() {
        return member == null ? super.getUsername() : member.getAccount();
    }

    @Override
    public String getPassword() {
        return member == null ? super.getPassword() : member.getPasswd();
    }

    @Override
    public int hashCode() {
        int hash = 3;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final CustomUserInfo other = (CustomUserInfo) obj;
        if (!Objects.equals(this.member, other.member)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return member.toString();
    }

}
