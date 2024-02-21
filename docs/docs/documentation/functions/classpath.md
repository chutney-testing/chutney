!!! info "[Browse implementation](https://github.com/chutney-testing/chutney/blob/master/action-impl/src/main/java/com/chutneytesting/action/function/ClasspathFunctions.java){:target="_blank"}"

# resourceContent

!!! note "String resourceContent(String name, String charset)"

    See [Files.readString()](https://docs.oracle.com/en/java/javase/12/docs/api/java.base/java/nio/file/Files.html#readString(java.nio.file.Path,java.nio.charset.Charset)){:target="_blank"} for further details

    **Returns** :

    * Returns the content of the resource.

    **Examples** :

    SpEL : `${#resourceContent("name", "UTF-8")}`

# resourcePath

!!! note "String resourcePath(String name)"

    See [Path.toString()](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Path.html#toString()){:target="_blank"} for further details

    **Returns** :

    * Returns the string representation of this path.

    **Examples** :

    SpEL : `${#resourcePath("name")}`

# resourcesPath

!!! note "String resourcesPath(String name)"

    Finds all the resources with the given name. A resource is some data (images, audio, text, etc) that can be accessed by class code in a way that is independent of the location of the code.

    See [ClassLoader.getResources()](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/ClassLoader.html#getResources(java.lang.String)){:target="_blank"} for further details

    **Returns** :

    * Returns an enumeration of URL objects for the resource. If no resources could be found, the enumeration will be empty.

    **Examples** :

    SpEL : `${#resourcesPath("name")}`
