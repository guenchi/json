# json

### [Manual](manual.html)

***The shortest code JSON Parser in history***

***All the procedures are purely functional***

***Easy and powerful hight-order fuctions***

***No external dependencies, good portability***


> A JSON-struct in here may be a association-list (for dict) or a vector (for array) or these infinite nesting.

> It can easily be builded without tool functions, just need `` ` # ( . ) `` five symbols.

> such as: `` `(("foo" . "bar")("baz" . #(1 2 3))) = {"foo": "bar", "baz": [1, 2, 3]}``

> Key must be a string for dict. For an array, the key is a number index begin with 0. 

> Value may be a string, a number, a following symbol: 'true, 'false or 'null, or an other JSON-struct.

> In this library, we use this JSON-struct in scheme code level, for easily operating.

> When we get a JSON-string from front-end, parse it to JSON-struct first before operate. (use `string->json`).

> Only when we need to pass it to front-end we parse it to JSON-string. (use `json->string`).

> `json-ref`, `json-set`, `json-push`, `json-drop` and `json-reduce` is used to the struct, not to a JSON-string.

> When they accept plural keys / verifys they can jump nesting layers to operate multipe branchs.

> They return a new JSON-struct (a value for json-ref) with specified modify and has no side effect to old one.


### Important pacth update

> From 1.5.6, the procedures `array->vector` and `vector->array` move to [Core](https://github.com/guenchi/core) so this json library is dependent it. For reason of portability, you can just copy procedure `vector->alist`'s code into library to remove the external dependencie.

> When writting json-map, I relized that it's more efficient and powerful than procedure json-set and have features overlap, so I decided to replace the latter with it. Then I found il will be useful if I rewrite all the tool procedures with base of json-map.

`json-set`, `json-push` and `json-drop` is rewrite with higher-order function map as `json-map-set`, `json-map-push`, `json-map-drop` for convienient multi-branch operating in one time.

But their **names remain the same**: `json-set`, `json-push` and `json-drop`, so old style call will **not** be **affected.**

What have to **be noticed** is old json-set and json-oper was **removed** because have feature overlap with json-map and less efficient and powerful than the latter. For raison of naming conventions and in principal of change less, **json-map renamed to json-set**.

For the same raison, new procedure `json-map-reduce` named `json-reduce`. 

