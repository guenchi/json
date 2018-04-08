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
 
 
  (define json->list
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
                                
                                
                                
                                
)
