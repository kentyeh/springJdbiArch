package com.github.kentyeh.model;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

/**
 *
 * @author Kent Yeh
 */
public class Member implements Serializable {

    private static final long serialVersionUID = 395368712192880218L;
   
    @NotNull(message = "{com.github.kentyeh.model.Member.account.notNull.message}")
    @Size(min = 1, message = "{com.github.kentyeh.model.Member.account.notEmpty.message}")
    private String account;
    @NotNull(message = "{com.github.kentyeh.model.Member.passwd.notNull.message}")
    @Size(min = 1, message = "{com.github.kentyeh.model.Member.passwd.notEmpty.message}")
    private String passwd;
    @NotNull(message = "{com.github.kentyeh.model.Member.name.notNull.message}")
    @Size(min = 1, message = "{com.github.kentyeh.model.Member.name.notEmpty.message}")
    private String name;
    private String enabled = "Y";
    private Date birthday;
    private transient List<Authority> authorities;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPasswd() {
        return passwd;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }

    public String getEnabled() {
        return "Y".equals(enabled) ? "Y" : "N";
    }

    public void setEnabled(String enabled) {
        this.enabled = enabled;
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
        if(authorities==null){
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
            } else {
                this.authorities.clear();
            }
            this.authorities.addAll(authorities);
        }
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
        return this.account.equals(other.account);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 29 * hash + this.account.hashCode();
        return hash;
    }

    @Override
    public String toString() {
        return String.format("%s[%s]", name, account);
    }

    public static class MemberMapper implements ResultSetMapper<Member> {

        @Override
        public Member map(int i, ResultSet rs, StatementContext sc) throws SQLException {
            Member res = new Member();
            res.setAccount(rs.getString("account"));
            res.setName(rs.getString("name"));
            res.setPasswd(rs.getString("passwd"));
            res.setEnabled(rs.getString("enabled"));
            res.setBirthday(rs.getDate("birthday"));
            return res;
        }

    }
}
