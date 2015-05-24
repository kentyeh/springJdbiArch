package com.github.kentyeh.manager;

import com.github.kentyeh.model.Member;
import com.github.kentyeh.model.TestDao;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Kent Yeh
 */
@Repository("testMemberManager")
public class TestMemberManager extends MemberManager {

    private static final Logger logger = LogManager.getLogger(TestMemberManager.class);
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Transactional(propagation = Propagation.SUPPORTS, rollbackFor = Exception.class)
    public int countUsers() throws Exception {
        return getContext().getBean(TestDao.class).countUsers();
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void raiseRollback(Member member) throws Exception {
        TestDao dao = getContext().getBean(TestDao.class);
        dao.changePasswd(member.getAccount(), member.getPasswd(), "guesspass");
        member.setName(null);
        dao.updateMember(member);
    }
}
