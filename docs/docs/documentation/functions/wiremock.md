!!! info "[Browse implementation](https://github.com/chutney-testing/chutney/blob/master/action-impl/src/main/java/com/chutneytesting/action/http/function/WireMockFunction.java){:target="_blank"}"

Following functions help you work with a Wiremock [LoggedRequest](https://www.javadoc.io/static/com.github.tomakehurst/wiremock/2.27.2/com/github/tomakehurst/wiremock/verification/LoggedRequest.html){:target="_blank"}.

# wiremockHeaders

!!! note "Map<String, String> wiremockHeaders(LoggedRequest request)"

    Extract headers from a given Wiremock logged request.

    **Parameters** :

    * `request` : The logged request

    **Returns** : The request's headers as a map

    **Examples** :

    SpEL : `${#wiremockHeaders(#request)}`

# wiremockQueryParams

!!! note "Map<String, String> wiremockQueryParams(LoggedRequest request)"

    Extract query parameters from a given Wiremock logged request.

    **Parameters** :

    * `request` : The logged request

    **Returns** : The request's query parameters as a map

    **Examples** :

    SpEL : `${#wiremockQueryParams(#request)}`
