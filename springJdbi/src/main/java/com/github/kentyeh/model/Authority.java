package com.github.kentyeh.model;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.validation.constraints.NotNull;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

/**
 *
 * @author Kent Yeh
 */
public class Authority implements Serializable {

    private static final long serialVersionUID = -7454760999684175357L;
    private long aid = -1;
    @NotNull
    private String authority;
    private String account;

    public Authority() {
    }

    public Authority(int aid, String authority, String account) {
        this.aid = aid;
        this.authority = authority;
        this.account = account;
    }

    public long getAid() {
        return aid;
    }

    public void setAid(long aid) {
        this.aid = aid;
    }

    public String getAuthority() {
        return authority;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 17 * hash + (int) (this.aid ^ (this.aid >>> 32));
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
        final Authority other = (Authority) obj;
        if (this.aid != other.aid) {
            return false;
        }
        return true;
    }

    public boolean same(Authority other) {
        return other == null ? false : this.authority.equals(other.getAuthority());
    }

    public static class Authorityapper implements ResultSetMapper<Authority> {

        @Override
        public Authority map(int i, ResultSet rs, StatementContext sc) throws SQLException {
            return new Authority(rs.getInt("aid"), rs.getString("authority"), rs.getString("account"));
        }

    }
}
