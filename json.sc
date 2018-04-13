(library (json json)
  (export
    string->json
    json->string
    vector->array
    array->vector
    json-ref
    json-set
    json-push
    json-drop
    json-reduce
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
                            (l s bgn (+ 1 end) rst len quts? lst))
                        (else
                          (case (string-ref s end)
                            (#\{
                                (l s (+ 1 end) (+ 1 end) 
                                    (cons 
                                        (string-append 
                                            (substring s bgn end) "((" ) rst) len quts? (cons #t lst)))
                            (#\}
                                (l s (+ 1 end) (+ 1 end) 
                                    (cons 
                                        (string-append 
                                            (substring s bgn end) "))") rst) len quts? (cdr lst)))
                            (#\[
                                (l s (+ 1 end) (+ 1 end) 
                                    (cons
                                        (string-append 
                                            (substring s bgn end) "#(") rst) len quts? (cons #f lst)))
                            (#\]
                                (l s (+ 1 end) (+ 1 end) 
                                    (cons 
                                        (string-append 
                                            (substring s bgn end) ")") rst) len quts? (cdr lst)))
                            (#\:
                                (l s (+ 1 end) (+ 1 end) 
                                    (cons 
                                        (string-append 
                                            (substring s bgn end) " . ") rst) len quts? lst))
                            (#\,
                                (l s (+ 1 end) (+ 1 end) 
                                    (cons 
                                        (string-append 
                                            (substring s bgn end) 
                                            (if (car lst) ")(" " ")) rst) len quts? lst))
                            (#\"
                                (l s bgn (+ 1 end) rst len (not quts?) lst))
                            (else
                                (l s bgn (+ 1 end) rst len quts? lst))))))))))

                                
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
        (lambda (x v p)
            (let ((x x)(v v)(p (if (procedure? p) p (lambda (x) p))))
                (if (vector? x)
                    (list->vector
                        (cond 
                            ((boolean? v)
                                (if v
                                    (let l ((x (vector->array x))(p p))
                                        (if (null? x)
                                            '()
                                            (cons (p (cdar x)) (l (cdr x) p))))))
                            ((procedure? v)
                                (let l ((x (vector->array x))(v v)(p p))
                                    (if (null? x)
                                        '()
                                        (if (v (caar x))
                                            (cons (p (cdar x)) (l (cdr x) v p))
                                            (cons (cdar x) (l (cdr x) v p))))))
                            (else
                                (let l ((x (vector->array x))(v v)(p p))
                                    (if (null? x)
                                        '()
                                        (if (equal? (caar x) v)
                                            (cons (p (cdar x)) (l (cdr x) v p))
                                            (cons (cdar x) (l (cdr x) v p))))))))
                    (cond
                        ((boolean? v)
                            (if v
                                (let l ((x x)(p p))
                                    (if (null? x)
                                        '()
                                        (cons (cons (caar x) (p (cdar x)))(l (cdr x) p))))))
                        ((procedure? v)
                            (let l ((x x)(v v)(p p))
                                (if (null? x)
                                    '()
                                    (if (v (caar x))
                                        (cons (cons (caar x) (p (cdar x)))(l (cdr x) v p))
                                        (cons (car x) (l (cdr x) v p))))))
                        (else
                            (let l ((x x)(v v)(p p))
                                (if (null? x)
                                    '()
                                    (if (equal? (caar x) v)
                                        (cons (cons v (p (cdar x)))(l (cdr x) v p))
                                        (cons (car x) (l (cdr x) v p)))))))))))


    (define-syntax json-set
        (lambda (x)
            (syntax-case x ()
                ((_ j v1 p) #'(set j v1 p))
                ((_ j v1 v2 p) #'(json-set j v1 (lambda (x) (json-set x v2 p))))
                ((_ j v1 v2 v3 ... p) #'(json-set j v1 (lambda (x) (json-set x v2 v3 ... p)))))))               
                   
                   

    (define push
        (lambda (x k v)
            (if (vector? x)
                (if (= (vector-length x) 0)
                    (vector v)
                    (list->vector    
                        (let l ((x (vector->array x))(k k)(v v)(b #f))
                            (if (null? x)
                                (if b '() (cons v '()))
                                (if (equal? (caar x) k)
                                    (cons v (cons  (cdar x) (l (cdr x) k v #t)))
                                    (cons (cdar x) (l (cdr x) k v b)))))))
                (cons (cons k v) x))))


                   
    (define-syntax json-push
        (lambda (x)
            (syntax-case x ()
                ((_ j k1 v) #'(push j k1 v))
                ((_ j k1 k2 v) #'(json-set j k1 (lambda (x) (json-push x k2 v))))
                ((_ j k1 k2 k3 ... v) #'(json-set j k1 (lambda (x) (json-push x k2 k3 ... v))))))) 
                   
                   

    (define drop
        (lambda (x k)
            (if (vector? x)
                (if (> (vector-length x) 0)
                    (list->vector
                        (let l ((x (vector->array x))(k k))
                            (if (null? x)
                                '()
                                (if (equal? (caar x) k)
                                    (l (cdr x) k)
                                    (cons (cdar x) (l (cdr x) k)))))))
                (let l ((x x)(k k))
                    (if (null? x)
                        '()
                        (if (equal? (caar x) k)
                            (l (cdr x) k)
                            (cons (car x) (l (cdr x) k))))))))

                   
    (define-syntax json-drop
        (lambda (x)
            (syntax-case x ()
                ((_ j k1) #'(drop j k1))
                ((_ j k1 k2) #'(json-set j k1 (lambda (x) (json-drop x k2))))
                ((_ j k1 k2 k3 ...) #'(json-set j k1 (lambda (x) (json-drop x k2 k3 ...)))))))
                   
                   
                  
    (define reduce
        (lambda (x v p)
                (if (vector? x)
                    (list->vector
                        (cond 
                            ((boolean? v)
                                (if v
                                    (let l ((x (vector->array x))(p p))
                                        (if (null? x)
                                            '()
                                            (cons (p (caar x) (cdar x)) (l (cdr x) p))))))
                            ((procedure? v)
                                (let l ((x (vector->array x))(v v)(p p))
                                    (if (null? x)
                                        '()
                                        (if (v (caar x))
                                            (cons (p (caar x) (cdar x)) (l (cdr x) v p))
                                            (cons (cdar x) (l (cdr x) v p ))))))
                            (else
                                (let l ((x (vector->array x))(v v)(p p))
                                    (if (null? x)
                                        '()
                                        (if (equal? (caar x) v)
                                            (cons (p (caar x) (cdar x)) (l (cdr x) v p))
                                            (cons (cdar x) (l (cdr x) v p ))))))))
                    (cond
                        ((boolean? v)
                            (if v
                                (let l ((x x)(p p))
                                    (if (null? x)
                                        '()
                                        (cons (cons (caar x) (p (caar x) (cdar x)))(l (cdr x) p))))))
                        ((procedure? v)
                            (let l ((x x)(v v)(p p))
                                (if (null? x)
                                    '()
                                    (if (v (caar x))
                                        (cons (cons (caar x) (p (caar x) (cdar x)))(l (cdr x) v p))
                                        (cons (car x) (l (cdr x) v p ))))))
                        (else
                            (let l ((x x)(v v)(p p))
                                (if (null? x)
                                    '()
                                    (if (equal? (caar x) v)
                                        (cons (cons v (p v (cdar x)))(l (cdr x) v p))
                                        (cons (car x) (l (cdr x) v p))))))))))


    (define-syntax json-reduce
        (lambda (x)
            (syntax-case x ()
                ((_ j v1 p) #'(reduce j v1 (lambda (x y)(p (cons x '()) y))))
                ((_ j v1 v2 p) #'(json-reduce j v1 (lambda (x y) (json-reduce y v2 (lambda (n m)(p (cons (car x) n) m))))))
                ((_ j v1 v2 v3 ... p) #'(json-reduce j v1 (lambda (x y) (json-reduce y v2 v3 ... (lambda (n m)(p (cons (car x) n) m)))))))))            
                   
                   
                                
)
