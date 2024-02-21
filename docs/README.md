# Developer setup

Follow [https://www.mkdocs.org/getting-started/](https://www.mkdocs.org/getting-started/) 
& [https://squidfunk.github.io/mkdocs-material/getting-started/](https://squidfunk.github.io/mkdocs-material/getting-started/)

Run : 
 - Via Python
   - `pip install mkdocs mkdocs-material mkdocs-git-revision-date-localized-plugin mkdocs-redirects`
   - `mkdocs serve` in the project root folder
   - Visit  [http://localhost:8000/](http://localhost:8000/)
 - Via [Docker](https://squidfunk.github.io/mkdocs-material/getting-started/ "more recent methods and docs may very well be available there") (in case you are not a big python fan)
   - build an image from de provided [Dockerfile](Dockerfile "you'll only need to do this once") `docker build -t chutney/doc .`
   - in the project [root folder](. "I'm pretty sure you're already there") run `docker run --rm -p 8000:8000 -v ${PWD}:/docs chutney/doc`
   - Visit  [http://localhost:8000/](http://localhost:8000/)
