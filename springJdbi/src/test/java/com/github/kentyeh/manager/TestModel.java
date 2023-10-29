package com.github.kentyeh.manager;

import com.github.kentyeh.model.Member;
import java.lang.annotation.AnnotationFormatError;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.assertj.core.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
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
public class TestModel extends AbstractTestNGSpringContextTests {

    private static final Logger logger = LogManager.getLogger(TestModel.class);
    private PasswordEncoder encoder;
    private TestMemberManager memberManager;

    @Autowired
    public void setEncoder(PasswordEncoder encoder) {
        this.encoder = encoder;
    }

    @Autowired
    public void setMemberManager(TestMemberManager memberManager) {
        this.memberManager = memberManager;
    }

    @Test(expectedExceptions = {RuntimeException.class, AnnotationFormatError.class})
    public void testDuplicateMember() throws Exception {
        Member member = new Member("admin", "admin");
        member.setPassword("xxxx");
        memberManager.newMember(member);
    }

    @Test(expectedExceptions = {RuntimeException.class, AnnotationFormatError.class})
    public void testWrongMember() throws Exception {
        Member member = new Member();
        member.setAccount("someone");
        memberManager.newMember(member);
    }

    @Test
    public void testNewMember() throws Exception {
        Member member = new Member("newbie", "newbie");
        member.setPassword("newbie");
        member.setBirthday(new java.util.Date());
        memberManager.newMember(member);
        member = memberManager.findByPrimaryKey("newbie");
        Assertions.assertThat(member).isNotNull();

    }

    @Test
    public void testUpdateMember() throws Exception {
        Member member = memberManager.findByPrimaryKey("newbie");
        Assertions.assertThat(member).isNotNull();
        member.setName("Junior");
        member.setPassword("HelloWorld!");
        Assertions.assertThat(memberManager.updateMember(member));
        member = memberManager.findByPrimaryKey("newbie");
        Assertions.assertThat(member.getName()).isEqualTo("Junior");
        Assertions.assertThat(encoder.matches("HelloWorld!", member.getPassword()));
    }

    @Test
    public void testRollback() throws Exception {
        Member member = memberManager.findByPrimaryKey("admin");
        String orignPass = member.getPassword();
        try {
            memberManager.raiseRollback(member);
        } catch (RuntimeException e) {
            logger.error(e.getMessage(), e);
        }
        member = memberManager.findByPrimaryKey("admin");
        Assertions.assertThat(member.getPassword()).isEqualTo(orignPass);
    }
}
