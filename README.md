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

> When they accept plural keys / verifys they can jump nesting layers to operate multipe branchs.

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

`(json-drop json verify)`

`(json-drop json verify1 verify2 verify3 ...)`

use to delete a key-value pair from list.

the lastest argument only accept a key or a procedure who needs one argument (key) and return boolean.


***json-reduce***

`(json-set json verify procedure)`

`(json-set json verify1 verify2 verify3 ... procedure)`

it likes json-set, the diffrent is it accept only procedure for the lastest argument.

the procedure will receive two arguments: list of key of all layers `'(key1 key2 key3 ...)` and the current value.

it have to return a new value what you want to put.

be attention that, the list of keys is not the list of verifys. that's means if you use #t to pass all branch of a layer or macth a check procedure to filter them, the procedure will receive its key whitch use to pass this layer not the keys of all match path in this layer. 


### Exemples

token (see the exemple.sc) is a common json string, such as queries from a database:

> (display token)
```
[{"Number":1,
  "Name":"Laetetia",
  "Gender":"female",
  "Age":16,
  "Father":{"Number":2,"Name":"Louis","Age":48,"Revenue":1000000},
  "Mother":{"Number":3,"Name":"Lamia","Age":43,"Revenue":800000},
  "Revenue":100000,
  "Score":{
          "Math":{"School":8,"Exam":9},
          "Literature":{"School":9,"Exam":9}}},
 {"Number":4,
  "Name":"Tania",
  "Gender":"female",
  "Age":17,
  "Father":{"Number":5,"Name":"Thomas","Age":45,"Revenue":150000},
  "Mother":{"Number":6,"Name":"Jenney","Age":42,"Revenue":180000},
  "Revenue":80000,
  "Score":{
          "Math":{"School":7,"Exam":8},
          "Literature":{"School":10,"Exam":6}}},
 {"Number":7,
  "Name":"Aarnn",
  "Gender":"male",
  "Age":18,
  "Father":{"Number":8,"Name":"Alex","Age":40,"Revenue":200000},
  "Mother":{"Number":9,"Name":"Anne","Age":43,"Revenue":50000},
  "Revenue":120000,
  "Score":{
           "Math":{"School":8,"Exam":8},
           "Literature":{"School":6,"Exam":8}}}]
```

***string->json***

we tranform it to our JSON-struct
> (string->json token)
```
#((("Number" . 1) ("Name" . "Laetetia") ("Gender" . "female") ("Age" . 16)
    ("Father"
      ("Number" . 2)
      ("Name" . "Louis")
      ("Age" . 48)
      ("Revenue" . 1000000))
    ("Mother"
      ("Number" . 3)
      ("Name" . "Lamia")
      ("Age" . 43)
      ("Revenue" . 800000))
    ("Revenue" . 100000)
    ("Score"
      ("Math" ("School" . 8) ("Exam" . 9))
      ("Literature" ("School" . 9) ("Exam" . 9))))
  (("Number" . 4) ("Name" . "Tania") ("Gender" . "female") ("Age" . 17)
    ("Father"
      ("Number" . 5)
      ("Name" . "Thomas")
      ("Age" . 45)
      ("Revenue" . 150000))
    ("Mother"
      ("Number" . 6)
      ("Name" . "Jenney")
      ("Age" . 42)
      ("Revenue" . 180000))
    ("Revenue" . 80000)
    ("Score"
      ("Math" ("School" . 7) ("Exam" . 8))
      ("Literature" ("School" . 10) ("Exam" . 6))))
  (("Number" . 7) ("Name" . "Anne") ("Gender" . "female") ("Age" . 18)
    ("Father"
      ("Number" . 8)
      ("Name" . "Alex")
      ("Age" . 40)
      ("Revenue" . 200000))
    ("Mother"
      ("Number" . 9)
      ("Name" . "Sicie")
      ("Age" . 43)
      ("Revenue" . 50000))
    ("Revenue" . 120000)
    ("Score"
      ("Math" ("School" . 8) ("Exam" . 8))
      ("Literature" ("School" . 6) ("Exam" . 8)))))
```
> (define x (string->json token))

