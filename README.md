# json — `igropyr` branch

`(igropyr json)` — a safe JSON parser and writer for [Chez Scheme][chez],
the variant of [`guenchi/json`](https://github.com/guenchi/json) used by
[Igropyr][igropyr]. It is a recursive-descent parser over the input
string — no reader tricks — so it is safe for untrusted input such as
HTTP request bodies, with full string-escape handling including
`\uXXXX` and surrogate pairs. It depends only on `(chezscheme)`.

The data model is compatible with `guenchi/json`'s path DSL.

## API

```scheme
(import (igropyr json))

(string->json s)     ; parse; raises #(json-error msg pos) on bad input
(json->string x)     ; serialize
(json-ref x k ...)   ; path access; #f when absent
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

`json->string` writes alists as objects and vectors as arrays; a list
that is neither empty nor alist-shaped also serializes as an array,
while `'()` is written as an empty object. `json-ref` takes a string or
symbol key for objects and an integer index for arrays, following a path
across nested values.

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
