# json

***The shortest code JSON Parser in history***

***All of procedures are pure functional***

***Easily and powerful features***

***No external dependencies, good portability***


> A JSON-struct in here may be a list (for dict) or a vector (for array) or these infinite multi-level nesting.

> It can easily builded without tool functions, just using scheme native procedures list, cons and vector. 

> As: `(list (cons "foo" "bar")(cons "baz" (vector 1 2 3))) = {"foo": "bar", "baz": [1, 2, 3]}`

> KEY must be a string for dict. For a array, the key is a number index begin with 0. 

> VALUE may be a string, a number, a following symbol: 'true, 'false or 'null, or an other JSON struct.

> In this library, we use this JSON-struct in scheme code level, for easily operating.

> When we get a JSON-string form front-end, parse it to JSON-struct first before operate. (use string->json).

> Only when we need to pass it to front-end we parse it to JSON-string. (use json->string).

> The procedures json-ref, json-set, json-oper, json-push, json-drop is used to the struct, not to a json string.

> When they accept plural keys they can jump nesting layers to directly get aim.

> They return a new JSON-struct (a value for json-ref) with specified modify and has no side effect to old one.


### Parse rules: 

***string->json***

`(string->json string)` 

```
"{key1: value1, key2: value2, key3: value3 ...}"      =>     ((key1 . value2)(key2 . value2)(key3 . value3) ...)

"[value1, value2, value3 ...]"                        =>     #(value1 value2 value3 ...)
```

***json->string***

`(json->string json)`

```
((key1 . value2)(key2 . value2)(key3 . value3) ...)    =>    "{key1: value1, key2: value2, key3: value3 ...}"

#(value1 value2 value3 ...)                            =>    "[value1, value2, value3 ...]"
```

if you want more libert to operate vector, use:

***vector->array***

`(vector->array vector)`


```
#(value1 value2 value3 ...)                    =>     ((0 . value2)(1 . value2)(2 . value2) ...) 
```

***array->vector***

`(vector->array list)`

```
((0 . value2)(1 . value2)(2 . value2) ...)      =>     #(value1 value2 value3 ...)
```


### Tools

***json-ref***

`(json-ref json key)`

`(json-ref json key1 key2 key3 ...)`

use to return the value of a key in specified location of JSON.

```
when value is:     json-ref return:

true          =>      #t
false         =>      #f
null          =>      '()
```
when it accept plural keys:

```
(json-ref json key1 key2 key3) = 

(json-ref (json-ref (json-ref json key1) key2) key3)))
```

***json-set***

`(json-set json key value)`

`(json-set json key1 key2 key3 ... value)`

use to modify the value of a key in specified location of JSON.

Do NOT set #t, #f and '() to values, use 'true, 'false and 'null.

when it accept plural keys:

```
(json-set json key1 key2 key3 value) = 

(json-set json (json-set (json-ref json key1) (json-set (json-ref (json-ref json key1) key2) key3 value)))))
```

***json-oper***

`(json-oper json key procedure)`

`(json-oper json key1 key2 key3 ... procedure)`

use to passe a procedure to specified location of json to modify the value of a key. 

the procedure must accept one argument (old value of key) and return a new value to replace.

when it accept plural keys:

```
(json-oper json key1 key2 key3 procedure) = 

(json-set json (json-set (json-ref json key1) (json-oper (json-ref (json-ref json key1) key2) key3 procedure)))))
```

***json-push***

`(json-push json key value)`

`(json-push json key1 key2 key3 ... value)`

use to add a key-value pair to json.

when it accept plural keys:

```
(json-push json key1 key2 key3 value) = 

(json-set json (json-set (json-ref json key1) (json-push (json-ref (json-ref json key1) key2) key3 value)))))
```

***json-drop***

`(json-drop json key value)`

`(json-drop json key1 key2 key3 ... value)`

use to delete a key-value pair from list.

when it accept plural keys:

```
(json-drop json key1 key2 key3 value) =

(json-set json (json-set (json-ref json key1) (json-drop (json-ref (json-ref json key1) key2) key3 value)))))
```



### Exemples

***string->json***


> (display token1)

```
{"first":"1","second":"2","third":[3.1,[3.1,3.2,3.3,3.4,3.5],3.3,3.4,3.5],"fourth":"4"}
```
> (string->json token1)

```
(("first" . "1")
  ("second" . "2")
  ("third" . #(3.1 #(3.1 3.2 3.3 3.4 3.5) 3.3 3.4 3.5))
  ("fourth" . "4"))
```
> (display token2)

```
{"first":1,"second":"2","third":[3.1,{"first":1,"second":"2","third":[3.31,3.32,3.33,3.34,3.35],"fourth":"4"},3.3,3.4,3.5],"fourth":"4"}
```

> (string->json token2)

```
(("first" . 1)
  ("second" . "2")
  ("third"
    .
    #(3.1
      (("first" . 1)
        ("second" . "2")
        ("third" . #(3.31 3.32 3.33 3.34 3.35))
        ("fourth" . "4"))
      3.3 3.4 3.5))
  ("fourth" . "4"))
```

