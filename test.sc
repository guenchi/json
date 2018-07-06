

(json-ref (json-reduce x 0 "Score" "Math"  (lambda (x y) (json-set y "School" (lambda (x) (+ x 100))))) 0 "Score" "Math" "School")
