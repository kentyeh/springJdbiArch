package com.github.kentyeh.manager;

import com.github.kentyeh.model.Member;
import com.github.kentyeh.model.TestDao;
import java.util.List;
import org.jdbi.v3.core.JdbiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Kent Yeh
 */
@Repository("testMemberManager")
public class TestMemberManager extends MemberManager {

    private JdbcTemplate jdbcTemplate;
    private PasswordEncoder encoder;

    @Autowired
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Autowired
    public void setEncoder(PasswordEncoder encoder) {
        this.encoder = encoder;
    }

    @Transactional(propagation = Propagation.SUPPORTS, rollbackFor = JdbiException.class)
    public int countUsers() throws Exception {
        try (TestDao dao = getContext().getBean(TestDao.class)) {
            return dao.countUsers();
        }
    }

    @Transactional(propagation = Propagation.SUPPORTS, rollbackFor = JdbiException.class)
    public int countAdminOrUser(List<String> authoritues) {
        try (TestDao dao = getContext().getBean(TestDao.class)) {
            return dao.countAdminOrUser(authoritues);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void raiseRollback(Member member) throws Exception {
        try (TestDao dao = getContext().getBean(TestDao.class)) {
            dao.changePasswd(member.getAccount(), member.getPassword(),
                    encoder.encode("guesspass"));
            member.setName(null);
            dao.updateMember(member);
        }
    }
}
