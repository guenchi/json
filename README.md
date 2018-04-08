# json
JSON librairy for Scheme

```
{key1:value1,key2:value2}
```

will parse to

```
((key1 . value2)(key2 . value2))
```

```
[value1,value2,value3]
```

will parse to

```
#(value1 value2 value3)
```

***json->list***


> (display token1)

```
{"first":"1","second":"2","third":[3.1,[3.1,3.2,3.3,3.4,3.5],3.3,3.4,3.5],"four":"4"}
```
> (json->list token1)

```
(("first" . "1")
  ("second" . "2")
  ("third" . #(3.1 #(3.1 3.2 3.3 3.4 3.5) 3.3 3.4 3.5))
  ("four" . "4"))
```
> (display token2)

```
{"first":1,2:"2","third":[3.1,{"first":1,"second":"2","third":[3.31,3.32,3.33,3.34,3.35],"four":"4"},3.3,3.4,3.5],"four":"4"}
```

> (json->list token2)

```
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
  ```


> (display token3)


```
[0.1,0.2,{"first":"1","second":"2","third":[3.1,{"first":1,"second":"2","third":[3.31,3.32,3.33,3.34,3.35],"four":"4"},3.3,3.4,3.5],"four":"4"},0.3]
```

> (json->list token3)


```
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

***list->json***

```
> (display token1)

{"first":"1","second":"2","third":[3.1,[3.1,3.2,3.3,3.4,3.5],3.3,3.4,3.5],"four":"4"}

> (display (list->json (json->list token1)))

{"first":"1","second":"2","third":[3.1,[3.1,3.2,3.3,3.4,3.5],3.3,3.4,3.5],"four":"4"}

> (display token2)

{"first":1,2:"2","third":[3.1,{"first":1,"second":"2","third":[3.31,3.32,3.33,3.34,3.35],"four":"4"},3.3,3.4,3.5],"four":"4"}

> (display (list->json (json->list token2)))

{"first":1,2:"2","third":[3.1,{"first":1,"second":"2","third":[3.31,3.32,3.33,3.34,3.35],"four":"4"},3.3,3.4,3.5],"four":"4"}

> (display token3)

[0.1,0.2,{"first":"1","second":"2","third":[3.1,{"first":1,"second":"2","third":[3.31,3.32,3.33,3.34,3.35],"four":"4"},3.3,3.4,3.5],"four":"4"},0.3]

> (display (list->json (json->list token3)))

[0.1,0.2,{"first":"1","second":"2","third":[3.1,{"first":1,"second":"2","third":[3.31,3.32,3.33,3.34,3.35],"four":"4"},3.3,3.4,3.5],"four":"4"},0.3]
```

***json-ref***
