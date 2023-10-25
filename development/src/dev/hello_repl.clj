(ns dev.hello-repl)

;; == Some VS Code knowledge required ==
;; This tutorial assumes you know a few things about
;; VS Code. Please check out this page if you are new
;; to the editor: https://code.visualstudio.com/docs
;;
;; == Keyboard Shortcuts Notation used in this tutorial ==
;; We use a notation for keyboard shortcuts, where
;; `+` means the keys are pressed at the same time
;; and ` ` separates any keyboard presses in the sequence.
;; `Ctrl+Alt+C Enter` means to press Ctrl, Alt, and C
;; all at the same time, then release the keys and
;; then press Enter. (The Alt key is named Option or
;; Opt, on some machines)
;;
;; Let's start by loading this file in the REPL.
;; Please press `Ctrl+Alt+C Enter`

"Welcome to the Getting Started REPL! 💜"

;; Once you see a message in the output/REPL window ->
;; saying that this file is loaded, you can start by
;; placing the cursor anywhere on the line with the
;; string above  and press: `Alt+Enter`

;; Did it? Great!
;; See that `=> "Welcome ...` at the end of the line?
;; That's the result of the evaluation you just
;; performed. You just used the Clojure REPL!
;; 🎉 Congratulations! 🎂

(comment
  ;; You can evaluate the string below in the same way
  ;; Place the cursor anywhere on the line with the
  ;; string and press `Alt+Enter`.

  "Hello World!"

  ;; This works because this is a 'Rich Comment Form'
  ;; which is where we Clojurians  often develop new
  ;; code. You'll sometimes see it abbreviated as RCF.
  ;; See also: https://calva.io/rich-comments/

  ;; Evaluate the following form too (you can
  ;; place the cursor anywhere on any of the two lines):

  (repeat 7
          "I am using the REPL! 💪")

  ;; Only `=> ("I am using the REPL! 💪"` is displayed
  ;; inline. You can see the full result, and also copy
  ;; it, if you hover the evaluated expression. Or press
  ;; `Ctrl+K Ctrl/Cmd+I`.

  ;; Let's get into the mood for real. 😂
  ;; Place the cursor on any of the five code lines below:
  ;; `Alt+Enter`, then `Cmd+K Cmd+I`.

  (map (fn [s]
         (if (re-find #" [REPL]$" s)
           (str "Give me " s "! ~•~ " (last s) "!")
           s))
       ["an R" "an E" "a  P" "an L" "What do you get?" "REPL!"])

  ;; Clear the inline display with `Esc`. The inline
  ;; results are also cleared when you edit the file.

  ;; Which brings us to a VERY IMPORTANT THING:
  ;; By default, Calva will be a Guardian of the Parens.
  ;; This means that the backspace and delete buttons
  ;; will not delete balanced brackets. Please go ahead
  ;; and try to delete a bracket in the expression above.
  ;; See?

  ;; TO DELETE A BALANCED BRACKET:
  ;;   press `alt/option+backspace` or `alt/option+delete`

  ;; You might notice that the output/REPL window ->
  ;; is also displaying the results. Depending on your
  ;; preferences you might want to close that window or move
  ;; it to the same editor group (unsplit) as the files you
  ;; edit. But don't do that just yet, get a feel how how
  ;; it works having it in a split pane first.

  ;; BTW. That output/REPL window ->
  ;; You can evaluate code from its prompt too.
  ;; But the cool peeps do not do that very often.
  ;; Because the REPL lives in the files with the application
  ;; code! And because Rich Comment Forms (RCF).
  ;; It is Interactive Programming, and it is 💪.

  :rcf) ; <- This is a convenient way to keep the closing
        ;    paren of a Rich comment form from folding
        ;    when the code is formatted.


;; About commands and shortcuts:
;; Please read https://calva.io/finding-commands/
;; (It's very short.)
;; When we refer to commands by their name, use
;; the VS Code Command Palette to search for them
;; if you don't know the keyboard shortcut.
;; All Calva commands are prefixed with ”Calva”.

;; == Evaluating definitions ==
;; Alt+Enter is the Calva default keyboard shortcut
;; to evaluate the current ”top level” forms. Top
;; level meaning the outermost ”container” of forms,
;; which is the file. This function definition is on
;; the top level. Please evaluate it!

(defn greet
  "I'll greet you"
  [s]
  (str "Hello " s "!"))

;; Forms inside `(comment ...)` are also considered
;; to be top level. This makes it easy to experiment
;; with code.

(comment
  (greet "World")
  :rcf)

;; Anything printed to stdout is not shown inline.

(comment
  (println (greet "World"))
  :rcf)

;; You should see the result of the evaluation, nil,
;; inline, and ”Hello World!” followed by the result
;; printed to the output window.

;; Maybe you wonder what a ”form” is? Loosely defined
;; it is about the same as an S-expression:
;; https://en.wikipedia.org/wiki/S-expression
;; That is, either a ”word” or something enclosed in
;; brackets of some type, parens (), hard brackets [],
;; curlies {}, or quotes "". This whole thing is a
;; form:

(str 23 (apply + [2 3]) (:foo {:foo "foo"}))

;; So is `str`, `23`, "foo", `(apply + [2 3])`,
;; `{:foo "foo"}`, `+`, `[2 3]`, `apply`, and also
;; `(:foo {:foo "foo"})`.

;; Calva has a concept of ”current form”, to let you
;; evaluate forms that are not at the top level. The
;; ”current form” is determined by where the cursor is.
;; Calva has a command that will let you easily
;; experiment with which form is considered current:
;; * Calva: Expand Selection
;; Using this command, starting from no selection, will
;; select the current form.

;; == Evaluating the Current Form ==
;; Ctrl+Enter evaluates the ”current” form
;; Try it with the cursor at different places in this
;; code snippet:

(comment
  (str 23 (apply + [2 3]) (:foo {:foo "foo"}))

  ;; You might discover that Calva regards words in
  ;; strings as forms. Don't panic if `foo` causes
  ;; `foo` is undefined until you top-level eval it.
  ;; an evaluation error. It is not defined, since
  ;; it shouldn't be. You can define it, of course,
  ;; just for fun and learning: Top level eval the
  ;; following definition.

  (def foo
    [1 2 :three :four])

  ;; Then *evaluate current form* with the cursor
  ;; inside the string "foo" above.
  ;; Whatever you ask Calva to send to the REPL,
  ;; Calva will send to the REPL.

  :rcf)

;; == Rich Comments Support ==
;; Repeating an important concept: Forms inside
;; `(comment ...)` are also considered top level
;; by Calva. Alt+Enter at different places below
;; to get a feel for it.

(comment
  "I ♥️ Clojure"

  (greet "World")

  foo

  (range 10)

  ;; https://calva.io/rich-comments/
  :rcf)


;; Also try the commands *Show Hover*,
;; *Show Definition Preview Hover*
;; *Go to Definition*

(comment
  (println (greet "side effect"))
  (+ (* 2 2)
     2)

  :rcf)


;; == You Control what is Evaluated ==
;; Please note that Calva never evaluates your code
;; unless you explicitly ask for it. So, you will
;; have to load files you open yourself. Make it a
;; habit to do this, because sometimes things don't
;; work when your file is not loaded.

;; Try it with this file: `Ctrl+Alt+C Enter`.
;; The result of loading a file is whatever is the
;; last top level form in the file.

;; == Editing Code ==
;; A note about editing Clojure in Calva:
;; If you edit and experiment with the examples you
;; will notice that Calva auto-indents your code.
;; You can re-indent, and format, code at will, using
;; the `Tab` key. It will format the current enclosing
;; form. Try it at the numbered places in this piece
;; of code, starting at `; 1`:

(comment ; 3
     (defn- divisible
           "Is `n` divisible by `d`?"
[n d]
(zero? (mod n d)
           )

  )

       (defn fizz-buzz [n] ; 2
                       (cond ; 1
(divisible n (* 5 3)) "FizzBuzz"
                      (divisible n 5)       "Buzz"
              (divisible n 3)       "Fizz"
  :else                  n))
                               :rcf)

;; === Paredit `strict` mode  is on ===
;; Calva supports structural editing (editing that
;; considers forms rather than lines) using a system
;; called Paredit. By default Paredit tries to protect
;; from accidentally deleting brackets and unbalancing
;; the structure of forms. To override the protection,
;; use `Alt+Backspace` or `Alt+delete`.

(comment
  (defn strict-greet
    "Try to remove brackets and string quotes
   using Backspace or Delete. Try the same
   with the Alt key pressed."
    [name]
    (str "Strictly yours, " name "!"))

  (strict-greet "dear Paredit fan")
  :rcf)

;; (Restore with *Undo* if needed.)
;; See also:
;; https://calva.io/paredit

;;;;;;;;;;;;;;;;;;;;;; 🤘 🎸 🎉 ;;;;;;;;;;;;;;;;;;;;;

;; Done? Awesome. Please continue with the Polylith
;; `getting_started.clj`

;; Learn much more about Calva at https://calva.io
;; Also: There is a more comprehensive guide to Calva
;; as well as to Paredit and even Clojure, built in


;; This string is the last expression in this file

"hello_repl.clj is loaded, and ready with some things for you to try."

;; It is what you'll see printed in the Output
;; window when you load the file.

;; This guide downloaded from:
;; https://github.com/BetterThanTomorrow/dram
;; Please consider contributing.
