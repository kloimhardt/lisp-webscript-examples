# lisp-webscript-examples
Examples for scripting reactive web-apps in Lisp

## Phel/PHP backend
[Phel](https://phel-lang.org) is inspired by Clojure, it compiles to [PHP](https://www.php.net). Using Phel as backend language allows ClojureScript to be served on cheap shared hosting whilst still remaining in the Lisp paradigm.

You need to install PHP 7.4 (or later). Then download Phel with:
```
php composer.phar install

```
Start a server with:
```
php -S localhost:8000
```
Then, in the adress bar of your browser, type:
```
http://localhost:8000/guestbook.php
```

The Phel code is in the file `examples/guestbook4.phel`, it is very similar to Clojure. The Clojurescript code is in `examples/guestbook_4.cljs`, the only change compared to `guestbook_1.cljs` is that JSON is used to exchange data instead of Transit.
