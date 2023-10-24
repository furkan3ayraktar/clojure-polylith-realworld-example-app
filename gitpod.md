# Hack on Real World Polylith in your Browser

Using [Gitpod](https://www.gitpod.io) you can explore a [Polylith](https://polylith.gitbook.io/polylith/) implementation of [Real World](https://www.realworld.how/) from the Clojure REPL, without downloading or installing anything at all.

![Alt text](.media/gitpod/Gitpod-Polylith-RealWorld.png)

Don't click on this link just yet: 
https://gitpod.io/#https://github.com/PEZ/clojure-polylith-realworld-example-app

The link will take you to a full blown [VS Code](https://code.visualstudio.com/) running in your browser. The VS Code instance will have [Clojure](https://clojure.org) development support (through [Calva](https://calva.io)). The first time you use it, it may be quite a long wait. But then (after some little more waiting) you will be in the editor, connected to the REPL of the Polylith Real World server!

## Prerequisites

* A Github account.
* Curiosity

That's it.

## From here to the Polylith REPL

When you click the link you will first need to sign in to Gitpod using your Github account, then create the workspace, then wait, then wait a bit again. Then you will have the REPL under your fingertips. The process looks like so:

![Alt text](.media/gitpod/Gitpod-to-REPL.png)

The Clojure file that opens will have further instructions and suggestions for what you can try at the REPL. There's also a file `src/hello_repl.clj` available for anyone unfamiliar with Calva and/or Clojure to start with.

Now you can click that link above. 😄 (That said, see below about [considering forking first](#fork-first).)

Happy coding! ❤️

## Fork first?

If you just click the link above, things will work, but you will not be able to immediately push your work to your own repo. If you find that you want to do that, use your git skills to retarget the local (albeit Gitpod hosted) repo to a repo of your own.

It could be a good idea to fork this repo first, of course.

## Gitpod Free hours are limited

You may find it so fun to play with Polylith in the REPL that you run out of Gitpod hours. Then it is time to either:

1. Upgrade to a paid Github plan
2. Run this repository locally, that may involve
   * Installng Java
   * Installing Clojure
   * Finding a Clojure environment for your editor of choice
   * Checking https://polylith.gitbook.io/poly/ out for how to install the `poly tool`

## TODO:

* [ ] Change the link to https://github.com/furkan3ayraktar/clojure-polylith-realworld-example-app before merging. And update the screenshots in [.media/gitpod](.media/gitpod).
* [x] Pre-install poly tool as `poly`
* [x] Figure out if we should silence the port 6003 opened, popup
* [x] Figure out if we can **Install Portal** message box
