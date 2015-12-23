package com.github.kentyeh.model;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Data;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

/**
 *
 * @author Kent Yeh
 */
@Data
@EqualsAndHashCode(of = "aid", callSuper = false)
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
