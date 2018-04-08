# json
JSON librairy for Scheme


`{key1:value1,key2:value2}`

will parse to

`((key1 . value2)(key2 . value2))`

`[value1,value2,value3]`
will parse to
`#(value1 value2 value3)`


***json->list***

```
> (display token1)
{"first":"1","second":"2","third":[3.1,[3.1,3.2,3.3,3.4,3.5],3.3,3.4,3.5],"four":"4"}

> (json->list token)
(("first" . "1")
  ("second" . "2")
  ("third" . #(3.1 #(3.1 3.2 3.3 3.4 3.5) 3.3 3.4 3.5))
  ("four" . "4"))
> (display token2)
{"first":1,2:"2","third":[3.1,{"first":1,"second":"2","third":[3.31,3.32,3.33,3.34,3.35],"four":"4"},3.3,3.4,3.5],"four":"4"}
> (json->list token)
(("first" . 1)
  (2 . "2")
  ("third"
    .
    #(3.1
      (("first" . 1)
        ("second" . "2")
        ("third" . #(3.31 3.32 3.33 3.34 3.35))
        ("four" . "4"))
      3.3 3.4 3.5))
  ("four" . "4"))
(display token3)
[0.1,0.2,{"first":"1","second":"2","third":[3.1,{"first":1,"second":"2","third":[3.31,3.32,3.33,3.34,3.35],"four":"4"},3.3,3.4,3.5],"four":"4"},0.3]
> (json->list token)
#(0.1 0.2
  (("first" . "1")
    ("second" . "2")
    ("third"
      .
      #(3.1
        (("first" . 1)
          ("second" . "2")
          ("third" . #(3.31 3.32 3.33 3.34 3.35))
          ("four" . "4"))
        3.3 3.4 3.5))
    ("four" . "4"))
  0.3)
```

```
> (define token
      "{\"first\":\"1\",\"seconde\":\"2\",\"third\":[3.1,3.2,3.3,3.4,3.5],\"four\":\"4\"}")
> (list->json (json->list token))
"{\"first\":\"1\",\"seconde\":\"2\",\"third\":[3.1,3.2,3.3,3.4,3.5],\"four\":\"4\"}"
> (define token
        "{\"first\":\"1\",\"second\":\"2\",\"third\":[3.1,[3.1,3.2,3.3,3.4,3.5],3.3,3.4,3.5],\"four\":\"4\"}")
> (list->json (json->list token))
"{\"first\":\"1\",\"second\":\"2\",\"third\":[3.1,[3.1,3.2,3.3,3.4,3.5],3.0,3,3.4,3.5],\"four\":\"4\"}"
> (define token
        "{\"first\":1,2:\"2\",\"third\":[3.1,{\"first\":1,\"second\":\"2\",\"third\":[3.31,3.32,3.33,3.34,3.35],\"four\":\"4\"},3.3,3.4,3.5],\"four\":\"4\"}")
> (list->json (json->list token))
"{\"first\":1,2:\"2\",\"third\":[3.1,{\"first\":1,\"second\":\"2\",\"third\n\":[3.31,3.32,3.33,3.34,3.35],\"four\":\"4\"},3.3,3.4,3.5],\"four\":\"4\"}"
> (define token
        "[0.1,0.2,{\"first\":\"1\",\"second\":\"2\",\"third\":[3.1,{\"first\":1,
\"
  second\":\"2\",\"third\":[3.31,3.32,3.33,3.34,3.35],\"four\":\"4\"},3.3,3.4,3.5],\"four\":\"4\"},0.3]")
> (list->json (json->list token))
"[0.1,0.2,{\"first\":\"1\",\"second\":\"2\",\"third\":[3.1,{\"first\":1,\"\nsecond\":\"2\",\"third\":[3.31,3.32,3.33,3.34,3.35],\"four\":\"4\"},3.3,3.4,3.5],\"four\":\"4\"},0.3]"
```
