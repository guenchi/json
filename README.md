# json — `igropyr` branch

`(igropyr json)` — a safe JSON parser and writer for [Chez Scheme][chez],
the variant of [`guenchi/json`](https://github.com/guenchi/json) used by
[Igropyr][igropyr]. It is a recursive-descent parser over the input
string — no reader tricks — so it is safe for untrusted input such as
HTTP request bodies, with full string-escape handling including
`\uXXXX` and surrogate pairs. It depends only on `(chezscheme)`.

The path DSL is the one from `guenchi/json`: the same verbs, and the
same locator forms. The set of Scheme values this variant will SERIALIZE
is narrower — see the refusals below — so a document round-trips
identically, while a program that fed the writer a list where an array
was meant will be told rather than obeyed.

## API

```scheme
(import (igropyr json))

(string->json s)     ; parse; raises #(json-error msg pos) on bad input,
                     ; and on an argument that is not a string
(json->string x)     ; serialize
(json-ref x k ... [absent])   ; path access; #f when absent, or the
                              ; value of a trailing thunk if given
```

Data model:

| JSON | Scheme |
| --- | --- |
| object `{"a":1}` | alist with string keys `(("a" . 1))` |
| array `[1,2]` | vector `#(1 2)` |
| string | string |
| number | number |
| `true` / `false` | `#t` / `#f` |
| `null` | `'null` |

`json->string` writes alists as objects and vectors as arrays, and
`'()` as an empty object. **An array is a vector and nothing else**: a
list in a value position is refused, not written, and so is a symbol
where a string belongs — including a symbol used as an object key.

Every refusal locates the mistake. The symbol ones name the offending
value; the rest name its KIND and never the value itself, because a value
that reaches a refusal may be circular or enormous, and an error path must
not print it:

```scheme
(json->string #\a)          ; "not a JSON value: a character"
(json->string 1+2i)         ; "not a JSON value: a complex number"
(json->string '(1 2 3))     ; "a JSON array is a vector, not a list: use list->vector"
(json->string '("a" . 1))   ; "not a JSON value: an improper or circular list, and an object with one member is (("k" . v))"
```

Where the writer cannot tell two intentions apart it gives both spellings
rather than guessing at one: an improper pair is a malformed list AND the
commonest way to write a single object member without the list around it,
and neither reading is a guess.

Refusing rather than converting is what keeps two Scheme values from
reaching the same document — that, and not tidiness, is why these are
errors and not conversions.

`string->json` checks its argument the same way: anything that is not a
string is refused by name, instead of failing somewhere inside the parser
with a condition that names neither the procedure nor the problem.

```scheme
(string->json (bytevector 1))  ; "string->json takes a string, not a bytevector"
```

**The third slot says which side raised.** Reading errors carry an index
into the input; writing errors carry `#f`, having no input to index. A
handler can dispatch on that, which is why the argument check reports
position `0` — nothing was consumed — rather than `#f`.

A **locator** is not a stored key, and the rule for it is the opposite
one: `json-ref` and the other path verbs accept a string, a symbol, or
an exact non-negative index, spelling a symbol to a string for the
lookup. Nothing symbolic is ever stored that way, so no ambiguity
follows.

Path verbs come in two layers, and here the trailing `*` means the
**lower** layer, not an enhanced one:

| starred, one locator, applicable | over a path written at the call site |
| --- | --- |
| `(json-ref* x k [absent])` | `(json-ref x k ... [absent])` |
| `(json-set* x k v)` | `(json-set x k ... v)` |
| `(json-drop* x sel)` | `(json-drop x k ... sel)` |
| `(json-update* x sel p)` | `(json-update x k ... sel p)` |
| `(json-push* x member)` | `(json-push x k ... member)` |
| `(json-insert* x k member)` | `(json-insert x k ... loc member)` |

The macros read down the path and rebuild on the way out; a failure
anywhere answers `#f` for the whole expression. The container and every
locator are evaluated exactly once. `json-object?`, `json-array?` and
`json-null?` classify a value in this representation; they are cheap and
non-recursive, so they are not writability checks.

## Layout and use

The sources are `json.sc` and `json-internal.sc`, defining
`(igropyr json)` and `(igropyr json-internal)`. Chez resolves those names
to `igropyr/json.sc` and `igropyr/json-internal.sc` on the library path,
so put BOTH files in an `igropyr/` directory that is on your
`CHEZSCHEMELIBDIRS`, and include `.sc` in the library extensions.

`(igropyr json)` depends on `(igropyr json-internal)`; both depend only
on `(chezscheme)`.

**`(igropyr json-internal)` IS NOT PART OF THIS LIBRARY'S API, and
application code must not import it.** It is a separate file for one
reason: three guards inside the number writer refuse spellings that no
formatter output reaches, so their refusals cannot be provoked through
`json->string`, and a test unable to call them directly would pass while
any of them was deleted. Its names and behaviour may change in any
release. Inside Igropyr, drop it
alongside the other `igropyr/*.sc` sources — it is already listed in the
build.

```sh
CHEZSCHEMELIBDIRS=. CHEZSCHEMELIBEXTS=.sc scheme --script your-program.ss
```

## License

MIT. See [LICENSE](LICENSE).

[chez]: https://www.scheme.com
[igropyr]: https://github.com/guenchi/Igropyr
