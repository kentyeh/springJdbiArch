package com.github.kentyeh.model;

import java.util.Collection;
import java.util.List;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.GetGeneratedKeys;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;
import org.skife.jdbi.v2.sqlobject.stringtemplate.UseStringTemplate3StatementLocator;
import org.skife.jdbi.v2.unstable.BindIn;

/**
 *
 * @author Kent Yeh
 */
@UseStringTemplate3StatementLocator //For In query syntax
public interface Dao extends AutoCloseable{

    @SqlQuery("SELECT * FROM appmember WHERE account = :account")
    @RegisterMapper(Member.MemberMapper.class)
    Member findMemberByPrimaryKey(@Bind("account") String account);

    @SqlQuery("SELECT * FROM appmember WHERE enabled = 'Y' ORDER BY account")
    @RegisterMapper(Member.MemberMapper.class)
    List<Member> findAvailableUsers();
    
    @SqlQuery("SELECT * FROM appmember ORDER BY account")
    @RegisterMapper(Member.MemberMapper.class)
    List<Member> findAllUsers();

    @SqlQuery("SELECT * FROM authorities WHERE account = :account")
    @RegisterMapper(Authority.Authorityapper.class)
    List<Authority> findAuthorityByAccount(@Bind("account") String account);

    @SqlUpdate("UPDATE appmember SET passwd = :newPass WHERE account = :account AND passwd= :oldPass")
    int changePasswd(@Bind("account") String account, @Bind("oldPass") String oldPass, @Bind("newPass") String newPass);

    @SqlUpdate("INSERT INTO appmember(account,name,passwd,enabled,birthday)"
            + "values( :account , :name , :passwd , :enabled , :birthday )")
    void newMember(@BindBean Member member);

    @SqlUpdate("UPDATE appmember SET name= :name ,enabled= :enabled ,birthday= :birthday WHERE account= :account")
    int  updateMember(@BindBean Member member);
    
    @SqlQuery("SELECT passwd FROM appmember WHERE account= :account")
    String getPasswd(@Bind("account") String account);

    @SqlUpdate("DELETE FROM appmember WHERE account= :account")
    int removeMember(@Bind("account") String account);

    @SqlQuery("SELECT * FROM authorities WHERE account = :account AND authority= :authority")
    @RegisterMapper(Authority.Authorityapper.class)
    Authority findAuthorityByBean(@BindBean Authority authority);

    @SqlUpdate("INSERT INTO authorities(account,authority) values( :account, :authority)")
    @GetGeneratedKeys
    long newAuthority(@BindBean Authority authority);

    @SqlUpdate("DELETE FROM authorities WHERE account = :account")
    int removeAuthories(@Bind("account") String account);

    @SqlUpdate("DELETE FROM authorities WHERE account = :account AND authority not in ( <authorities> )")
    int removeAuthories(@Bind("account") String account, @BindIn("authorities") Collection<String> authorities);

    @SqlQuery("select count(8) from information_schema.sessions")
    int countSessions();
}
