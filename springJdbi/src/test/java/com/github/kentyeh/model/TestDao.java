package com.github.kentyeh.model;

import java.util.List;
import org.skife.jdbi.v2.sqlobject.SqlQuery;

/**
 *
 * @author Kent Yeh
 */
public interface TestDao extends Dao {

    @SqlQuery("SELECT count(8) FROM appmember")
    int countUsers();
    
    @SqlQuery("SELECT count(8) FROM appmember WHERE EXISTS(SELECT 1 FROM authorities"
            + " WHERE authorities.account=appmember.account AND ARRAY_CONTAINS( :auths ,authority) )")
    int countAdminOrUser(@BindStringList("auths") List<String> authoritues);
}
