!!! info "[Browse implementation](https://github.com/chutney-testing/chutney/blob/master/action-impl/src/main/java/com/chutneytesting/action/function/XPathFunction.java){:target="_blank"}"

Following functions help you execute XPath request on XML document.

!!! important
    Those functions have two specific behaviors :
    
    * Return the first value in the XPath query result set type-cast to the return type of this XPathExpression. 
    * Try hard to return strings, i.e. try to unwrap, when possible, the dom element, attribute or element CDATA.
    
    Therefore, the object returned type could be :
    
    * String
    * [Element](http://www.jdom.org/docs/apidocs/org/jdom2/Element.html)
    * Generic type of the xpath expression

# xpath

!!! note "Object xpath(String documentAsString, String xpath)"

    Execute an XML path on a given document.

    **Parameters** :

    * `documentAsString` : The XML document against which the path will be executed.
    * `xpath` : The XML path to execute

    **Returns** : The read result

    **Examples** :

    SpEL : `${#xpath(#xmlString, '//element[@attribute='value']')}`

# xpathNs

!!! note "Object xpathNs(String documentAsString, String xpath, Map<String, String> nsPrefixes)"

    Execute an XML path on a given document with the given namespaces prefixes map.

    **Parameters** :

    * `documentAsString` : The XML document against which the path will be executed.
    * `xpath` : The XML path to execute
    * `nsPrefixes` : A map of prefixes associated with XML namespaces

    **Returns** : The read result

    **Examples** :

    SpEL : `${#xpathNs(#xmlString, '//ns1:element[@attribute='value']', {'ns1': 'http://namespace.uri'})}`
