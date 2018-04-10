(library (json json)
  (export
    string->json
    json->string
    vector->array
    array->vector
    json-ref
    json-set
    json-oper
    json-push
    json-drop
  )
  (import
     (scheme)
  )
 

 
  (define string->json
        (lambda (s)
            (read (open-input-string
                (let l
                    ((s s)(bgn 0)(end 0)(rst '())(len (string-length s))(quts? #f)(lst '(#t)))
                    (cond
                        ((= end len)
                            (apply string-append (reverse rst)))
                        ((and quts? (not (char=? (string-ref s end) #\")))
                            (l s bgn (1+ end) rst len quts? lst))
                        (else
                          (case (string-ref s end)
                            (#\{
                                (l s (1+ end) (1+ end) 
                                    (cons 
                                        (string-append 
                                            (substring s bgn end) "((" ) rst) len quts? (cons #t lst)))
                            (#\}
                                (l s (1+ end) (1+ end) 
                                    (cons 
                                        (string-append 
                                            (substring s bgn end) "))") rst) len quts? (cdr lst)))
                            (#\[
                                (l s (1+ end) (1+ end) 
                                    (cons
                                        (string-append 
                                            (substring s bgn end) "#(") rst) len quts? (cons #f lst)))
                            (#\]
                                (l s (1+ end) (1+ end) 
                                    (cons 
                                        (string-append 
                                            (substring s bgn end) ")") rst) len quts? (cdr lst)))
                            (#\:
                                (l s (1+ end) (1+ end) 
                                    (cons 
                                        (string-append 
                                            (substring s bgn end) " . ") rst) len quts? lst))
                            (#\,
                                (l s (1+ end) (1+ end) 
                                    (cons 
                                        (string-append 
                                            (substring s bgn end) 
                                            (if (car lst) ")(" " ")) rst) len quts? lst))
                            (#\"
                                (l s bgn (1+ end) rst len (not quts?) lst))
                            (else
                                (l s bgn (1+ end) rst len quts? lst))))))))))
                                
    (define json->string
        (lambda (lst)
            (define f
                (lambda (x)
                    (if (string? x) 
                        (string-append "\"" x "\"") 
                        (number->string x))))
            (define c
                (lambda (x)
                    (if (= x 0) "" ",")))
            (define q
                (lambda (x)
                    (if (vector? x) "[" "{")))
            (let l ((lst lst)(x (q lst)))
                (if (vector? lst)
                    (string-append x 
                        (let t ((len (vector-length lst))(n 0)(y ""))
                            (if (< n len)
                                (t len (+ n 1)
                                    (if (atom? (vector-ref lst n))
                                        (if (vector? (vector-ref lst n))
                                            (l (vector-ref lst n) (string-append y (c n) "["))
                                            (string-append y (c n) (f (vector-ref lst n))))
                                        (l (vector-ref lst n) (string-append y (c n) "{"))))
                                (string-append y "]"))))
                    (if (null? (cdr lst))
                        (string-append x "\"" (caar lst) "\":"
                            (if (list? (cdar lst))
                                (l (cdar lst) (q (cdar lst)))
                                (if (vector? (cdar lst))
                                    (l (cdar lst) x)
                                    (f (cdar lst)))) "}")
                        (l (cdr lst)
                            (if (list? (cdar lst))
                                (string-append x "\"" (caar lst) "\":" (l (cdar lst) "{") ",")
                                (if (vector? (cdar lst))
                                    (string-append x "\"" (caar lst) "\":" (l (cdar lst) "[") ",")
                                    (string-append x "\"" (caar lst) "\":" (f (cdar lst)) ",")))))))))
                   
    
                   
   (define vector->array
        (lambda (x)
            (let l ((x (vector->list x))(n 0))
                (cons (cons n (car x)) 
                    (if (null? (cdr x))
                        '()
                        (l (cdr x) (+ n 1)))))))


    (define array->vector
        (lambda (x)
            (list->vector         
                (let l ((x x)(n 0))
                    (cons (cdar x)
                        (if (null? (cdr x))
                            '()
                            (l (cdr x) (+ n 1))))))))               
                              
                   
    (define ref
        (lambda (x k)
            (define return
                (lambda (x)
                    (if (symbol? x)
                        (cond
                            ((symbol=? x 'true) #t)
                            ((symbol=? x 'false) #f)
                            ((symbol=? x 'null) '())
                            (else x))
                        x)))
            (if (vector? x)
                (return (vector-ref x k))
                (let l ((x x)(k k))
                    (if (null? x)
                        '()
                        (if (equal? (caar x) k)
                            (return (cdar x))
                            (l (cdr x) k)))))))

    
    (define-syntax json-ref
        (lambda (x)
            (syntax-case x ()
                ((_ j k1) #'(ref j k1))
                ((_ j k1 k2) #'(json-ref (json-ref j k1) k2))
                ((_ j k1 k2 k3 ...) #'(json-ref (json-ref j k1 k2) k3 ...))))) 
                   
                   
                   
    (define set
        (lambda (x k v)
            (if (vector? x)
                (list->vector
                    (let l ((x (vector->array x))(k k)(v v))
                        (if (null? x)
                            '()
                            (if (equal? (caar x) k)
                                (cons v (l (cdr x) k v))
                                (cons (cdar x) (l (cdr x) k v))))))
                (let l ((x x)(k k)(v v))
                    (if (null? x)
                        '()
                        (if (equal? (caar x) k)
                            (cons (cons k v)(l (cdr x) k v))
                            (cons (cons (caar x) (cdar x)) (l (cdr x) k v))))))))
                   
                   

    (define-syntax json-set
        (lambda (x)
            (syntax-case x ()
                ((_ j k1 v) #'(set j k1 v))
                ((_ j k1 k2 v) #'(json-set j k1 (json-set (ref j k1) k2 v)))
                ((_ j k1 k2 k3 v ...) #'(json-set j k1 (json-set (ref j k1) k2 k3 v ...))))))
                   
                   
                   
   
    (define oper
        (lambda (x k p)
            (if (vector? x)
                (list->vector
                    (let l ((x (vector->array x))(k k)(p p))
                        (if (null? x)
                            '()
                            (if (equal? (caar x) k)
                                (cons (p (cdar x)) (l (cdr x) k p))
                                (cons (cdar x) (l (cdr x) k p))))))
                (let l ((x x)(k k)(p p))
                    (if (null? x)
                        '()
                        (if (equal? (caar x) k)
                            (cons (cons k (p (cdar x)))(l (cdr x) k p))
                            (cons (cons (caar x) (cdar x)) (l (cdr x) k p))))))))


                   
                   

    (define-syntax json-oper
        (lambda (x)
            (syntax-case x ()
                ((_ j k1 p) #'(oper j k1 p))
                ((_ j k1 k2 p) #'(json-set j k1 (json-oper (ref j k1) k2 p)))
                ((_ j k1 k2 k3 p ...) #'(json-set j k1 (json-oper (ref j k1) k2 k3 p ...))))))
                
                   
                   
                   
                   
                   

    (define push
        (lambda (x k v)
            (if (vector? x)
                (list->vector    
                    (let l ((x (vector->array x))(k k)(v v)(b #f))
                        (if (null? x)
                            (if b '() (cons v '()))
                            (if (equal? (caar x) k)
                                (cons v (cons  (cdar x) (l (cdr x) k v #t)))
                                (cons (cdar x) (l (cdr x) k v b))))))
                (cons (cons k v) x))))


                   
    (define-syntax json-push
        (lambda (x)
            (syntax-case x ()
                ((_ j k1 v) #'(push j k1 v))
                ((_ j k1 k2 v) #'(json-set j k1 (json-push (ref j k1) k2 v)))
                ((_ j k1 k2 k3 v ...) #'(json-set j k1 (json-push (ref j k1) k2 k3 v ...))))))
                   
                   

    (define drop
        (lambda (x k)
            (if (vector? x)
                (list->vector
                    (let l ((x (vector->array x))(k k))
                        (if (null? x)
                            '()
                            (if (equal? (caar x) k)
                                (l (cdr x) k)
                                (cons (cdar x) (l (cdr x) k))))))
                (let l ((x x)(k k))
                    (if (null? x)
                        '()
                        (if (equal? (caar x) k)
                            (l (cdr x) k)
                            (cons (cons (caar x) (cdar x)) (l (cdr x) k))))))))

                   
    (define-syntax json-drop
        (lambda (x)
            (syntax-case x ()
                ((_ j k1) #'(drop j k1))
                ((_ j k1 k2) #'(json-set j k1 (json-drop (ref j k1) k2)))
                ((_ j k1 k2 k3 ...) #'(json-set j k1 (json-drop (ref j k1) k2 k3 ...))))))
                   
                                
)