***json->string***

when we need to send JSON-string, we transform our JONS-struct back to string:
> (display (json->string x))
```
[{"Number":1,
  "Name":"Laetetia",
  "Gender":"female",
  "Age":16,
  "Father":{"Number":2,"Name":"Louis","Age":48,"Revenue":1000000},
  "Mother":{"Number":3,"Name":"Lamia","Age":43,"Revenue":800000},
  "Revenue":100000,
  "Score":{
          "Math":{"School":8,"Exam":9},
          "Literature":{"School":9,"Exam":9}}},
 {"Number":4,
  "Name":"Tania",
  "Gender":"female",
  "Age":17,
  "Father":{"Number":5,"Name":"Thomas","Age":45,"Revenue":150000},
  "Mother":{"Number":6,"Name":"Jenney","Age":42,"Revenue":180000},
  "Revenue":80000,
  "Score":{
          "Math":{"School":7,"Exam":8},
          "Literature":{"School":10,"Exam":6}}},
 {"Number":7,
  "Name":"Aarnn",
  "Gender":"male",
  "Age":18,
  "Father":{"Number":8,"Name":"Alex","Age":40,"Revenue":200000},
  "Mother":{"Number":9,"Name":"Anne","Age":43,"Revenue":50000},
  "Revenue":120000,
  "Score":{
           "Math":{"School":8,"Exam":8},
           "Literature":{"School":6,"Exam":8}}}]
```

***json-ref***

if I want to know the name of first persone:
> (json-ref x 0 "Name")
```
"Laetetia"
```
her mother's name?
> (json-ref x 0 "Mother" "Name")

```
"Lamia"
```

> (define displaydemo (lambda (x) (display (json->string x))))

what about her father?

> (displaydemo (json-ref x 0 "Father"))
```
{"Number":2,"Name":"Louis","Age":48,"Revenue":1000000}
```

***json-set***

if we want change her revenue to 80000
> (displaydemo (json-set x 0 "Revenue" 80000))
```
[{"Number":1,
  "Name":"Laetetia",
  "Gender":"female",
  "Age":16,
  "Father":{"Number":2,"Name":"Louis","Age":48,"Revenue":1000000},
  "Mother":{"Number":3,"Name":"Lamia","Age":43,"Revenue":800000},
  "Revenue":80000,
  "Score":{
          "Math":{"School":8,"Exam":9},
          "Literature":{"School":9,"Exam":9}}},
 {"Number":4,
  "Name":"Tania",
  "Gender":"female",
  "Age":17,
  "Father":{"Number":5,"Name":"Thomas","Age":45,"Revenue":150000},
  "Mother":{"Number":6,"Name":"Jenney","Age":42,"Revenue":180000},
  "Revenue":80000,
  "Score":{
          "Math":{"School":7,"Exam":8},
          "Literature":{"School":10,"Exam":6}}},
 {"Number":7,
  "Name":"Aarnn",
  "Gender":"male",
  "Age":18,
  "Father":{"Number":8,"Name":"Alex","Age":40,"Revenue":200000},
  "Mother":{"Number":9,"Name":"Anne","Age":43,"Revenue":50000},
  "Revenue":120000,
  "Score":{
           "Math":{"School":8,"Exam":8},
           "Literature":{"School":6,"Exam":8}}}]
```

