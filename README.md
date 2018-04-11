# json

***The shortest code JSON Parser in history***

***All of procedures are pure functional***

***Easily and powerful hight-order fuctions***

***No external dependencies, good portability***


> A JSON-struct in here may be a list (for dict) or a vector (for array) or these infinite multi-level nesting.

> It can easily builded without tool functions, just using scheme native procedures list, cons and vector. 

> As: `(list (cons "foo" "bar")(cons "baz" (vector 1 2 3))) = {"foo": "bar", "baz": [1, 2, 3]}`

> Key must be a string for dict. For a array, the key is a number index begin with 0. 

> Value may be a string, a number, a following symbol: 'true, 'false or 'null, or an other JSON-struct.

> In this library, we use this JSON-struct in scheme code level, for easily operating.

> When we get a JSON-string from front-end, parse it to JSON-struct first before operate. (use string->json).

> Only when we need to pass it to front-end we parse it to JSON-string. (use json->string).

> The procedures json-ref, json-set, json-push, json-drop json-map is used to the struct, not to a JSON-string.

> When they accept plural keys they can jump nesting layers to directly get aim.

> They return a new JSON-struct (a value for json-ref) with specified modify and has no side effect to old one.


### 1.2.0 -> 2.0.0 Pacth update

> "Becuse the procedure json-map is efficient and powerful than old procedure json-set, I decided to replace the latter with it. Then I found il will be useful if I rewrite all the tool procedures with base of json-map."

json-set, json-push and json-drop is rewrite with higher-order function map as json-map-set, json-map-push, json-map-drop for convienient multi-operating in one time.

*But their names remain the same: json-set, json-push and json-drop.*

json-map has been deleted, its features move to json-set.



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
#(value1 value2 value3 ...)                    =>     ((0 . value1)(1 . value2)(2 . value3) ...) 
```

***array->vector***

`(vector->array list)`

```
((0 . value1)(1 . value2)(2 . value3) ...)     =>     #(value1 value2 value3 ...)
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

***following procedure is embedded high-order function map***

> They can operate multiple layer and multiple branches at the same time.

> "Verify" may be a key, a boolean #t or a procedure.

> When it's a key, it specife the branch to throw this layer.

> When it's a #t, it means operate all of branch of this layer.

> When it's a procedure, it match all of keys with this procedure in this layer.

> The procedure must accept one argument (key) and return boolean.

> When it accept plural verifys, each key will match each layer.



***json-set***

`(json-set json verify value)`

`(json-set json verify1 verify2 verify3 ... value)`

use to modify a value or values in specified location of JSON.

"value" may be a value or a procedure.

when it's a value, it will directly replace the old one's.

when it's a procedure, it must accept one argument (old value of key) and return a new value to replace.

Do NOT set #t, #f and '() to values, use 'true, 'false and 'null.



***json-push***

`(json-push json key value)`

`(json-push json verify1 verify2 verify3 ... key value)`

use to add a key-value pair to json.

it has to have a key value pair in arguments.


***json-drop***

`(json-drop json key value)`

`(json-drop json verify1 verify2 verify3 ... key value)`

use to delete a key-value pair from list.

it has to have a key value pair in arguments.



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
> (json-set (string->json token3) 2 "third" 1 "first" (lambda (x)(+ x 1)))

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

> (json-set (string->json token3) 2 "third" 1 "third" 0 (lambda (x)(* x x)))

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

> (display x)

```
(("1" .
      #((("1" . #(1 2 3 4 5))("2" . #(1 2 3 4 5))("3" . #(1 2 3 4 5)))
        (("1" . #(1 2 3 4 5))("2" . #(1 2 3 4 5))("3" . #(1 2 3 4 5)))
        (("1" . #(1 2 3 4 5))("2" . #(1 2 3 4 5))("3" . #(1 2 3 4 5)))))
  ("2" .
       #((("1" . #(1 2 3 4 5))("2" . #(1 2 3 4 5))("3" . #(1 2 3 4 5)))
        (("1" . #(1 2 3 4 5))("2" . #(1 2 3 4 5))("3" . #(1 2 3 4 5)))
        (("1" . #(1 2 3 4 5))("2" . #(1 2 3 4 5))("3" . #(1 2 3 4 5)))))
  ("3" .
       #((("1" . #(1 2 3 4 5))("2" . #(1 2 3 4 5))("3" . #(1 2 3 4 5)))
        (("1" . #(1 2 3 4 5))("2" . #(1 2 3 4 5))("3" . #(1 2 3 4 5)))
        (("1" . #(1 2 3 4 5))("2" . #(1 2 3 4 5))("3" . #(1 2 3 4 5))))))
```

