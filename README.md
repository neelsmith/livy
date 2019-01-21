# livy

Digital texts of Livy's *History* and the *Periochae*

## Using `tabulae` with this repository


### Prequisites

1.  Clone [tabulae](https://github.com/neelsmith/tabulae) in this directory, or download and unpack a zip of the tabulae repistory, and rename it `tabulae`.
2.  Install the [docker desktop](https://www.docker.com/products/docker-desktop) app.

### Starting, stopping and resuming [Chris Blackwell's docker app](https://github.com/Eumaeus/sbt-sfst-docker)

`sbt-sfst-docker` provides the `sfst` toolkit that `tabulae` uses, and starts an `sbt` console in your current directory.  Follow the instructions on the docker app's README, or run these commands by copying and pasting them into a terminal in this directory:

3.  Initial installation in this directory: `docker run --name citeWork -ti -v $(pwd):/workspace eumaeus/sbt-sfst-docker:v3`
4.  To end a session:  `:quit` in the dockerized sbt console
5.  To resume a session:  `docker restart citeWork && docker exec -ti citeWork sbt console`

### Using `tabulae` within the docker session

To begin or resume a docker session, first load this scala script:  `:load /workspace/scripts/compile.sc`

-  compile a new parser:  `compile()`
-  generate a word list from the corpus
-  analyze a generated word list
