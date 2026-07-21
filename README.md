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

`json->string` writes alists as objects and vectors as arrays (a plain
list also serializes as an array). `json-ref` takes a string or symbol
key for objects and an integer index for arrays, following a path across
nested values.

## Layout and use

The source is `json.sc`; the library it defines is `(igropyr json)`.
Chez resolves that name to `igropyr/json.sc` on the library path, so put
`json.sc` in an `igropyr/` directory that is on your `CHEZSCHEMELIBDIRS`,
and include `.sc` in the library extensions. Inside Igropyr, drop it
alongside the other `igropyr/*.sc` sources — it is already listed in the
build.

```sh
CHEZSCHEMELIBDIRS=. CHEZSCHEMELIBEXTS=.sc scheme --script your-program.ss
```

## License

MIT. See [LICENSE](LICENSE).

[chez]: https://www.scheme.com
[igropyr]: https://github.com/guenchi/Igropyr
