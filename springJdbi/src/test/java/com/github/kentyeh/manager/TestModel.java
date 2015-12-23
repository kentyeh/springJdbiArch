package com.github.kentyeh.manager;

import com.github.kentyeh.model.Member;
import lombok.extern.log4j.Log4j2;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.context.web.WebAppConfiguration;
import org.testng.annotations.Test;

/**
 *
 * @author Kent Yeh
 */
@WebAppConfiguration
@ContextConfiguration(classes = com.github.kentyeh.context.TestContext.class)
@Log4j2
public class TestModel extends AbstractTestNGSpringContextTests {

    @Autowired
    private TestMemberManager memberManager;

    @Test(expectedExceptions = RuntimeException.class)
    public void testDuplicateMember() throws Exception {
        Member member = new Member("admin", "admin");
        member.setPasswd("xxxx");
        memberManager.newMember(member);
    }

    @Test(expectedExceptions = RuntimeException.class)
    public void testWrongMember() throws Exception {
        Member member = new Member();
        member.setAccount("someone");
        memberManager.newMember(member);
    }

    @Test
    public void testRollback() throws Exception {
        Member member = memberManager.findMemberByPrimaryKey("admin");
        String orignPass = member.getPasswd();
        try {
            memberManager.raiseRollback(member);
        } catch (RuntimeException e) {
            log.error(e.getMessage(), e);
        }
        member = memberManager.findMemberByPrimaryKey("admin");
        assertThat("DB not rollback!", member.getPasswd(), is(equalTo(orignPass)));
    }
}
