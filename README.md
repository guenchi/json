# json

***The shortest code JSON Parser in history***

***All the procedures are pure functional***

***Easy and powerful hight-order fuctions***

***No external dependencies, good portability***


> A JSON-struct in here may be a list (for dict) or a vector (for array) or these infinite multi-layer nesting.

> It can easily be builded without tool functions, just using Scheme native procedures `list`, `cons` and `vector`. 

> As: `(list (cons "foo" "bar")(cons "baz" (vector 1 2 3))) = {"foo": "bar", "baz": [1, 2, 3]}`

> Key must be a string for dict. For an array, the key is a number index begin with 0. 

> Value may be a string, a number, a following symbol: 'true, 'false or 'null, or an other JSON-struct.

> In this library, we use this JSON-struct in scheme code level, for easily operating.

> When we get a JSON-string from front-end, parse it to JSON-struct first before operate. (use string->json).

> Only when we need to pass it to front-end we parse it to JSON-string. (use json->string).

> `json-ref`, `json-set`, `json-push`, `json-drop` and `json-reduce` is used to the struct, not to a JSON-string.

> When they accept plural keys / verifys they can jump nesting layers to directly get aim.

> They return a new JSON-struct (a value for json-ref) with specified modify and has no side effect to old one.


### Important pacth update

> When writting json-map, I relized that it's more efficient and powerful than procedure json-set and have features overlap, so I decided to replace the latter with it. Then I found il will be useful if I rewrite all the tool procedures with base of json-map.

`json-set`, `json-push` and `json-drop` is rewrite with higher-order function map as `json-map-set`, `json-map-push`, `json-map-drop` for convienient multi-branch operating in one time.

But their **names remain the same**: `json-set`, `json-push` and `json-drop`, so old style call will **not** be **affected.**

What have to **be noticed** is old json-set and json-oper was **removed** because have feature overlap with json-map and less efficient and powerful than the latter. For raison of naming conventions and in principal of change less, **json-map renamed to json-set**.

For the same raison, new procedure `json-map-reduce` named `json-reduce`.

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

***following procedures are embedded high-order function map***

> They can filter multiple layer and operate multiple branches at the same time.

> "Verify" may be a key, a boolean #t or a procedure.

> When it's a key, it specife the branch to throw this layer.

> When it's a #t, it means operate all of branch of this layer.

> When it's a procedure, it match all of keys with in this layer.

> The procedure must accept one argument (key) and return boolean.

> When it accept plural verifys, each key will match each layer.



***json-set***

`(json-set json verify value)`

`(json-set json verify1 verify2 verify3 ... value)`

use to modify a value or values in specified location of JSON.

"value" may be a value or a procedure.

when it's a value, it will directly replace the old one's.

when it's a procedure, it must accept one argument (current value of key) and return a new value to replace.

Do NOT set #t, #f and '() to values, use 'true, 'false and 'null.



***json-push***

`(json-push json key value)`

`(json-push json verify1 verify2 verify3 ... key value)`

use to add a key-value pair to json.

it has to have a key value pair in arguments.


***json-drop***

`(json-drop json key)`

`(json-drop json verify1 verify2 verify3 ... key)`

use to delete a key-value pair from list.

it has to have a key for the lastest argument.


***json-reduce***

`(json-set json verify procedure)`

`(json-set json verify1 verify2 verify3 ... procedure)`

it likes json-set, the diffrent is it accept only procedure for the lastest argument.

the procedure will receive two arguments: list of key of all layers `'(key1 key2 key3 ...)` and the current value.

it have to return a new value what you want to put.

be attention that, the list of keys is not the list of verifys. that's means if you use #t to pass all branch of a layer or macth a check procedure to filter them, the procedure will receive its key whitch use to pass this layer not the keys of all match path in this layer. 


### Exemples

