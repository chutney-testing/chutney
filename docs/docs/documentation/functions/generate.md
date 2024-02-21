!!! info "[Browse implementation](https://github.com/chutney-testing/chutney/blob/master/action-impl/src/main/java/com/chutneytesting/action/function/Generate.java){:target="_blank"}"

Following functions help you generate random values.

# File

!!! note "String file()"

    Generate a file with a default size of 1kB, in the default OS temp directory.
    File name is prefixed with _chutney_

    **Returns** :

    * The canonical path of the file

    **Examples** :

    SpEL : `${#generate.file()}`

!!! note "String file(int fileSize)"

    Generate a file with a size of _n_ bytes, in the default OS temp directory.
    File name is prefix with _chutney_ and maximum file size is 100MB (104857600 bytes).

    **Returns** :

    * The canonical path of the file

    **Examples** :

    SpEL : `${#generate.file(42)}`

!!! note "String file(String destination, int fileSize)"

    Generate a file with a size of _n_ bytes, with a specific path and filename.
    Maximum file size is 100MB (104857600 bytes).

    **Returns** :

    * The canonical path of the file

    **Examples** :

    SpEL : `${#generate.file("/path/to/dest/file", 42)}`

# Identifier

!!! note "String id(String prefix, int length)"

    Generate a String with a given prefix and _n_ random characters.

    **Returns** :

    * The generated String

    **Examples** :

    SpEL : `${#generate.id("prefix-", 6)}` -> ex. output `prefix-r4nd0m`

!!! note "String id(int length, String suffix)"

    Generate a String with _n_ random characters and a given suffix.

    **Returns** :

    * The generated String

    **Examples** :

    SpEL : `${#generate.id(6, "-suffix")}` -> ex. output `r4nd0m-suffix`

!!! note "String id(String prefix, int length, String suffix)"

    Generate a String with a given prefix, _n_ random characters and a given suffix.

    **Returns** :

    * The generated String

    **Examples** :

    SpEL : `${#generate.id("pre-", 6, "-suf")}` -> ex. output `pre-r4nd0m-suf`

# Int

!!! note "String randomInt(int bound)"

    Generate a random int value between 0 (included) and a given bound value (excluded) (i.e `[0, bound[`).

    See [Random.nextInt(int)](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/Random.html#nextInt(int)){:target="_blank"} for further details

    **Returns** : 

    * The random int value as a String

    **Examples** :

    SpEL : `${#generate.randomInt(42)}`

# Long

!!! note "String randomLong()"

    Generate a random long value.

    See [Random.nextLong()](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/Random.html#nextLong()){:target="_blank"} for further details

    **Returns** :

    * The generated long value as a String

    **Examples** :

    SpEL : `${#generate.randomLong()}`

# UUID

!!! note "String uuid()"

    Generates a unique identifier (UUID). 

    See [UUID.uuid()](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/UUID.html){:target="_blank"} for further details

    **Returns** :

    * The UUID as a String

    **Examples** :

    SpEL without : `${T(java.util.UUID).randomUUID().toString()}`

    SpEL with    : `${#generate.uuid()}`

