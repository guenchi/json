# json

***The shortest code JSON Parser in history***

***All of procedures are full PURE functions***

***Easy and Powerful tool functions***


> a LIST in here, is a json struct may be a list or a vector or these infinite multi-level nesting.

> In this library, we use this struct in scheme code level, to easily operating it.

> When we get a json string form front-end, parse it to this struct first before operate. (use json->list).

> Only when we need to pass it to front-end we parse it to json's string. (use list->json).

> The procedures json-ref, json-set, json-oper, json-push, json-drop is used to the struct, not to a json string.

> When they accept plural keys it can jump nesting layers to directly get aim.

> Except json-ref, they return a new list with modify specified and has no side effect to old one.

> JSON struct may easily builded using cons, list and vector. as: `(list (cons "foo" "bar")(cons "baz" (vector 1 2 3)))`

> keys must be strings. values may be  strings, numbers or following symbols: 'true, 'false or 'null.



parse rules: 

***json->list***

`(json->list json)` 

```
{key1: value1, key2: value2, key3: value3 ...}      =>     ((key1 . value2)(key2 . value2)(key3 . value3) ...)

[value1, value2, value3 ...]                        =>     #(value1 value2 value3 ...)
```

***list->json***

`(list->json list)`

```
((key1 . value2)(key2 . value2)(key3 . value3) ...)    =>    {key1: value1, key2: value2, key3: value3 ...}

#(value1 value2 value3 ...)                            =>    [value1, value2, value3 ...]
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

`(json-ref list key)`

`(json-ref list key1 key2 key3 ...)`

use to return a value of specified location of list.

```
when value is:     json-ref return:

true          =>      #t
false         =>      #f
null          =>      '()
```
when it accept plural keys:

```
(json-ref list key1 key2 key3) = 

(json-ref (json-ref (json-ref list key1) key2) key3)))
```

***json-set***

`(json-set list key value)`

`(json-set list key1 key2 key3 ... value)`

use to modify a value of specified location of list.

Do NOT set #t, #f and '() to values, use 'true, 'false and 'null.

when it accept plural keys:

```
(json-set list key1 key2 key3 value) = 

(json-set list (json-set (json-ref list key1) (json-set (json-ref (json-ref list key1) key2) key3 value)))))
```

***json-oper***

`(json-oper list key procedure)`

`(json-oper list key1 key2 key3 ... procedure)`

use to passe a procedure to specified location of list to modify it's value. 

the procedure must accept one argument (old value of key) and return a new value to replace.

when it accept plural keys:

```
(json-oper list key1 key2 key3 procedure) = 

(json-set list (json-set (json-ref list key1) (json-oper (json-ref (json-ref list key1) key2) key3 procedure)))))
```

***json-push***

`(json-push list key value)`

`(json-push list key1 key2 key3 ... value)`

use to add a key-value pair to list.

when it accept plural keys:

```
(json-push list key1 key2 key3 value) = 

(json-set list (json-set (json-ref list key1) (json-push (json-ref (json-ref list key1) key2) key3 value)))))
```

***json-drop***

`(json-drop list key value)`

`(json-drop list key1 key2 key3 ... value)`

use to delete a key-value pair from list.

when it accept plural keys:

```
(json-drop list key1 key2 key3 value) =

(json-set list (json-set (json-ref list key1) (json-drop (json-ref (json-ref list key1) key2) key3 value)))))
```


***Portable***

this library should run in any scheme implementation，at least can be easily ported.

there is some possible probleme:

1. if aim implementation don't have procedure 1+, add `(define 1+ (lambda (x) (+ 1 x)))` to code.

2. if aim implementation don't have syntax-case, rewrite json-ref, json-set, json-oper, json-push and json-drop using syntax-rules. it's simple like replace "syntax-case" with "syntax-rules" and remove #' in the code.


***exemple***

***json->list***


> (display token1)

```
{"first":"1","second":"2","third":[3.1,[3.1,3.2,3.3,3.4,3.5],3.3,3.4,3.5],"fourth":"4"}
```
> (json->list token1)

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

> (json->list token2)

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
          ("fourth" . "4"))
        3.3 3.4 3.5))
    ("fourth" . "4"))
  0.3)
```

***list->json***


> (json->list token1)

```
(("first" . "1")
  ("second" . "2")
  ("third" . #(3.1 #(3.1 3.2 3.3 3.4 3.5) 3.3 3.4 3.5))
  ("fourth" . "4"))
```

> (display (list->json (json->list token1)))

```
{"first":"1","second":"2","third":[3.1,[3.1,3.2,3.3,3.4,3.5],3.3,3.4,3.5],"fourth":"4"}
```

> (json->list token2)

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


> (display (list->json (json->list token2)))

```
{"first":1,"second":"2","third":[3.1,{"first":1,"second":"2","third":[3.31,3.32,3.33,3.34,3.35],"fourth":"4"},3.3,3.4,3.5],"fourth":"4"}
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
          ("fourth" . "4"))
        3.3 3.4 3.5))
    ("fourth" . "4"))
  0.3)
```

> (display (list->json (json->list token3)))

```
[0.1,0.2,{"first":"1","second":"2","third":[3.1,{"first":1,"second":"2","third":[3.31,3.32,3.33,3.34,3.35],"fourth":"4"},3.3,3.4,3.5],"fourth":"4"},0.3]
```

***json-ref***

> (display token3)

```
[0.1,0.2,{"first":"1","second":"2","third":[3.1,{"first":1,"second":"2","third":[3.31,3.32,3.33,3.34,3.35],"fourth":"4"},3.3,3.4,3.5],"fourth":"4"},0.3]
```

> (json-ref (json->list token3) 0)

```
0.1
```

> (json-ref (json->list token3) 1)

```
0.2
```

> (json-ref (json->list token3) 2 "first")

```
"1"
```

> (json-ref (json->list token3) 2 "third" 0)

```
3.1
```

> (json-ref (json->list token3) 2 "third" 1 "first")

```
1
```

> (json-ref (json->list token3) 2 "third" 1 "third" 0)

```
3.31
```

> (json-ref (json->list token3) 2 "third" 1 "third" 4)

```
3.35
```

