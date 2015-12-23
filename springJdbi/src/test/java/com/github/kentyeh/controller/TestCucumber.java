package com.github.kentyeh.controller;

import cucumber.api.CucumberOptions;
import cucumber.api.SnippetType;
import cucumber.api.testng.AbstractTestNGCucumberTests;

/**
 *
 * @author Kent Yeh
 */
@CucumberOptions(plugin = {"pretty", "html:target/cucumber-reports", "json:target/cucumber.json"}, snippets = SnippetType.CAMELCASE,
        features = "src/test/resources/features", glue = "com.github.kentyeh.cucumber")
public class TestCucumber extends AbstractTestNGCucumberTests {

}
