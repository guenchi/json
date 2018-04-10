# json

***The shortest code JSON Parser in history***

***All of procedures are full PURE functions***

***Easy and Powerful tool functions***

***No external dependencies and Good portability***


> a JSON struct in here may be a list (for dict) or a vector (for array) or these infinite multi-level nesting.

> it may easily builded without tool functions, just using scheme native procedures list, cons and vector. 

> as: `(list (cons "foo" "bar")(cons "baz" (vector 1 2 3)))`

> keys must be strings. values may be  strings, numbers or following symbols: 'true, 'false or 'null.

> In this library, we use this JSON struct in scheme code level, to easily operating it.

> When we get a JSON string form front-end, parse it to this struct first before operate. (use string->json).

> Only when we need to pass it to front-end we parse it to JSON string. (use json->string).

> The procedures json-ref, json-set, json-oper, json-push, json-drop is used to the struct, not to a json string.

> When they accept plural keys they can jump nesting layers to directly get aim.

> Except json-ref, they return a new json with specified modify and has no side effect to old one.



parse rules: 

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


***Portable***

this library should easily port to any scheme implementation.

there is some possible probleme:

1. if aim implementation don't have procedure 1+, add `(define 1+ (lambda (x) (+ 1 x)))` to code.

2. if aim implementation don't have syntax-case, rewrite json-ref, json-set, json-oper, json-push and json-drop using syntax-rules. it's simple like replace "syntax-case" with "syntax-rules" and remove #' in the code.


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
  
> (json-push (string->json token3) 2 "third" 1 "third" 0 'true)

```
#(0.1 0.2
  (("first" . "1")
    ("second" . "2")
    ("third"
      .
      #(3.1
        (("first" . 1)
          ("second" . "2")
          ("third" . #(true 3.31 3.32 3.33 3.34 3.35))
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



