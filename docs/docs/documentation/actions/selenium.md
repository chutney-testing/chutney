??? info "Browse implementations"

    - [Click](https://github.com/chutney-testing/chutney/blob/master/action-impl/src/main/java/com/chutneytesting/action/selenium/SeleniumClickAction.java){:target="_blank"}
    - [Close](https://github.com/chutney-testing/chutney/blob/master/action-impl/src/main/java/com/chutneytesting/action/selenium/SeleniumCloseAction.java){:target="_blank"}
    - [Driver Init](https://github.com/chutney-testing/chutney/blob/master/action-impl/src/main/java/com/chutneytesting/action/selenium/SeleniumDriverInitAction.java){:target="_blank"}
    - [Get](https://github.com/chutney-testing/chutney/blob/master/action-impl/src/main/java/com/chutneytesting/action/selenium/SeleniumGetAction.java){:target="_blank"}
    - [Get Attribute](https://github.com/chutney-testing/chutney/blob/master/action-impl/src/main/java/com/chutneytesting/action/selenium/SeleniumGetAttributeAction.java){:target="_blank"}
    - [Get Text](https://github.com/chutney-testing/chutney/blob/master/action-impl/src/main/java/com/chutneytesting/action/selenium/SeleniumGetTextAction.java){:target="_blank"}
    - [Hover ThenClick](https://github.com/chutney-testing/chutney/blob/master/action-impl/src/main/java/com/chutneytesting/action/selenium/SeleniumHoverThenClickAction.java){:target="_blank"}
    - [Quit](https://github.com/chutney-testing/chutney/blob/master/action-impl/src/main/java/com/chutneytesting/action/selenium/SeleniumQuitAction.java){:target="_blank"}
    - [Remote Driver Init](https://github.com/chutney-testing/chutney/blob/master/action-impl/src/main/java/com/chutneytesting/action/selenium/SeleniumRemoteDriverInitAction.java){:target="_blank"}
    - [Screenshot](https://github.com/chutney-testing/chutney/blob/master/action-impl/src/main/java/com/chutneytesting/action/selenium/SeleniumScreenShotAction.java){:target="_blank"}
    - [Scroll To](https://github.com/chutney-testing/chutney/blob/master/action-impl/src/main/java/com/chutneytesting/action/selenium/SeleniumScrollToAction.java){:target="_blank"}
    - [Send Keys](https://github.com/chutney-testing/chutney/blob/master/action-impl/src/main/java/com/chutneytesting/action/selenium/SeleniumSendKeysAction.java){:target="_blank"}
    - [Set Browser Size](https://github.com/chutney-testing/chutney/blob/master/action-impl/src/main/java/com/chutneytesting/action/selenium/SeleniumSetBrowserSizeAction.java){:target="_blank"}
    - [Switch To](https://github.com/chutney-testing/chutney/blob/master/action-impl/src/main/java/com/chutneytesting/action/selenium/SeleniumSwitchToAction.java){:target="_blank"}
    - [Wait](https://github.com/chutney-testing/chutney/blob/master/action-impl/src/main/java/com/chutneytesting/action/selenium/SeleniumWaitAction.java){:target="_blank"}

!!! important "About `by` input"

    Selenium actions selecting an elements require a selector and specifying the selection type.  
    See Selenium [by](https://www.selenium.dev/selenium/docs/api/java/org/openqa/selenium/By.html){:target="_blank"} documentation for further details.  
    Chutney provides the following values :
    
    * "id" 
    * "name"
    * "className"
    * "cssSelector"
    * "xpath"
    * "zk"

    ??? info "About ZK"
    
        [ZK](https://www.zkoss.org/){:target="_blank"} is an open source Java framework for building web and mobile apps.  
        When using ZK, element IDs are gerenated and managed by the framework. Thus, impossible to know beforehand.  
        But you can get HTML IDs by calling ZK at runtime [with JS using the widget's ID](https://www.zkoss.org/wiki/ZK_Developer%27s_Reference/Testing/Testing_Tips){:target="_blank"}.  
        So, Chutney provides it for you.


!!! important "About `web-driver` input"

    Most actions requires a `web-driver` input. It's value comes from the output off the action [DriverInit](#driverInit).  
    So the most probable value for it would come from the execution context : `${#webDriver}`

    **While required in a Chutney scenario, it is set by default using the Chutney Kotlin DSL so you don't need to provide it.**

# Click

!!! info "[Browse Implementation](https://github.com/chutney-testing/chutney/blob/master/action-impl/src/main/java/com/chutneytesting/action/selenium/SeleniumClickAction.java){:target="_blank"}"

Performs a click on an element. Element is expected to be clickable.  
See [WebElement.click()](https://www.selenium.dev/selenium/docs/api/java/org/openqa/selenium/WebElement.html#click()){:target="_blank"}
and [ExpectedConditions.elementToBeClickable()](https://www.selenium.dev/selenium/docs/api/java/org/openqa/selenium/support/ui/ExpectedConditions.html#elementToBeClickable(org.openqa.selenium.By)){:target="_blank"} for further details.

It takes a screenshot in case of error.

=== "Inputs"

    | Required | Name         | Type    | Default |  Note   |
    |:--------:|:-------------|:--------|:-------:|:-------:|
    |    *     | `web-driver` | String  |         |         |
    |    *     | `selector`   | String  |         |         |
    |    *     | `by`         | String  |         |         |
    |          | `wait`       | Integer |   1     | seconds |


### Example

=== "Kotlin"
    ``` kotlin
    SeleniumClickAction(
        selector = "//button[text()=\"Some text\"",
        by = SELENIUM_BY.xpath
    )
    ```

# Close

!!! info "[Browse Implementation](https://github.com/chutney-testing/chutney/blob/master/action-impl/src/main/java/com/chutneytesting/action/selenium/SeleniumCloseAction.java){:target="_blank"}"

This action close the current window, quitting the browser if it's the last window currently open.  
See [WebDriver.close()](https://www.selenium.dev/selenium/docs/api/java/org/openqa/selenium/WebDriver.html#close()){:target="_blank"} for further details.

=== "Inputs"

    | Required | Name         | Type   | Default |
    |:--------:|:-------------|:-------|:-------:|
    |    *     | `web-driver` | String |         |


### Example

=== "Kotlin"
    ``` kotlin
    SeleniumCloseAction()
    ```

# DriverInit

!!! info "[Browse Implementation](https://github.com/chutney-testing/chutney/blob/master/action-impl/src/main/java/com/chutneytesting/action/selenium/SeleniumDriverInitAction.java){:target="_blank"}"

This action instantiate a webdriver

!!! important
    Firefox is the default browser and `browser` input should be empty, but you should provide `browserPath` input.  
    For using IE, put "Internet Explorer" as `browser` input, `browserPath` is not required when using IE.

    ??? note "IE running options"
        IE runs with the following options :

        - "AcceptInsecureCertificates": true
        - "disable-popup-blocking": true
        - "enablePersistentHover": true
        - "ensureCleanSession": true
        - "ignoreProtectedModeSettings": true
        - "ignoreZoomSetting": false
        - "introduceInstabilityByIgnoringProtectedModeSettings": true
        - "javascriptEnabled": true
        - "nativeEvents": true
        - "unexpectedAlertBehaviour": "accept"

    ??? note "FF running options"
        FF runs with the following options :

        - Headless: true
        - Log level: FATAL

=== "Inputs"

    | Required | Name          | Type     | Default |  Accepted Value   |
    |:--------:|:--------------|:---------|:-------:|:-----------------:|
    |    *     | `driverPath`  | String   |         |                   |
    |    *     | `browserPath` | String   |         |                   |
    |          | `browser`     | String   | Firefox | Internet Explorer |

=== "Outputs"

    |      Name   | Type                                                                                                              |
    |:-----------:|:------------------------------------------------------------------------------------------------------------------|
    | `webDriver` | [WebDriver](https://www.selenium.dev/selenium/docs/api/java/org/openqa/selenium/WebDriver.html){:target="_blank"} |

### Finally Action

Performs the action [Quit](#quit) when the scenario ends.

### Example

=== "Kotlin"
    ``` kotlin
    SeleniumDriverInitAction(
        browserPath = "/path/to/the/browser/",
        driverPath = "/path/to/the/webdriver/"
    )
    ```

# Get

!!! info "[Browse Implementation](https://github.com/chutney-testing/chutney/blob/master/action-impl/src/main/java/com/chutneytesting/action/selenium/SeleniumGetAction.java){:target="_blank"}"

This action load a new web page in the current browser window.  
See [WebDriver.get(String url)](https://www.selenium.dev/selenium/docs/api/java/org/openqa/selenium/WebDriver.html#get(java.lang.String)){:target="_blank"} for further details.

=== "Inputs"

    | Required | Name         | Type    | Note                                           |
    |:--------:|:-------------|:--------|:----------------------------------------------:|
    |    *     | `web-driver` | String  |                                                |
    |          | `selector`   | String  | (Weird implemention) Used as a boolean. If null, the page is opened in current window. If not the page is opened in a new window |
    |    *     | `value`      | String  | The URL to load. Must be a fully qualified URL |

=== "Outputs"

    |     Name    | Type   | Note                                                                                                                                                                                                                                                                    |
    |:-----------:|:-------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
    | `outputGet` | String | This value can be used to switch to this window using action [SwitchTo](#switch-to).<br> See [WebDriver.getWindowHandle()](https://www.selenium.dev/selenium/docs/api/java/org/openqa/selenium/WebDriver.html#getWindowHandle()){:target="_blank"} for further details. |


### Example

=== "Kotlin"
    ``` kotlin
    SeleniumGetAction(
        url = "http://www.chutney-testing.com/" 
    )
    ```

# GetAttribute

!!! info "[Browse Implementation](https://github.com/chutney-testing/chutney/blob/master/action-impl/src/main/java/com/chutneytesting/action/selenium/SeleniumGetAttributeAction.java){:target="_blank"}"

Get the value of the given attribute of the element.  
See [WebElement.getAttribute()](https://www.selenium.dev/selenium/docs/api/java/org/openqa/selenium/WebElement.html#getAttribute(java.lang.String)){:target="_blank"} for further details.

=== "Inputs"

    | Required | Name         | Type    | Default |  Note   |
    |:--------:|:-------------|:--------|:-------:|:-------:|
    |    *     | `web-driver` | String  |         |         |
    |    *     | `selector`   | String  |         |         |
    |    *     | `by`         | String  |         |         |
    |          | `wait`       | Integer |   1     | seconds |
    |          | `attribute`  | String  | "value" |         |

=== "Outputs"

    |         Name           | Type   |
    |:----------------------:|:-------|
    | `outputAttributeValue` | String |

### Example

=== "Kotlin"
    ``` kotlin
    SeleniumGetAttributeAction(
        selector = "usernameInput",
        by = SELENIUM_BY.NAME,
        attribute = "type"
    )
    ```

# GetText

!!! info "[Browse Implementation](https://github.com/chutney-testing/chutney/blob/master/action-impl/src/main/java/com/chutneytesting/action/selenium/SeleniumGetTextAction.java){:target="_blank"}"

This action returns the visible (i.e. not hidden by CSS) text of an element, including sub-elements.  
See [WebElement.getText()](https://www.selenium.dev/selenium/docs/api/java/org/openqa/selenium/WebElement.html#getText()){:target="_blank"} for further details.

=== "Inputs"

    | Required | Name         | Type    | Default |  Note   |
    |:--------:|:-------------|:--------|:-------:|:-------:|
    |    *     | `web-driver` | String  |         |         |
    |    *     | `selector`   | String  |         |         |
    |    *     | `by`         | String  |         |         |
    |          | `wait`       | Integer |   1     | seconds |

=== "Outputs"

    |      Name       | Type   | Note                                                      |
    |:---------------:|:-------|-----------------------------------------------------------|
    | `outputGetText` | String | Returns the value of attribute "value" when text is empty |

### Example

=== "Kotlin"
    ``` kotlin
    SeleniumGetTextAction(
        selector = "usernameInput",
        by = SELENIUM_BY.NAME,
    )
    ```

# Hover and click

!!! info "[Browse Implementation](https://github.com/chutney-testing/chutney/blob/master/action-impl/src/main/java/com/chutneytesting/action/selenium/SeleniumHoverThenClickAction.java){:target="_blank"}"

Moves the mouse to the middle of the element then performs a click.  
See [Actions.moveToElement()](https://www.selenium.dev/selenium/docs/api/java/org/openqa/selenium/interactions/Actions.html#moveToElement(org.openqa.selenium.WebElement)){:target="_blank"} and [Click](#click) for further details.

=== "Inputs"

    | Required | Name         | Type    | Default |  Note   |
    |:--------:|:-------------|:--------|:-------:|:-------:|
    |    *     | `web-driver` | String  |         |         |
    |    *     | `selector`   | String  |         |         |
    |    *     | `by`         | String  |         |         |
    |          | `wait`       | Integer |   1     | seconds |

### Example

=== "Kotlin"
    ``` kotlin
    SeleniumHoverThenClickAction(
        selector = "myHyperLink",
        by = SELENIUM_BY.id
    )
    ```

# Quit

!!! info "[Browse Implementation](https://github.com/chutney-testing/chutney/blob/master/action-impl/src/main/java/com/chutneytesting/action/selenium/SeleniumQuitAction.java){:target="_blank"}"

!!! important 
    This action is not available with the Kotlin DSL since it is performed by default when the scenario ends.

This action quits the driver, closing every associated window.  
See [WebDriver.quit()](https://www.selenium.dev/selenium/docs/api/java/org/openqa/selenium/WebDriver.html#quit()){:target="_blank"} for further details.

=== "Inputs"

    | Required | Name         | Type   | Default |
    |:--------:|:-------------|:-------|:-------:|
    |    *     | `web-driver` | String |         |


# Remote Driver Init

!!! info "[Browse Implementation](https://github.com/chutney-testing/chutney/blob/master/action-impl/src/main/java/com/chutneytesting/action/selenium/SeleniumRemoteDriverInitAction.java){:target="_blank"}"

!!! note
    `browser` input values :

    - "chrome"
    - "internet explorer"
    - "firefox"

??? note "Internet Explorer running options"
    IE runs with the following options :

    - "AcceptInsecureCertificates": true
    - "disable-popup-blocking": true
    - "enablePersistentHover": true
    - "ensureCleanSession": true
    - "ignoreProtectedModeSettings": true
    - "ignoreZoomSetting": false
    - "introduceInstabilityByIgnoringProtectedModeSettings": true
    - "javascriptEnabled": true
    - "nativeEvents": true
    - "unexpectedAlertBehaviour": "accept"

??? note "Firefox running options"
    Firefox runs with the following options :

    - Headless: false
    - Log level: FATAL

??? note "Chrome running options"
    Chrome runs with the following options :

    - start-maximized: true

=== "Inputs"

    | Required | Name      | Type          | Default | Note                               |
    |:--------:|:----------|:--------------|:-------:|------------------------------------|
    |    *     | `hub`     | String        |         | The URL of the remote Selenium Hub |
    |          | `browser` | String        | firefox |                                    |

=== "Outputs"

    |      Name   | Type                                                                                                              |
    |:-----------:|:------------------------------------------------------------------------------------------------------------------|
    | `webDriver` | [WebDriver](https://www.selenium.dev/selenium/docs/api/java/org/openqa/selenium/WebDriver.html){:target="_blank"} |


### Finally Action

Performs the action [Quit](#quit) when the scenario ends.

### Example

=== "Kotlin"
    ``` kotlin
    SeleniumRemoteDriverInitAction(
        hub = "http://my.hub.url:4242/",
        browser = "chrome"
    )
    ```

# Screenshot

!!! info "[Browse Implementation](https://github.com/chutney-testing/chutney/blob/master/action-impl/src/main/java/com/chutneytesting/action/selenium/SeleniumScreenShotAction.java){:target="_blank"}"

Takes a screenshot, available in the execution report as a Base64 String.  
See [TakesScreenshot.getScreenshotAs()](https://www.selenium.dev/selenium/docs/api/java/org/openqa/selenium/TakesScreenshot.html#getScreenshotAs(org.openqa.selenium.OutputType)){:target="_blank"}
and [OutputType.BASE64](https://www.selenium.dev/selenium/docs/api/java/org/openqa/selenium/OutputType.html#BASE64){:target="_blank"} for further details.

=== "Inputs"

    | Required | Name         | Type    |
    |:--------:|:-------------|:--------|
    |    *     | `web-driver` | String  |


### Example

=== "Kotlin"
    ``` kotlin
    SeleniumScreenShotAction()
    ```

# Scroll to

!!! info "[Browse Implementation](https://github.com/chutney-testing/chutney/blob/master/action-impl/src/main/java/com/chutneytesting/action/selenium/SeleniumScrollToAction.java){:target="_blank"}"

Scroll the page to make the element visible and align to the top.  
See [Element.scrollIntoView()](https://developer.mozilla.org/en-US/docs/Web/API/Element/scrollIntoView){:target="_blank"} for further details.

=== "Inputs"

    | Required | Name         | Type    | Default |  Note   |
    |:--------:|:-------------|:--------|:-------:|:-------:|
    |    *     | `web-driver` | String  |         |         |
    |    *     | `selector`   | String  |         |         |
    |    *     | `by`         | String  |         |         |
    |          | `wait`       | Integer |   1     | seconds |

### Example

=== "Kotlin"
    ``` kotlin
    SeleniumScrollToAction(
        selector = "/html/body/form[1]",
        by = SELENIUM_BY.xpath
    )
    ```

# Send keys

!!! info "[Browse Implementation](https://github.com/chutney-testing/chutney/blob/master/action-impl/src/main/java/com/chutneytesting/action/selenium/SeleniumSendKeysAction.java){:target="_blank"}"

This action simulate typing into an element, which may set its value.  
See [WebElement.sendKeys()](https://www.selenium.dev/selenium/docs/api/java/org/openqa/selenium/WebElement.html#sendKeys(java.lang.CharSequence...)){:target="_blank"}

=== "Inputs"

    | Required | Name         | Type    | Default |       Note        |
    |:--------:|:-------------|:--------|:-------:|:-----------------:|
    |    *     | `web-driver` | String  |         |                   |
    |    *     | `selector`   | String  |         |                   |
    |    *     | `by`         | String  |         |                   |
    |    *     | `value`      | String  |         | The value to type |
    |          | `wait`       | Integer |    1    |      seconds      |

### Example

=== "Kotlin"
    ``` kotlin
    SeleniumSendKeysAction(
        selector = "usernameInput",
        by = SELENIUM_BY.NAME,
        value = "the hitchhiker"
    )
    ```

# Set browser size

!!! info "[Browse Implementation](https://github.com/chutney-testing/chutney/blob/master/action-impl/src/main/java/com/chutneytesting/action/selenium/SeleniumSetBrowserSizeAction.java){:target="_blank"}"

This action set the size of the current window.  
See [WebDriver.Window.setSize()](https://www.selenium.dev/selenium/docs/api/java/org/openqa/selenium/WebDriver.Window.html#setSize(org.openqa.selenium.Dimension)){:target="_blank"} for further details.

=== "Inputs"

| Required | Name      | Type    | Default   |
|:--------:|:----------|:--------|:---------:|
|    *     | `width`   | Integer |           |
|    *     | `height`  | Integer |           |

### Example

=== "Kotlin"
    ``` kotlin
    SeleniumSetBrowserSizeAction(
        width = 640,
        height = 480
    )
    ```

# Switch to

!!! info "[Browse Implementation](https://github.com/chutney-testing/chutney/blob/master/action-impl/src/main/java/com/chutneytesting/action/selenium/SeleniumSwitchToAction.java){:target="_blank"}"

!!! note

    By default it selects either the first frame on the page, or the main document when a page contains iframes.  
    See [WebDriver.TargetLocator](https://www.selenium.dev/selenium/docs/api/java/org/openqa/selenium/WebDriver.TargetLocator.html){:target="_blank"} for further details.

    `switchType` accepted values :

    - "Frame"
    - "Popup"
    - "Window"
    - "AlertOk" (Switch to alert and click on the OK button)
    - "AlertCancel" (Switch to alert and click on the Cancel button)

=== "Inputs"

    | Required | Name         | Type    | Default |  Note   |
    |:--------:|:-------------|:--------|:-------:|:-------:|
    |    *     | `web-driver` | String  |         |         |
    |    *     | `selector`   | String  |         |         |
    |    *     | `by`         | String  |         |         |
    |          | `wait`       | Integer |    1    | seconds |
    |          | `switchType` | String  |         |         |

=== "Outputs"

    |        Name      | Type                                                    |
    |:----------------:|:--------------------------------------------------------|
    | `outputSwitchTo` | This value can be used to switch to this element later. |

### Example

=== "Kotlin"
    ``` kotlin
    SeleniumSwitchToAction(
        selector = "outputGet".spEL(),
        by = SELENIUM_BY.id,
        switchType = SELENIUM_SWITCH.Window
    )
    ```

# Wait

!!! info "[Browse Implementation](https://github.com/chutney-testing/chutney/blob/master/action-impl/src/main/java/com/chutneytesting/action/selenium/SeleniumWaitAction.java){:target="_blank"}"

This action waits N seconds or until the expected conditions are met.

!!! note "About `value` input"
    Expected conditions can be combined.  
    Available values are :

    - "elementToBeSelected"
    - "elementToBeClickable"
    - "frameToBeAvailableAndSwitchToIt"
    - "invisibilityOfElementLocated"
    - "visibilityOfElementLocated"
    - "visibilityOfAllElementLocated"
    - "presenceOfElementLocated"
    - "presenceOfAllElementLocated"
    - "and"
    - "or"
    - "not"

=== "Inputs"

| Required | Name         | Type    | Default |  Note   |
|:--------:|:-------------|:--------|:-------:|:-------:|
|    *     | `web-driver` | String  |         |         |
|    *     | `selector`   | String  |         |         |
|    *     | `by`         | String  |         |         |
|          | `wait`       | Integer |    1    | seconds |
|          | `value`      | String  |         |         |

### Example

=== "Kotlin"
    ``` kotlin
    SeleniumWaitAction(
        selector = "//button[text()=\"Some text\"",
        by = SELENIUM_BY.xpath,
        value = "elementToBeClickable"
    )
    ```
