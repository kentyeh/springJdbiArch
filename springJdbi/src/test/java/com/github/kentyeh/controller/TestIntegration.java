package com.github.kentyeh.controller;

import com.github.kentyeh.context.WebConsolLoger;
import java.io.IOException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.assertj.core.api.Assertions;
import org.htmlunit.BrowserVersion;
import org.htmlunit.CollectingAlertHandler;
import org.htmlunit.FailingHttpStatusCodeException;
import org.htmlunit.NicelyResynchronizingAjaxController;
import org.htmlunit.WebClient;
import org.htmlunit.html.HtmlForm;
import org.htmlunit.html.HtmlPage;
import org.htmlunit.html.HtmlTitle;
import org.htmlunit.javascript.SilentJavaScriptErrorListener;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

/**
 * 僅使用HtmlUnit進行整合測試。
 *
 * @author Kent Yeh
 */
@Test(groups = {"integrate"})
public class TestIntegration {

    private static final Logger logger = LogManager.getLogger(TestIntegration.class);
    private int httpPort = 80;
    private String contextPath = "";
    private WebClient webClient;
    private String captcha;
//    private HtmlPage myInfoPage;

    @BeforeClass
    @Parameters({"http.port", "contextPath", "captcha"})
    public void setup(@Optional("http.port") int httpPort,
            @Optional("contextPath") String contextPath,
            @Optional("captcha") String captcha) {
        this.httpPort = httpPort;
        this.captcha = captcha;
        logger.debug("http port is {}", httpPort);
        this.contextPath = contextPath;
        webClient = new WebClient(BrowserVersion.BEST_SUPPORTED);
        webClient.getOptions().setUseInsecureSSL(true);
        webClient.getOptions().setDownloadImages(true);//4 loading captcha
        webClient.getOptions().setThrowExceptionOnScriptError(false);//Do not throws Exception when JS run wrong.
        webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);//Do not throws Exception when HTTP status not equals 200
        webClient.getOptions().setDownloadImages(true);//Load Image owning to Captcha need.
        webClient.getOptions().setCssEnabled(false);//Enable only if JavaScript requires to judge css rule.
        webClient.setJavaScriptTimeout(10_0000);//Max seconds to wait JavsScript executed.
        webClient.getOptions().setRedirectEnabled(true);//Accept URL redirection. 
        webClient.getOptions().setTimeout(5_000);//Max mini seconds to wait page back.
        webClient.getOptions().setJavaScriptEnabled(true);//Usually enable JavaScript.
        webClient.getOptions().setFetchPolyfillEnabled(true);//Enable JavaScript support Polyfill. 
        webClient.getOptions().setWebSocketEnabled(false);//Here we don't need enable WebSocket.
        webClient.setJavaScriptErrorListener(new SilentJavaScriptErrorListener());//Skip JavaScript Error.
        webClient.setAjaxController(new NicelyResynchronizingAjaxController());//Synchronize Ajax.
        webClient.setAlertHandler(new CollectingAlertHandler());//Collect JavaScript Alert Messages.
        webClient.getWebConsole().setLogger(new WebConsolLoger());//Redirect java script console log to Log4j2.
    }

    @AfterClass
    public void tearDown() {
        if (this.webClient != null) {
            this.webClient.close();
        }
    }

    @Test(expectedExceptions = FailingHttpStatusCodeException.class)
    public void test404() throws IOException {
        String url = String.format("http://localhost:%d/%s/unknownpath/404.html", httpPort, contextPath);
        logger.debug("Integration Test: test404 with {}", url);
        HtmlPage page404 = webClient.getPage(url);
        Assertions.assertThat(page404.getWebResponse().getContentAsString())
                .contains("Page not exists");
        throw new FailingHttpStatusCodeException(page404.getWebResponse());
    }

    @Test
    public void testMyInfo() throws IOException {
        String url = String.format("http://localhost:%d/%s/user/myinfo", httpPort, contextPath);
        logger.debug("Test myinfo with {}", url);
        HtmlPage beforeInfoPage = webClient.getPage(url);
        HtmlForm form = beforeInfoPage.getFirstByXPath("//form");
        form.getInputByName("username").setValueAttribute("admin");
        form.getInputByName("password").setValueAttribute("admin");
        form.getInputByName("captcha").setValueAttribute(captcha);
        HtmlPage myInfoPage = form.getOneHtmlElementByAttribute("button", "type", "submit").click();
        HtmlTitle title = myInfoPage.getFirstByXPath("//title");
        Assertions.assertThat(title.getTextContent()).contains("admin");
    }

    @Test(dependsOnMethods = "testMyInfo")
    public void logout() throws IOException {
        String url = String.format("http://localhost:%d/%s/", httpPort, contextPath);
        logger.debug("Integration Test: logout with {}", url);
        HtmlPage homePage = webClient.getPage(url);
        HtmlForm form = homePage.getFirstByXPath("//form");
        homePage = form.getElementsByTagName("button").get(0).click();
        logger.debug("logout redirect to {}", homePage.getUrl());
        Assertions.assertThat(homePage.getUrl().toString()).contains("/index");
    }
}
