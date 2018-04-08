(library (json json)
  (export
    TRUE
    FALSE
    NULL
    true
    false
    null
    json->list
    list->json
    json-ref
  )
  (import
     (scheme)
  )
 
    (define TRUE #t)
    (define FALSE #f)
    (define NULL '())
    (define true #t)
    (define false #f)
    (define null '())