> (display token3)


```
[0.1,0.2,{"first":"1","second":"2","third":[3.1,{"first":1,"second":"2","third":[3.31,3.32,3.33,3.34,3.35],"fourth":"4"},3.3,3.4,3.5],"fourth":"4"},0.3]
```

> (string->json token3)


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
          ("fourth" . "4"))
        3.3 3.4 3.5))
    ("fourth" . "4"))
  0.3)
```

***json->string***


> (string->json token1)

```
(("first" . "1")
  ("second" . "2")
  ("third" . #(3.1 #(3.1 3.2 3.3 3.4 3.5) 3.3 3.4 3.5))
  ("fourth" . "4"))
```

> (display (json->string (string->json token1)))

```
{"first":"1","second":"2","third":[3.1,[3.1,3.2,3.3,3.4,3.5],3.3,3.4,3.5],"fourth":"4"}
```

> (string->json token2)

```
(("first" . 1)
  ("second" . "2")
  ("third"
    .
    #(3.1
      (("first" . 1)
        ("second" . "2")
        ("third" . #(3.31 3.32 3.33 3.34 3.35))
        ("fourth" . "4"))
      3.3 3.4 3.5))
  ("fourth" . "4"))
```


> (display (json->string (string->json token2)))

```
{"first":1,"second":"2","third":[3.1,{"first":1,"second":"2","third":[3.31,3.32,3.33,3.34,3.35],"fourth":"4"},3.3,3.4,3.5],"fourth":"4"}
```

> (string->json token3)


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
          ("fourth" . "4"))
        3.3 3.4 3.5))
    ("fourth" . "4"))
  0.3)
```

> (display (json->string (string->json token3)))

```
[0.1,0.2,{"first":"1","second":"2","third":[3.1,{"first":1,"second":"2","third":[3.31,3.32,3.33,3.34,3.35],"fourth":"4"},3.3,3.4,3.5],"fourth":"4"},0.3]
```

***json-ref***
  
> (json-ref (string->json token3) 0)

```
0.1
```

> (json-ref (string->json token3) 2 "third" 1 "third" 0)

```
3.31
```

***json-set***

> (json-set (string->json token3) 2 0.2)

```
#(0.1 0.2 0.2 0.3)
```

> (json-set (string->json token3) 2 "third" 1 "third" 0  'true)

```
#(0.1 0.2
  (("first" . "1")
    ("second" . "2")
    ("third"
      .
      #(3.1
        (("first" . 1)
          ("second" . "2")
          ("third" . #(true 3.32 3.33 3.34 3.35))
          ("fourth" . "4"))
        3.3 3.4 3.5))
    ("fourth" . "4"))
  0.3)
```

***json-oper***

> (json-oper (string->json token3) 2 "third" 1 "first" (lambda (x)(+ x 1)))

```
#(0.1 0.2
  (("first" . "1")
    ("second" . "2")
    ("third"
      .
      #(3.1
        (("first" . 2)
          ("second" . "2")
          ("third" . #(3.31 3.32 3.33 3.34 3.35))
          ("fourth" . "4"))
        3.3 3.4 3.5))
    ("fourth" . "4"))
  0.3)
```

> (json-oper (string->json token3) 2 "third" 1 "third" 0 (lambda (x)(* x x)))

```
#(0.1 0.2
  (("first" . "1")
    ("second" . "2")
    ("third"
      .
      #(3.1
        (("first" . 1)
          ("second" . "2")
          ("third" . #(10.956100000000001 3.32 3.33 3.34 3.35))
          ("fourth" . "4"))
        3.3 3.4 3.5))
    ("fourth" . "4"))
  0.3)
```

***json-push***

> (json-push (string->json token3) 2 'true)

```
#(0.1 0.2 true
  (("first" . "1")
    ("second" . "2")
    ("third"
      .
      #(3.1
        (("first" . 1)
          ("second" . "2")
          ("third" . #(3.31 3.32 3.33 3.34 3.35))
          ("fourth" . "4"))
        3.3 3.4 3.5))
    ("fourth" . "4"))
  0.3)
```
  
> (json-push (string->json token3) 2 "third" 1 "third" 3 'true)

```
#(0.1 0.2
  (("first" . "1")
    ("second" . "2")
    ("third"
      .
      #(3.1
        (("first" . 1)
          ("second" . "2")
          ("third" . #(3.31 3.32 3.33 true 3.34 3.35))
          ("fourth" . "4"))
        3.3 3.4 3.5))
    ("fourth" . "4"))
  0.3)
```
  
***json-drop***
  
> (json-drop (string->json token3) 2)

```
#(0.1 0.2 0.3)
```

> (json-drop (string->json token3) 2 "third" 1 "third" 0)

```
#(0.1 0.2
  (("first" . "1")
    ("second" . "2")
    ("third"
      .
      #(3.1
        (("first" . 1)
          ("second" . "2")
          ("third" . #(3.32 3.33 3.34 3.35))
          ("fourth" . "4"))
        3.3 3.4 3.5))
    ("fourth" . "4"))
  0.3)
```



