# lisp-webscript-examples

Lisp like its Javacript+PHP.

Examples for programming small web-apps using two [Clojure](https://clojure.org) inspired Lisp interpreters, one for the backend and one for the frontend: [Phel](https://phel-lang.org) and  [Scittle](https://borkdude.github.io/scittle). Because the interpreters run on PHP and Javascript respectively, the code can be deployed on cheap shared web-hosting. Moreover the code can be directly changed on the host with immediate effect, because there is no compilation step involved: Lisp like its Javacript+PHP.

The are two examples, both canonical: Guestbook and Todo-MVC. They show how to administer the interplay between frontend and backend the Clojure way. While Guestbook (taken directly from the [Luminus book](https://pragprog.com/titles/dswdcloj3/web-development-with-clojure-third-edition)) is simpler and has a more traditional REST api, Todo-MVC has a more advanced GraphQL api via [Qlkit](https://medium.com/@conrad_9565/lets-build-a-ui-with-qlkit-and-graph-queries-79b7b118ddac)

## Setup

You need to install PHP 7.4 (or later) and Composer ([download here](https://getcomposer.org/download/latest-stable/composer.phar)). Then download Phel with:
```
php composer.phar install

```
Start a server with:
```
php -S localhost:8000
```

## Guestbook
To try the Guestbook example, type in the adress bar of your browser:
```
http://localhost:8000/?guestbook
```
The Phel and Scittle code are in the files `phel/guestbook.phel` and `cljs/guestbook.cljs`, both sport the Clojure(Script) syntax and the goodness of persistent data structures.

## Todo-MVC
You need to have the MySql database installed. Most probably you also need to change the `dbname=` part of the dsn string at the top of `phel/server.phel`. Then, start with
```
http://localhost:8000/
```

The code in `little-clojure/honey-sql.phel` implements a very small subset of [HoneySQL](https://github.com/seancorfield/honeysql) which translates Clojure/Phel datastructures (like `{:select [:id] :from [:todos]}`) into SQL strings.

The code in `little-clojure/core.phel` contains a crude imitation of [Clojure multimethods](https://clojure.org/reference/multimethods), which are necessary for implementing Qlkit's parsers.

## Technical Note 
I needed to expose to [Scittle](https://github.com/kloimhardt/scittle) the library [cljs-ajax](https://github.com/JulianBirch/cljs-ajax) and a slightly patched [Qlkit](https://github.com/kloimhardt/qlkit) (patched for a straightforward exposure of the central Macro). All of this Clojurescript code is compiled into publicly served [Javascript files](https://kloimhardt.github.io/scittle-fork/js/scittle.ajax.js), but can be self-compiled following the Scittle instructions.

The 60 loc server part of Qlkit had to be ported to Phel.

## Run Tests

```
./vendor/bin/phel test
```
