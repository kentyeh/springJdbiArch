package com.github.kentyeh.cucumber;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.kentyeh.manager.TestMemberManager;
import com.github.kentyeh.model.Member;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.Arrays;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.assertj.core.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.TestingAuthenticationToken;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;
import org.springframework.web.context.WebApplicationContext;

/**
 *
 * @author Kent Yeh
 */
@WebAppConfiguration
@ContextConfiguration(classes = com.github.kentyeh.context.TestContext.class)
public class DemoFeatureTwStepDef {

    private static final Logger logger = LogManager.getLogger(DemoFeatureTwStepDef.class);

    protected WebApplicationContext wac;
    private ObjectMapper objectMapper;

    private TestMemberManager memberManager;
    private MockMvc mockMvc;

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

    @Before
    public void setup() {
        this.mockMvc = webAppContextSetup(this.wac).alwaysExpect(status().isOk()).build();
    }

    @Given("^系統人員已完成登錄$")
    public void adminAuthoritied() throws Exception {
        logger.debug("系統人員已完成登錄");
    }

    @When("^系統人員點選查詢所有人員資訊$")
    public void adminWhen() {
        logger.debug("系統人員點選查詢所有人員資訊");
    }

    @Then("^顯示人員數應與所有人員數相等$")
    public void testUsersInfo() throws Exception {
        MvcResult mvcResult = mockMvc.perform(post("/admin/users")
                .with(user("admin").roles("ADMIN")))
                .andDo(print()).andReturn();
        Member[] members = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), Member[].class);
        Assertions.assertThat(members.length).isEqualTo(memberManager.countUsers());
    }

    @Given("^用戶\"([^\"]*)\"已完成登錄$")
    public void alreadyLogin(String user) throws Throwable {
        logger.debug("{}已完成登錄", user);
    }

    @When("^\"([^\"]*)\"點選我的個人資料$")
    public void clickMyInfo(String user) throws Throwable {
        logger.debug("{}點選我的個人資料$", user);
    }

    @Then("^顯示\"([^\"]*)\"的個資$")
    public void viewMyIno(String user) throws Throwable {
        MvcResult mvcResult = mockMvc.perform(post("/user/myinfo").principal(new TestingAuthenticationToken(user, null))).andDo(print()).andReturn();
        Member member = (Member) mvcResult.getRequest().getAttribute("member");
        Assertions.assertThat(member.getAccount()).isEqualTo(user);
    }

}