token is a common json string, such as queries from a database:
> (display token)
```
[{"Number":1,
  "Name":"Laetetia",
  "Gender":"female",
  "Age":16,
  "Father":{"Number":2,"Name":"Louis","Age":48,"revenue":1000000},
  "Mother":{"Number":3,"Name":"Lamia","Age":43,"revenue":800000},
   "revenue":100000,
   "score":{
          "Math":{"School":8,"Exam":9},
          "Literature":{"School":9,"Exam":9}}},
 {"Number":4,
  "Name":"Tania",
  "Gender":"female",
  "Age":16,
  "Father":{"Number":5,"Name":"Thomas","Age":45,"revenue":150000},
  "Mother":{"Number":6,"Name":"Jenney","Age":42,"revenue":180000},
  "revenue":80000,
  "score":{
          "Math":{"School":7,"Exam":8},
          "Literature":{"School":10,"Exam":6}}},
 {"Number":7,
  "Name":"Aarnn",
  "Gender":"male",
  "Age":16,
  "Father":{"Number":8,"Name":"Alex","Age":40,"revenue":200000},
  "Mother":{"Number":9,"Name":"Anne","Age":43,"revenue":50000},
  "revenue":120000,
  "score":{
           "Math":{"School":8,"Exam":8},
           "Literature":{"School":6,"Exam":8}}}]
```
we tranform it to our JSON-struct
> (string->json token)
```
#((("Number" . 1) ("Name" . "Laetetia") ("Gender" . "female") ("Age" . 16)
    ("Father"
      ("Number" . 2)
      ("Name" . "Louis")
      ("Age" . 48)
      ("revenue" . 1000000))
    ("Mother"
      ("Number" . 3)
      ("Name" . "Lamia")
      ("Age" . 43)
      ("revenue" . 800000))
    ("revenue" . 100000)
    ("score"
      ("Math" ("School" . 8) ("Exam" . 9))
      ("Literature" ("School" . 9) ("Exam" . 9))))
  (("Number" . 4) ("Name" . "Tania") ("Gender" . "female") ("Age" . 16)
    ("Father"
      ("Number" . 5)
      ("Name" . "Thomas")
      ("Age" . 45)
      ("revenue" . 150000))
    ("Mother"
      ("Number" . 6)
      ("Name" . "Jenney")
      ("Age" . 42)
      ("revenue" . 180000))
    ("revenue" . 80000)
    ("score"
      ("Math" ("School" . 7) ("Exam" . 8))
      ("Literature" ("School" . 10) ("Exam" . 6))))
  (("Number" . 7) ("Name" . "Anne") ("Gender" . "female") ("Age" . 16)
    ("Father"
      ("Number" . 8)
      ("Name" . "Alex")
      ("Age" . 40)
      ("revenue" . 200000))
    ("Mother"
      ("Number" . 9)
      ("Name" . "Sicie")
      ("Age" . 43)
      ("revenue" . 50000))
    ("revenue" . 120000)
    ("score"
      ("Math" ("School" . 8) ("Exam" . 8))
      ("Literature" ("School" . 6) ("Exam" . 8)))))
```
> (define x (string->json token))
when we need to send JSON-string, we transform our JONS-struct back to string:
> (display (json->string x))
```
[{"Number":1,
  "Name":"Laetetia",
  "Gender":"female",
  "Age":16,
  "Father":{"Number":2,"Name":"Louis","Age":48,"revenue":1000000},
  "Mother":{"Number":3,"Name":"Lamia","Age":43,"revenue":800000},
   "revenue":100000,
   "score":{
          "Math":{"School":8,"Exam":9},
          "Literature":{"School":9,"Exam":9}}},
 {"Number":4,
  "Name":"Tania",
  "Gender":"female",
  "Age":16,
  "Father":{"Number":5,"Name":"Thomas","Age":45,"revenue":150000},
  "Mother":{"Number":6,"Name":"Jenney","Age":42,"revenue":180000},
  "revenue":80000,
  "score":{
          "Math":{"School":7,"Exam":8},
          "Literature":{"School":10,"Exam":6}}},
 {"Number":7,
  "Name":"Aarnn",
  "Gender":"male",
  "Age":16,
  "Father":{"Number":8,"Name":"Alex","Age":40,"revenue":200000},
  "Mother":{"Number":9,"Name":"Anne","Age":43,"revenue":50000},
  "revenue":120000,
  "score":{
           "Math":{"School":8,"Exam":8},
           "Literature":{"School":6,"Exam":8}}}]
```
if I want to know the name of first persone:
> (json-ref x 0 "Name")
```
"Laetetia"
```
what about her's father?
> (json-ref x 0 "Father" "Name")
```
"Louis"
```
> (define displaydemo (lambda (x) (display (json->string x))))
