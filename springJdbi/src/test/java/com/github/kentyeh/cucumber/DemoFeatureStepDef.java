package com.github.kentyeh.cucumber;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.kentyeh.manager.TestMemberManager;
import com.github.kentyeh.model.Member;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.spring.CucumberContextConfiguration;
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
@CucumberContextConfiguration
public class DemoFeatureStepDef {

    private static final Logger logger = LogManager.getLogger(DemoFeatureStepDef.class);
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private TestMemberManager memberManager;
    WebApplicationContext wac;

    @Autowired
    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Autowired
    public void setMemberManager(TestMemberManager memberManager) {
        this.memberManager = memberManager;
    }

    @Autowired
    public void setWac(WebApplicationContext wac) {
        this.wac = wac;
    }

    @Before
    public void setup() {
        this.mockMvc = webAppContextSetup(this.wac).alwaysExpect(status().isOk()).build();
    }

    @Given("^administrator has been authorized\\.$")
    public void administratorHasBeenAuthorized() throws Throwable {
        logger.debug("Administrator has been authorized.");
    }

    @When("^administrator click to view all users' infomation\\.$")
    public void administratorClickToViewAllUsersInfomation() throws Throwable {
        logger.debug("Administrator click to view all users' infomation");
    }

    @Then("^return headcount should equals all user's amount\\.$")
    public void testUsersInfo() throws Exception {
        MvcResult mvcResult = mockMvc.perform(post("/admin/users").with(user("admin")
                .roles("ADMIN"))).andDo(print()).andReturn();
        Member[] members = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), Member[].class);
        Assertions.assertThat(members.length).isEqualTo(memberManager.countUsers());
    }

    @Given("^acouunt user \"([^\"]*)\" has already been authorized\\.$")
    public void acouuntUserHasAlreadyBeenAuthorized(String user) throws Throwable {
        logger.debug("Acouunt user \"{}\" has already been authorized.", user);
    }

    @When("^\"([^\"]*)\" click MyInfo anchor\\.$")
    public void clickMyInfoAnchor(String user) throws Throwable {
        logger.debug("\"{}\" click MyInfo anchor.", user);
    }

    @Then("^display personal info to \"([^\"]*)\"$")
    public void displayPersonalInfoTo(String user) throws Throwable {
        MvcResult mvcResult = mockMvc.perform(post("/user/myinfo").principal(new TestingAuthenticationToken(user, null))).andDo(print()).andReturn();
        Member member = (Member) mvcResult.getRequest().getAttribute("member");
        Assertions.assertThat(member.getAccount()).isEqualTo(user);
    }

}
