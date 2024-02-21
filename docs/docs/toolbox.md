* [mkdocs.org](https://www.mkdocs.org/user-guide/){:target="_blank"}
* [mkdocs-material](https://squidfunk.github.io/mkdocs-material/reference/){:target="_blank"}

# Main Section

## Subsection

### Codeblocs

=== "JSON"

    ``` json
    {
        "type": "http-get",
        "target": "some_target",  // (1)
        "inputs": {
            "uri": "/actuator/health",  // (2)
            "headers": { // (3)
                "X--API-VERSION": "1.0",
                "X--HEADER-1": "42"
            },
            "timeout": "3000 ms" // (4)
        }
    }
    ```

    1. Target de type http ou https
    2. Uri de la requete
    3. *Optionel* Headers de la requete
    4. *Optionel* par défault 2 secondes. Unité disponible : "ms", "s", "sec", "m", "min", "h", "hour", "hours", "hour(s)", "d", "day", "days", "day(s)"


=== "YAML"

    ``` yaml
    code:
      annotation: are possible # (1)
      contains: markdown # (2)
    ```

    1. Everywhere a comment is possible in the target language (thanksfully [pygment supports it for json](https://github.com/pygments/pygments/pull/2049){:target="_blank"})
    2. Code annotation contain `code`, __formatted
         text__, images, ... basically anything that can be written in Markdown.

=== "C++"

    ``` c++
    #include <iostream>

    int main(void) {
      std::cout << "Hello world!" << std::endl;
      return 0;
    }
    ```

=== "file import"
    ``` md
    --8<-- "docs/index.md"
    ```

### Admonitions

!!! note
    `note`

!!! tip
    `tip`, `hint`, `important`


!!! abstract
    `abstract`, `summary`, `tldr`

!!! info
    `info`, `todo`

!!! warning
    `warning`

### Diagrams

``` mermaid
erDiagram
  CUSTOMER ||--o{ ORDER : places
  ORDER ||--|{ LINE-ITEM : contains
  CUSTOMER }|..|{ DELIVERY-ADDRESS : uses
```

### Footnotes

[^1]: This is a footnote content.