we can hide everyone's mother's age:
> (displaydemo (json-set x #t "Mother" "Age" "secret"))

```
[{"Number":1,
  "Name":"Laetetia",
  "Gender":"female",
  "Age":16,
  "Father":{"Number":2,"Name":"Louis","Age":48,"Revenue":1000000},
  "Mother":{"Number":3,"Name":"Lamia","Age":"secret","Revenue":800000},
  "Revenue":100000,
  "Score":{
          "Math":{"School":8,"Exam":9},
          "Literature":{"School":9,"Exam":9}}},
 {"Number":4,
  "Name":"Tania",
  "Gender":"female",
  "Age":17,
  "Father":{"Number":5,"Name":"Thomas","Age":45,"Revenue":150000},
  "Mother":{"Number":6,"Name":"Jenney","Age":"secret","Revenue":180000},
  "Revenue":80000,
  "Score":{
          "Math":{"School":7,"Exam":8},
          "Literature":{"School":10,"Exam":6}}},
 {"Number":7,
  "Name":"Aarnn",
  "Gender":"male",
  "Age":18,
  "Father":{"Number":8,"Name":"Alex","Age":40,"Revenue":200000},
  "Mother":{"Number":9,"Name":"Anne","Age":"secret","Revenue":50000},
  "Revenue":120000,
  "Score":{
           "Math":{"School":8,"Exam":8},
           "Literature":{"School":6,"Exam":8}}}]
```

In france, the Score is 20/20, if we want to change to 100/100
> (displaydemo (json-set x #t "Score" #t #t (lambda (x) (* x 5))))
```
[{"Number":1,
  "Name":"Laetetia",
  "Gender":"female",
  "Age":16,
  "Father":{"Number":2,"Name":"Louis","Age":48,"Revenue":1000000},
  "Mother":{"Number":3,"Name":"Lamia","Age":43,"Revenue":800000},
  "Revenue":100000,
  "Score":{
          "Math":{"School":40,"Exam":45},
          "Literature":{"School":45,"Exam":45}}},
 {"Number":4,
  "Name":"Tania",
  "Gender":"female",
  "Age":17,
  "Father":{"Number":5,"Name":"Thomas","Age":45,"Revenue":150000},
  "Mother":{"Number":6,"Name":"Jenney","Age":42,"Revenue":180000},
  "Revenue":80000,
  "Score":{
          "Math":{"School":35,"Exam":40},
          "Literature":{"School":50,"Exam":30}}},
 {"Number":7,
  "Name":"Aarnn",
  "Gender":"male",
  "Age":18,
  "Father":{"Number":8,"Name":"Alex","Age":40,"Revenue":200000},
  "Mother":{"Number":9,"Name":"Anne","Age":43,"Revenue":50000},
  "Revenue":120000,
  "Score":{
           "Math":{"School":40,"Exam":40},
           "Literature":{"School":30,"Exam":40}}}]
```

***json-push***

add nationality french to all the person
> (displaydemo (json-push x #t "Nationality" "french"))
```
[{"Nationality":"french",
  "Number":1,
  "Name":"Laetetia",
  "Gender":"female",
  "Age":16,
  "Father":{"Number":2,"Name":"Louis","Age":48,"Revenue":1000000},
  "Mother":{"Number":3,"Name":"Lamia","Age":43,"Revenue":800000},
  "Revenue":100000,
  "Score":{
          "Math":{"School":8,"Exam":9},
          "Literature":{"School":9,"Exam":9}}},
 {"Nationality":"french",
  "Number":4,
  "Name":"Tania",
  "Gender":"female",
  "Age":17,
  "Father":{"Number":5,"Name":"Thomas","Age":45,"Revenue":150000},
  "Mother":{"Number":6,"Name":"Jenney","Age":42,"Revenue":180000},
  "Revenue":80000,
  "Score":{
          "Math":{"School":7,"Exam":8},
          "Literature":{"School":10,"Exam":6}}},
 {"Nationality":"french",
  "Number":7,
  "Name":"Aarnn",
  "Gender":"male",
  "Age":18,
  "Father":{"Number":8,"Name":"Alex","Age":40,"Revenue":200000},
  "Mother":{"Number":9,"Name":"Anne","Age":43,"Revenue":50000},
  "Revenue":120000,
  "Score":{
           "Math":{"School":8,"Exam":8},
           "Literature":{"School":6,"Exam":8}}}]
```

***json-drop***

or if we no longer need these Revenue:
> (displaydemo (json-drop x #t "Revenue"))
```
[{"Number":1,
  "Name":"Laetetia",
  "Gender":"female",
  "Age":16,
  "Father":{"Number":2,"Name":"Louis","Age":48,"Revenue":1000000},
  "Mother":{"Number":3,"Name":"Lamia","Age":43,"Revenue":800000},
  "Score":{
          "Math":{"School":8,"Exam":9},
          "Literature":{"School":9,"Exam":9}}},
 {"Number":4,
  "Name":"Tania",
  "Gender":"female",
  "Age":17,
  "Father":{"Number":5,"Name":"Thomas","Age":45,"Revenue":150000},
  "Mother":{"Number":6,"Name":"Jenney","Age":42,"Revenue":180000},
  "Score":{
          "Math":{"School":7,"Exam":8},
          "Literature":{"School":10,"Exam":6}}},
 {"Number":7,
  "Name":"Aarnn",
  "Gender":"male",
  "Age":18,
  "Father":{"Number":8,"Name":"Alex","Age":40,"Revenue":200000},
  "Mother":{"Number":9,"Name":"Anne","Age":43,"Revenue":50000},
  "Score":{
           "Math":{"School":8,"Exam":8},
           "Literature":{"School":6,"Exam":8}}}]
```

if we only need these Name and Age
> (displaydemo (json-drop x #t (lambda (x)(not (or (equal? x "Name")(equal? x "A
ge"))))))
```
[{"Name":"Laetetia","Age":16},
 {"Name":"Tania","Age":17},
 {"Name":"Anne","Age":18}]
```

***json-reduce***

or add the parents' gender

> (displaydemo (json-reduce x #t (lambda (x)(or (equal? x "Father")(equal? x "Mother"))) (lambda (x y)(json-push y "Gender" (if (equal?(cadr x) "Father") "male" "female")))))

```
[{"Number":1,
  "Name":"Laetetia",
  "Gender":"female",
  "Age":16,
  "Father":{"Gender":"male","Number":2,"Name":"Louis","Age":48,"Revenue":1000000},
  "Mother":{"Gender":"female","Number":3,"Name":"Lamia","Age":43,"Revenue":800000},
  "Revenue":100000,
  "Score":{
          "Math":{"School":8,"Exam":9},
          "Literature":{"School":9,"Exam":9}}},
 {"Number":4,
  "Name":"Tania",
  "Gender":"female",
  "Age":17,
  "Father":{"Gender":"male","Number":5,"Name":"Thomas","Age":45,"Revenue":150000},
  "Mother":{"Gender":"female","Number":6,"Name":"Jenney","Age":42,"Revenue":180000},
  "Revenue":80000,
  "Score":{
          "Math":{"School":7,"Exam":8},
          "Literature":{"School":10,"Exam":6}}},
 {"Number":7,
  "Name":"Aarnn",
  "Gender":"male",
  "Age":18,
  "Father":{"Gender":"male","Number":8,"Name":"Alex","Age":40,"Revenue":200000},
  "Mother":{"Gender":"female","Number":9,"Name":"Anne","Age":43,"Revenue":50000},
  "Revenue":120000,
  "Score":{
           "Math":{"School":8,"Exam":8},
           "Literature":{"School":6,"Exam":8}}}]
```


***drop layers***

if we want the total score of each displine
> (displaydemo (json-set x #t "Score" #t (lambda (x)(+ (json-ref x "School") (json-ref x "Exam")))))
```
[{"Number":1,
  "Name":"Laetetia",
  "Gender":"female",
  "Age":16,
  "Father":{"Number":2,"Name":"Louis","Age":48,"Revenue":1000000},
  "Mother":{"Number":3,"Name":"Lamia","Age":43,"Revenue":800000},
  "Revenue":100000,
  "Score":{
          "Math":17,
          "Literature":18}},
 {"Number":4,
  "Name":"Tania",
  "Gender":"female",
  "Age":17,
  "Father":{"Number":5,"Name":"Thomas","Age":45,"Revenue":150000},
  "Mother":{"Number":6,"Name":"Jenney","Age":42,"Revenue":180000},
  "Revenue":80000,
  "Score":{
          "Math":15,
          "Literature":16}},
 {"Number":7,
  "Name":"Aarnn",
  "Gender":"male",
  "Age":18,
  "Father":{"Number":8,"Name":"Alex","Age":40,"Revenue":200000},
  "Mother":{"Number":9,"Name":"Anne","Age":43,"Revenue":50000},
  "Revenue":120000,
  "Score":{
           "Math":16,
           "Literature":14}}]
```
if we only need father's information:

> (displaydemo (json-set x #t (lambda (x)(json-ref x "Father"))))

```
[{"Number":2,"Name":"Louis","Age":48,"Revenue":1000000},
 {"Number":5,"Name":"Thomas","Age":45,"Revenue":150000},
 {"Number":8,"Name":"Alex","Age":40,"Revenue":200000}]
```

set the Revenue as family's total Revenue

> (displaydemo (json-set x #t (lambda (x)(json-set x "Revenue" (lambda (y)(+ y (json-ref x "Father" "Revenue") (json-ref x "Mother" "Revenue")))))))

```
[{"Number":1,
  "Name":"Laetetia",
  "Gender":"female",
  "Age":16,
  "Father":{"Number":2,"Name":"Louis","Age":48,"Revenue":1000000},
  "Mother":{"Number":3,"Name":"Lamia","Age":43,"Revenue":800000},
  "Revenue":1900000,
  "Score":{
          "Math":{"School":8,"Exam":9},
          "Literature":{"School":9,"Exam":9}}},
 {"Number":4,
  "Name":"Tania",
  "Gender":"female",
  "Age":17,
  "Father":{"Number":5,"Name":"Thomas","Age":45,"Revenue":150000},
  "Mother":{"Number":6,"Name":"Jenney","Age":42,"Revenue":180000},
  "Revenue":410000,
  "Score":{
          "Math":{"School":7,"Exam":8},
          "Literature":{"School":10,"Exam":6}}},
 {"Number":7,
  "Name":"Aarnn",
  "Gender":"male",
  "Age":18,
  "Father":{"Number":8,"Name":"Alex","Age":40,"Revenue":200000},
  "Mother":{"Number":9,"Name":"Anne","Age":43,"Revenue":50000},
  "Revenue":370000,
  "Score":{
           "Math":{"School":8,"Exam":8},
           "Literature":{"School":6,"Exam":8}}}]
 ```
 
 set the Score to all the displines' total score
 
 > (displaydemo (json-set x #t "Score" (lambda (x)(let ((x (json-set x #t (lambda(n)(+ (json-ref n "School")(json-ref n "Exam"))))))(+ (json-ref x "Math")(json-ref x "Literature"))))))

```
[{"Number":1,
  "Name":"Laetetia",
  "Gender":"female",
  "Age":16,
  "Father":{"Number":2,"Name":"Louis","Age":48,"Revenue":1000000},
  "Mother":{"Number":3,"Name":"Lamia","Age":43,"Revenue":800000},
  "Revenue":100000,
  "Score":35},
 {"Number":4,
  "Name":"Tania",
  "Gender":"female",
  "Age":17,
  "Father":{"Number":5,"Name":"Thomas","Age":45,"Revenue":150000},
  "Mother":{"Number":6,"Name":"Jenney","Age":42,"Revenue":180000},
  "Revenue":80000,
  "Score":31},
 {"Number":7,
  "Name":"Aarnn",
  "Gender":"male",
  "Age":18,
  "Father":{"Number":8,"Name":"Alex","Age":40,"Revenue":200000},
  "Mother":{"Number":9,"Name":"Anne","Age":43,"Revenue":50000},
  "Revenue":120000,
  "Score":30}]
```
