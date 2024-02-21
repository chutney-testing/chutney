# Contributing

## Summary

* [How to contribute to the documentation](#doc)
* [How to make a Pull Request](#pr)
* [Code convention](#code)
* [Branch convention](#branch)
* [Commit message](#commit)
* [Build Process](#build)
* [Release Management](#release)
* [Licensing](#oss)


## <a name="doc"></a> How to contribute to the documentation

To contribute to this documentation (README, CONTRIBUTING, etc.), we conforms to the [CommonMark Spec](http://spec.commonmark.org/0.27/)

* [https://www.makeareadme.com/#suggestions-for-a-good-readme](https://www.makeareadme.com/#suggestions-for-a-good-readme)
* [https://help.github.com/en/articles/setting-guidelines-for-repository-contributors](https://help.github.com/en/articles/setting-guidelines-for-repository-contributors)


## <a name="pr"></a> How to make a Pull Request

1. Fork the repository and keep active sync on our repo
2. Create your working branches as you like
   * **WARNING** - Do not modify the master branch nor any of our branches since it will break the automatic sync
3. When you are done, fetch all and rebase your branch onto our master or any other of ours
   * ex. on your branch, do : 
     * `git fetch --all --prune`
     * `git rebase --no-ff origin/master`
4. Test your changes and make sure everything is working
5. Submit your Pull Request
   * Do not forget to add reviewers ! Check out the last authors of the code you modified and add them.
   * In case of doubts, here are active contributors :

     
## <a name="code"></a> Code convention

Try to follow [Kotlin coding conventions](https://kotlinlang.org/docs/reference/coding-conventions.html).

We use an [.editorconfig file](http://editorconfig.org/), please use a tool accepting it and do not override rules.

Concerning tests mocking :
* Use either Mockito or hand-written test doubles.
* Use `org.springframework.test.web.servlet.MockMvc` to mock REST HTTP endpoints
* **Do not use PowerMock**
  * We consider it to be sign of a code-smell
  
## <a name="branch"></a> Branch convention

* **wip/** unstable code, to share between developers working on the same task
* **feat/** stable code of new feature, to be merged if validated
* **bugfix/** stable code of correction (PROD / VALID)
* **tech/** stable code, purely technical modification like refactoring, log level change or documentation


## <a name="commit"></a> Commit message

As a general rule, the style and formatting of commit messages should follow the guidelines in
[How to Write a Git Commit Message](http://chris.beams.io/posts/git-commit/).

* Separate subject from body with a blank line
* Limit the subject line to 50 characters
* Capitalize the subject line
* Do not end the subject line with a period
* Use the imperative mode in the subject line
* Wrap the body at 72 characters
* Use the body to explain what and why vs. how


**Alternative**:

* http://karma-runner.github.io/0.10/dev/git-commit-msg.html


## <a name="build"></a> Build Process

We use github actions to build and releas this library.
![CI](https://github.com/chutney-testing/chutney-kotlin-dsl/workflows/CI/badge.svg)

## <a name="release"></a> Release Management

### Update Changelog file

Do it first, because changelog updates should be part of the release being made.  
Check page:  [Automatically generated release notes](https://docs.github.com/en/repositories/releasing-projects-on-github/automatically-generated-release-notes)

Do not hesitate to update the release note generated especially the titles of pull request :)
Use it to update [CHANGELOG.md](https://github.com/chutney-testing/chutney-kotlin-dsl/blob/master/CHANGELOG.md)

### Releasing

We use [Reckon gradle plugin](https://github.com/ajoberstar/reckon). 

Release is just pushing a tag with version information :
```shell
  ./gradlew reckonTagCreate -Preckon.stage=<final|snapshot> -Preckon.scope=<major|minor|patch>
  git push origin <TAG_VERSION> 
```

### Update Github release

- Update [Release <RELEASE_VERSION>](https://github.com/chutney-testing/chutney-kotlin-dsl/releases)

## <a name="oss"></a> Licensing

We choose to apply the Apache License 2.0 (ALv2) : [http://www.apache.org/licenses/LICENSE-2.0](http://www.apache.org/licenses/LICENSE-2.0)

As for any project, license compatibility issues may arise and should be taken care of.

Concrete instructions and tooling to keep Chutney ALv2 compliant and limit licensing issues are to be found below.

However, we acknowledge topic's complexity, mistakes might be done and we might not get it 100% right.

Still, we strive to be compliant and be fair, meaning, we do our best in good faith.

As such, we welcome any advice and change request.


To any contributor, we strongly recommend further reading and personal research :
* [http://www.apache.org/licenses/](http://www.apache.org/licenses/)
* [http://www.apache.org/legal/](http://www.apache.org/legal/)
* [http://apache.org/legal/resolved.html](http://apache.org/legal/resolved.html)
* [http://www.apache.org/dev/apply-license.html](http://www.apache.org/dev/apply-license.html)
* [http://www.apache.org/legal/src-headers.html](http://apache.org/legal/src-headers.html)
* [http://www.apache.org/legal/release-policy.html](http://www.apache.org/legal/release-policy.html)
* [http://www.apache.org/dev/licensing-howto.html](http://www.apache.org/dev/licensing-howto.html)

* [Why is LGPL not allowed](https://issues.apache.org/jira/browse/LEGAL-192)
* https://issues.apache.org/jira/projects/LEGAL/issues/

* General news : [https://opensource.com/tags/law](https://opensource.com/tags/law)

### How to manage license compatibility

When adding a new dependency, **one should check its license and all its transitive dependencies** licenses.

ALv2 license compatibility as defined by the ASF can be found here : [http://apache.org/legal/resolved.html](http://apache.org/legal/resolved.html)

3 categories are defined :
   * [Category A](https://www.apache.org/legal/resolved.html#category-a) : Contains all compatibles licenses.
   * [Category B](https://www.apache.org/legal/resolved.html#category-b) : Contains compatibles licenses under certain conditions.
   * [Category X](https://www.apache.org/legal/resolved.html#category-x) : Contains all incompatibles licenses which must be avoid at all cost.

__As far as we understand :__

If, by any mean, your contribution should rely on a Category X dependency, then you must provide a way to modularize it 
and make it's use optional to Chutney, as a plugin.

You may distribute your plugin under the terms of the Category X license.

Any distribution of Chutney bundled with your plugin will probably be done under the terms of the Category X license.

But _"you can provide the user with instructions on how to obtain and install the non-included"_ plugin.

__References :__
- [Optional](https://www.apache.org/legal/resolved.html#optional)
- [Prohibited](https://www.apache.org/legal/resolved.html#prohibited)

### How to comply with Redistribution and Attribution clauses

Lots of licenses place conditions on redistribution and attribution, including ALv2.

__References :__
* http://mail-archives.apache.org/mod_mbox/www-legal-discuss/201502.mbox/%3CCAAS6%3D7gzsAYZMT5mar_nfy9egXB1t3HendDQRMUpkA6dqvhr7w%40mail.gmail.com%3E
* http://mail-archives.apache.org/mod_mbox/www-legal-discuss/201501.mbox/%3CCAAS6%3D7jJoJMkzMRpSdJ6kAVSZCvSfC5aRD0eMyGzP_rzWyE73Q%40mail.gmail.com%3E

#### LICENSE file
##### In Source distribution

This file contains :
* the complete ALv2 license.
* list dependencies and points to their respective license file
  * Example :
    _This product bundles SuperWidget 1.2.3, which is available under a
    "3-clause BSD" license.  For details, see deps/superwidget/_
* do not list dependencies under the ALv2