> (json-set x #t #t #t 2 0)

```
(("1" .
      #((("1" . #(1 2 0 4 5))("2" . #(1 2 0 4 5))("3" . #(1 2 0 4 5)))
        (("1" . #(1 2 0 4 5))("2" . #(1 2 0 4 5))("3" . #(1 2 0 4 5)))
        (("1" . #(1 2 0 4 5))("2" . #(1 2 0 4 5))("3" . #(1 2 0 4 5)))))
  ("2" .
       #((("1" . #(1 2 0 4 5))("2" . #(1 2 0 4 5))("3" . #(1 2 0 4 5)))
         (("1" . #(1 2 0 4 5))("2" . #(1 2 0 4 5))("3" . #(1 2 0 4 5)))
         (("1" . #(1 2 0 4 5))("2" . #(1 2 0 4 5))("3" . #(1 2 0 4 5)))))
  ("3" .
       #((("1" . #(1 2 0 4 5))("2" . #(1 2 0 4 5))("3" . #(1 2 0 4 5)))
         (("1" . #(1 2 0 4 5))("2" . #(1 2 0 4 5))("3" . #(1 2 0 4 5)))
         (("1" . #(1 2 0 4 5))("2" . #(1 2 0 4 5))("3" . #(1 2 0 4 5))))))
     
```
           
> (json-set x #t (lambda (x)(> x 1)) #t 3 (lambda (x)(* x x)))

```
(("1" .
      #((("1" . #(1 2 3 4 5))("2" . #(1 2 3 4 5))("3" . #(1 2 3 4 5)))
        (("1" . #(1 2 3 4 5))("2" . #(1 2 3 4 5))("3" . #(1 2 3 4 5)))
        (("1" . #(1 2 3 16 5))("2" . #(1 2 3 16 5))("3" . #(1 2 3 16 5)))))
  ("2" .
       #((("1" . #(1 2 3 4 5))("2" . #(1 2 3 4 5))("3" . #(1 2 3 4 5)))
         (("1" . #(1 2 3 4 5))("2" . #(1 2 3 4 5))("3" . #(1 2 3 4 5)))
         (("1" . #(1 2 3 16 5))("2" . #(1 2 3 16 5))("3" . #(1 2 3 16 5)))))
  ("3" .
       #((("1" . #(1 2 3 4 5))("2" . #(1 2 3 4 5))("3" . #(1 2 3 4 5)))
         (("1" . #(1 2 3 4 5))("2" . #(1 2 3 4 5))("3" . #(1 2 3 4 5)))
         (("1" . #(1 2 3 16 5))("2" . #(1 2 3 16 5))("3" . #(1 2 3 16 5))))))
```

> (json-set x #t #t "2" 3 (lambda (x)(* x x x)))

```
(("1" .
      #((("1" . #(1 2 3 4 5))("2" . #(1 2 3 64 5))("3" . #(1 2 3 4 5)))
        (("1" . #(1 2 3 4 5))("2" . #(1 2 3 64 5))("3" . #(1 2 3 4 5)))
        (("1" . #(1 2 3 4 5))("2" . #(1 2 3 64 5))("3" . #(1 2 3 4 5)))))
  ("2" .
       #((("1" . #(1 2 3 4 5))("2" . #(1 2 3 64 5))("3" . #(1 2 3 4 5)))
         (("1" . #(1 2 3 4 5))("2" . #(1 2 3 64 5))("3" . #(1 2 3 4 5)))
         (("1" . #(1 2 3 4 5))("2" . #(1 2 3 64 5))("3" . #(1 2 3 4 5)))))
  ("3" .
       #((("1" . #(1 2 3 4 5))("2" . #(1 2 3 64 5))("3" . #(1 2 3 4 5)))
         (("1" . #(1 2 3 4 5))("2" . #(1 2 3 64 5))("3" . #(1 2 3 4 5)))
         (("1" . #(1 2 3 4 5))("2" . #(1 2 3 64 5))("3" . #(1 2 3 4 5))))))
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



