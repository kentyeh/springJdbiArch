package com.github.kentyeh.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

/**
 *
 * @author Kent Yeh
 */
public class Member{

    @NotNull(message = "{com.github.kentyeh.model.Member.account.notNull.message}")
    @Size(min = 1, message = "{com.github.kentyeh.model.Member.account.notEmpty.message}")
    private String account;

    @NotNull(message = "{com.github.kentyeh.model.Member.passwd.notNull.message}")
    @Size(min = 1, message = "{com.github.kentyeh.model.Member.passwd.notEmpty.message}")
    private String password;

    @NotNull(message = "{com.github.kentyeh.model.Member.name.notNull.message}")
    @Size(min = 1, message = "{com.github.kentyeh.model.Member.name.notEmpty.message}")
    private String name;

    private String enabled = "Y";

    private Date birthday;

    private List<Authority> authorities;

    public Member() {
    }

    public Member(String account, String name) {
        this.account = account;
        this.name = name;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEnabled() {
        return "Y".equals(enabled) ? "Y" : "N";
    }

    public void setEnabled(String enabled) {
        this.enabled = enabled;
    }

    public String getFormattedBirthday() {
        return birthday == null ? "" : String.format("%1$tY/%1$tm/%1$td", birthday);
    }

    public Date getBirthday() {
        return birthday == null ? null : new Date(birthday.getTime());
    }

    public void setBirthday(Date birthday) {
        if (birthday == null) {
            this.birthday = null;
        } else if (this.birthday == null) {
            this.birthday = new Date(birthday.getTime());
        } else {
            this.birthday.setTime(birthday.getTime());
        }
    }

    public List<Authority> getAuthorities() {
        if (authorities == null) {
            authorities = new ArrayList<>();
        }
        return authorities;
    }

    public void setAuthorities(List<Authority> authorities) {
        if (authorities == null || authorities.isEmpty()) {
            if (this.authorities != null) {
                this.authorities.clear();
            }
        } else {
            if (this.authorities == null) {
                this.authorities = new ArrayList<>(authorities.size());
            } else if (!this.authorities.isEmpty()) {
                this.authorities.clear();
            }
            this.authorities.addAll(authorities);
        }
    }

    public String getRoles() {
        if (authorities == null || authorities.isEmpty()) {
            return "";
        } else {
            StringBuilder sb = new StringBuilder();
            for (Authority authority : getAuthorities()) {
                sb.append(sb.isEmpty() ? "" : ",").append(authority.getAuthority());
            }
            return sb.toString();
        }
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.account);
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
        final Member other = (Member) obj;
        return Objects.equals(this.account, other.account);
    }

    @Override
    public String toString() {
        return String.format("%s[%s]:%s", name, account, password);
    }

    public static class MemberMapper implements RowMapper<Member> {

        @Override
        public Member map(ResultSet rs, StatementContext ctx) throws SQLException {
            Member res = new Member(rs.getString("account"), rs.getString("name"));
            res.setPassword(rs.getString("passwd"));
            res.setEnabled(rs.getString("enabled"));
            res.setBirthday(rs.getDate("birthday"));
            return res;
        }

    }
}
