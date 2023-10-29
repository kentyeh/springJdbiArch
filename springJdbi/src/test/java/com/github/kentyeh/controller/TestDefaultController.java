package com.github.kentyeh.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.kentyeh.manager.TestMemberManager;
import com.github.kentyeh.model.Member;
import java.security.Principal;
import java.util.Arrays;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.assertj.core.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.TestingAuthenticationToken;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 *
 * @author kent
 */
@WebAppConfiguration
@ContextConfiguration(classes = com.github.kentyeh.context.TestContext.class)
public class TestDefaultController extends AbstractTestNGSpringContextTests {

    private static final Logger logger = LogManager.getLogger(TestDefaultController.class);
    private WebApplicationContext wac;
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private TestMemberManager memberManager;

    @Autowired
    public void setWac(WebApplicationContext wac) {
        this.wac = wac;
    }

    @Autowired
    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Autowired
    public void setMemberManager(TestMemberManager memberManager) {
        this.memberManager = memberManager;
    }

    @BeforeClass
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    @Test
    public void testListuser() throws Exception {
        MvcResult mvcResult = mockMvc.perform(post("/admin/users")
                .with(user("admin").roles("ADMIN"))).andDo(print())
                .andReturn();
        Member[] members = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), Member[].class);
        Assertions.assertThat(members.length).isEqualTo(memberManager.countUsers());
    }

    @Test
    public void testListAdminOrUser() throws Exception {
        Principal principal = wac.getBean(Principal.class, "admin");
        MvcResult mvcResult = mockMvc.perform(post("/admin/adminOrUsers").principal(principal)).andDo(print())
                .andExpect(status().isOk()).andReturn();
        Member[] members = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), Member[].class);
        Assertions.assertThat(members.length).isEqualTo(memberManager.countAdminOrUser(Arrays.asList(new String[]{"ROLE_ADMIN", "ROLE_USER"})));
    }

    @Test
    public void testMyinfo() throws Exception {
        MvcResult mvcResult = mockMvc.perform(post("/user/myinfo")
                .principal(new TestingAuthenticationToken("admin", "admin", "ROLE_ADMIN"))).andReturn();
        Member member = (Member) mvcResult.getRequest().getAttribute("member");
        if (member != null) {
            Assertions.assertThat(member.getAccount()).isEqualTo("admin");
        } else {
            throw new RuntimeException("Myinfo not found!");
        }
    }

    @Test
    public void testUserInfo() throws Exception {
        MvcResult mvcResult = mockMvc.perform(post("/admin/user/{account}", "admin").with(user("admin").roles("ADMIN"))).andReturn();
        Member member = (Member) mvcResult.getRequest().getAttribute("member");
        logger.debug("account \"{}\" name is {}", member.getAccount(), member.getName());
        Assertions.assertThat(member.getAccount()).isEqualTo(member.getAccount());
    }
}
