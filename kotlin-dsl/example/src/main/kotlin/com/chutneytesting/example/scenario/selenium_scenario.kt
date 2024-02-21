package com.chutneytesting.example.scenario

import com.chutneytesting.kotlin.dsl.*

fun selenium_scenario(hub: String) = Scenario(title = "Selenium scenario") {

    Given("Given the webdriver initialized") {
        SeleniumFirefoxDriverInitAction(hub = hub)
    }
    When("When navigate to Chutney url") {
        SeleniumGetAction("webDriver".spEL, url = "https://chutneyServer:8443")
    }
    Then("Then we can login") {
        Step("set username") {
            SeleniumSendKeysAction(selector = "username", by = SELENIUM_BY.id, value = "admin", wait = 10)
        }
        Step("set password") {
            SeleniumSendKeysAction(selector = "password", by = SELENIUM_BY.id, value = "admin")
        }
        Step("click login button") {
            SeleniumClickAction(
                selector = "//*[@class='login-container']//button[@type='submit']",
                by = SELENIUM_BY.xpath
            )
        }
    }
    And("And scenario page are loaded") {
        Step("Scenario menu is selected") {
            SeleniumGetAttributeAction(
                selector = "//*[@href='#/scenario']",
                by = SELENIUM_BY.xpath,
                wait = 10,
                attribute = "class",
                validations = mapOf("Scenario menu element is active" to "#outputAttributeValue.contains('active-item')".elEval())
            )
        }
        Step("current url contains /scenario") {
            AssertAction(asserts = listOf("#webDriver.getCurrentUrl().contains('/scenario')".elEval()))
        }
    }
}
