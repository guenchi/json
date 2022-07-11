;;  MIT License

;  Copyright guenchi (c) 2018 - 2019 
     
;  Permission is hereby granted, free of charge, to any person obtaining a copy
;  of this software and associated documentation files (the "Software"), to deal
;  in the Software without restriction, including without limitation the rights
;  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
;  copies of the Software, and to permit persons to whom the Software is
;  furnished to do so, subject to the following conditions:
     
;  The above copyright notice and this permission notice shall be included in all
;  copies or substantial portions of the Software.
     
;  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
;  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
;  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
;  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
;  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
;  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
;  SOFTWARE.




(library (json json)
  (export
    string->json
    json->string
    json-ref
    json-set
    json-drop
    json-push
    json-reduce
  )
  (import
    (scheme)
    (only (core alist) vector->alist)
  )
 

  (define (loose-car pair-or-empty)
    (if (eq? '() pair-or-empty)
        '()
        (car pair-or-empty)))

  (define (loose-cdr pair-or-empty)
    (if (eq? '() pair-or-empty)
        '()
        (cdr pair-or-empty)))

  (define (string-length-sum strings)
    (let loop ((o 0)
               (rest strings))
      (cond
       ((eq? '() rest) o)
       (else
        (loop (+ o (string-length (car rest)))
              (cdr rest))))))

  (define (fast-string-list-append strings)
    (let* ((output-length (string-length-sum strings))
           (output (make-string output-length #\_))
           (fill 0))
      (let outer ((rest strings))
        (cond
         ((eq? '() rest) output)
         (else
          (let* ((s (car rest))
                 (n (string-length s)))
            (let inner ((i 0))
              (cond ((= i n) 'done)
                    (else
                     (string-set! output fill (string-ref s i))
                     (set! fill (+ fill 1))
                     (inner (+ i 1))))))
          (outer (cdr rest)))))))

 
  (define string->json
    (lambda (s)
      (read (open-input-string
        (let l
          ((s s)(bgn 0)(end 0)(rst '())(len (string-length s))(quts? #f)(lst '(#t)))
          (cond
            ((= end len)
              (fast-string-list-append (reverse rst)))
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
                      (substring s bgn end) "))") rst) len quts? (loose-cdr lst)))
              (#\[
                (l s (+ 1 end) (+ 1 end) 
                  (cons
                    (string-append 
                      (substring s bgn end) "#(") rst) len quts? (cons #f lst)))
              (#\]
                (l s (+ 1 end) (+ 1 end) 
                  (cons 
                    (string-append 
                      (substring s bgn end) ")") rst) len quts? (loose-cdr lst)))
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
                      (if (loose-car lst) ")(" " ")) rst) len quts? lst))
              (#\"
                (l s bgn (+ 1 end) rst len (not quts?) lst))
              (else
                (l s bgn (+ 1 end) rst len quts? lst))))))))))

                
  (define json->string
    (lambda (lst)
      (define f
        (lambda (x)
          (cond 		          
            ((string? x) (string-append "\"" x "\""))		             
            ((number? x) (number->string x))		             
            ((symbol? x) (symbol->string x)))))
      (define c
        (lambda (x)
          (if (= x 0) "" ",")))
      (let l ((lst lst)(x (if (vector? lst) "[" "{")))
        (if (vector? lst)
          (string-append x 
            (let t ((len (vector-length lst))(n 0)(y ""))
              (if (< n len)
                (t len (+ n 1)
                  (let ((k (vector-ref lst n)))
                    (if (atom? k)
                      (if (vector? k)
                        (l k (string-append y (c n) "["))
                        (string-append y (c n) (f k)))
                      (l k (string-append y (c n) "{")))))
                (string-append y "]"))))
          (let ((k (cdar lst)))
            (if (null? (cdr lst))
              (string-append x "\"" (caar lst) "\":"
                (cond 
                  ((list? k)(l k "{"))
                  ((vector? k)(l k "["))
                  (else (f k))) "}")
              (l (cdr lst)
                (cond 
                  ((list? k)(string-append x "\"" (caar lst) "\":" (l k "{") ","))
                  ((vector? k)(string-append x "\"" (caar lst) "\":" (l k "[") ","))
                  (else (string-append x "\"" (caar lst) "\":" (f k) ","))))))))))
                
                
           
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
    (syntax-rules ()
      ((_ j k1) (ref j k1))
      ((_ j k1 k2 ...) (json-ref (json-ref j k1) k2 ...))))
       
       
       
  (define vector-check?
    (lambda (vct v)
      (define l (vector-length vct))
      (let loop ((n 0))
        (if (< n l)
            (if (equal? v (vector-ref vct n))
                #t
                (loop (+ n 1)))
            #f))))



  (define check?
    (lambda (x k v)
      (let l ((x x))
      (if (null? x)
          #f
          (if (equal? (caar x) k)
              (if (equal? v (cadar x))
                  #t
                  (l (cdr x)))
              (l (cdr x))))))) 
           
           
           
  (define set
    (lambda (x v p)
      (let ((x x)(v v)(p (if (procedure? p) p (lambda (x) p))))
        (if (vector? x)
          (list->vector
            (cond 
              ((boolean? v)
                (if v
                  (let l ((x (vector->alist x))(p p))
                    (if (null? x)
                      '()
                      (cons (p (cdar x)) (l (cdr x) p))))))
              ((procedure? v)
                (let l ((x (vector->alist x))(v v)(p p))
                  (if (null? x)
                    '()
                    (if (v (caar x))
                      (cons (p (cdar x)) (l (cdr x) v p))
                      (cons (cdar x) (l (cdr x) v p))))))
              (else
                (let l ((x (vector->alist x))(v v)(p p))
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
    (syntax-rules ()
      ((_ j v1 p) (set j v1 p))
      ((_ j v1 v2 ... p) (json-set j v1 (lambda (x) (json-set x v2 ... p))))))       
           
           

  (define push
    (lambda (x k v)
      (if (vector? x)
        (if (= (vector-length x) 0)
          (vector v)
          (list->vector  
            (let l ((x (vector->alist x))(k k)(v v)(b #f))
              (if (null? x)
                (if b '() (cons v '()))
                (if (equal? (caar x) k)
                  (cons v (cons  (cdar x) (l (cdr x) k v #t)))
                  (cons (cdar x) (l (cdr x) k v b)))))))
        (cons (cons k v) x))))


           
  (define-syntax json-push
    (syntax-rules ()
      ((_ j k v) (push j k v))
      ((_ j v1 k v) (json-set j v1 (lambda (x) (json-push x k v))))
      ((_ j v1 v2 ... k v) (json-set j v1 (lambda (x) (json-push x v2 ... k v))))))  
           
           

  (define drop
    (lambda (x v)
      (if (vector? x)
        (if (> (vector-length x) 0)
          (list->vector
            (cond
              ((procedure? v)
                (let l ((x (vector->alist x))(v v))
                  (if (null? x)
                    '()
                    (if (v (caar x))
                      (l (cdr x) v)
                      (cons (cdar x) (l (cdr x) v))))))
              (else 
                (let l ((x (vector->alist x))(v v))
                  (if (null? x)
                    '()
                    (if (equal? (caar x) v)
                      (l (cdr x) v)
                      (cons (cdar x) (l (cdr x) v)))))))))
        (cond 
          ((procedure? v)
            (let l ((x x)(v v))
              (if (null? x)
                '()
                (if (v (caar x))
                  (l (cdr x) v)
                  (cons (car x) (l (cdr x) v))))))
          (else  
            (let l ((x x)(v v))
              (if (null? x)
                '()
                (if (equal? (caar x) v)
                  (l (cdr x) v)
                  (cons (car x) (l (cdr x) v))))))))))

           
  (define-syntax json-drop
    (syntax-rules ()
      ((_ j v1) (drop j v1))
      ((_ j v1 v2 ...) (json-set j v1 (lambda (x) (json-drop x v2 ...))))))
           
           
          
  (define reduce
    (lambda (x v p)
        (if (vector? x)
          (list->vector
            (cond 
              ((boolean? v)
                (if v
                  (let l ((x (vector->alist x))(p p))
                    (if (null? x)
                      '()
                      (cons (p (caar x) (cdar x)) (l (cdr x) p))))))
              ((procedure? v)
                (let l ((x (vector->alist x))(v v)(p p))
                  (if (null? x)
                    '()
                    (if (v (caar x))
                      (cons (p (caar x) (cdar x)) (l (cdr x) v p))
                      (cons (cdar x) (l (cdr x) v p ))))))
              (else
                (let l ((x (vector->alist x))(v v)(p p))
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
    (syntax-rules ()
      ((_ j v1 p) (reduce j v1 (lambda (x y)(p (cons x '()) y))))
      ((_ j v1 v2 ... p) (json-reduce j v1 (lambda (x y) (json-reduce y v2 ... (lambda (n m)(p (cons (car x) n) m))))))))
       
       

           
                
)
