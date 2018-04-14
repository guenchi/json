(define token
        "[{\"Number\":1,\"Name\":\"Laetetia\",\"Gender\":\"female\",\"Age\":16,\"Father\":{\"Number\":2,\"Name\":\"Louis\",\"Age\":48,\"revenue\":1000000},\"Mother\":{\"Number\":3,\"Name\":\"Lamia\",\"Age\":43,\"revenue\":800000},\"revenue\":100000,\"score\":{\"Math\":{\"School\":8,\"Exam\":9},\"Literature\":{\"School\":9,\"Exam\":9}}},{\"Number\":4,\"Name\":\"Tania\",\"Gender\":\"female\",\"Age\":16,\"Father\":{\"Number\":5,\"Name\":\"Thomas\",\"Age\":45,\"revenue\":150000},\"Mother\":{\"Number\":6,\"Name\":\"Jenney\",\"Age\":42,\"revenue\":180000},\"revenue\":80000,\"score\":{\"Math\":{\"School\":7,\"Exam\":8},\"Literature\":{\"School\":10,\"Exam\":6}}},{\"Number\":7,\"Name\":\"Anne\",\"Gender\":\"female\",\"Age\":16,\"Father\":{\"Number\":8,\"Name\":\"Alex\",\"Age\":40,\"revenue\":200000},\"Mother\":{\"Number\":9,\"Name\":\"Sicie\",\"Age\":43,\"revenue\":50000},\"revenue\":120000,\"score\":{\"Math\":{\"School\":8,\"Exam\":8},\"Literature\":{\"School\":6,\"Exam\":8}}}]")


(define token1
    "{\"first\":\"1\",\"second\":\"2\",\"third\":[3.1,[3.1,3.2,3.3,3.4,3.5],3.3,3.4,3.5],\"fourth\":\"4\"}")
    
(define token2
    "{\"first\":1,\"second\":\"2\",\"third\":[3.1,{\"first\":1,\"second\":\"2\",\"third\":[3.31,3.32,3.33,3.34,3.35],\"fourth\":\"4\"},3.3,3.4,3.5],\"fourth\":\"4\"}")

(define token3
    "[0.1,0.2,{\"first\":\"1\",\"second\":\"2\",\"third\":[3.1,{\"first\":1,\"second\":\"2\",\"third\":[3.31,3.32,3.33,3.34,3.35],\"fourth\":\"4\"},3.3,3.4,3.5],\"fourth\":\"4\"},0.3]")

(define x
    (list 
        (cons "1" (vector 
            (list (cons "1" (vector 1 2 3 4 5))(cons "2" (vector 1 2 3 4 5))(cons "3" (vector 1 2 3 4 5)))
            (list (cons "1" (vector 1 2 3 4 5))(cons "2" (vector 1 2 3 4 5))(cons "3" (vector 1 2 3 4 5)))
            (list (cons "1" (vector 1 2 3 4 5))(cons "2" (vector 1 2 3 4 5))(cons "3" (vector 1 2 3 4 5)))
            (list (cons "1" (vector 1 2 3 4 5))(cons "2" (vector 1 2 3 4 5))(cons "3" (vector 1 2 3 4 5)))
            (list (cons "1" (vector 1 2 3 4 5))(cons "2" (vector 1 2 3 4 5))(cons "3" (vector 1 2 3 4 5)))))
        (cons "2" (vector 
            (list (cons "1" (vector 1 2 3 4 5))(cons "2" (vector 1 2 3 4 5))(cons "3" (vector 1 2 3 4 5)))
            (list (cons "1" (vector 1 2 3 4 5))(cons "2" (vector 1 2 3 4 5))(cons "3" (vector 1 2 3 4 5)))
            (list (cons "1" (vector 1 2 3 4 5))(cons "2" (vector 1 2 3 4 5))(cons "3" (vector 1 2 3 4 5)))
            (list (cons "1" (vector 1 2 3 4 5))(cons "2" (vector 1 2 3 4 5))(cons "3" (vector 1 2 3 4 5)))
            (list (cons "1" (vector 1 2 3 4 5))(cons "2" (vector 1 2 3 4 5))(cons "3" (vector 1 2 3 4 5)))))
        (cons "3" (vector 
            (list (cons "1" (vector 1 2 3 4 5))(cons "2" (vector 1 2 3 4 5))(cons "3" (vector 1 2 3 4 5)))
            (list (cons "1" (vector 1 2 3 4 5))(cons "2" (vector 1 2 3 4 5))(cons "3" (vector 1 2 3 4 5)))
            (list (cons "1" (vector 1 2 3 4 5))(cons "2" (vector 1 2 3 4 5))(cons "3" (vector 1 2 3 4 5)))
            (list (cons "1" (vector 1 2 3 4 5))(cons "2" (vector 1 2 3 4 5))(cons "3" (vector 1 2 3 4 5)))
            (list (cons "1" (vector 1 2 3 4 5))(cons "2" (vector 1 2 3 4 5))(cons "3" (vector 1 2 3 4 5)))))))



(define y
    (list (cons "1" (vector 
        (list (cons "1" (vector (vector 1 2 3 4 5)(vector 1 2 3 4 5)(vector 1 2 3 4 5)))(cons "2" (vector (vector 1 2 3 4 5)(vector 1 2 3 4 5)(vector 1 2 3 4 5)))(cons "3" (vector (vector 1 2 3 4 5)(vector 1 2 3 4 5)(vector 1 2 3 4 5))))
        (list (cons "1" (vector (vector 1 2 3 4 5)(vector 1 2 3 4 5)(vector 1 2 3 4 5)))(cons "2" (vector (vector 1 2 3 4 5)(vector 1 2 3 4 5)(vector 1 2 3 4 5)))(cons "3" (vector (vector 1 2 3 4 5)(vector 1 2 3 4 5)(vector 1 2 3 4 5))))
        (list (cons "1" (vector (vector 1 2 3 4 5)(vector 1 2 3 4 5)(vector 1 2 3 4 5)))(cons "2" (vector (vector 1 2 3 4 5)(vector 1 2 3 4 5)(vector 1 2 3 4 5)))(cons "3" (vector (vector 1 2 3 4 5)(vector 1 2 3 4 5)(vector 1 2 3 4 5))))))
        (cons "2" (vector 
            (list (cons "1" (vector (vector 1 2 3 4 5)(vector 1 2 3 4 5)(vector 1 2 3 4 5)))(cons "2" (vector (vector 1 2 3 4 5)(vector 1 2 3 4 5)(vector 1 2 3 4 5)))(cons "3" (vector (vector 1 2 3 4 5)(vector 1 2 3 4 5)(vector 1 2 3 4 5))))
            (list (cons "1" (vector (vector 1 2 3 4 5)(vector 1 2 3 4 5)(vector 1 2 3 4 5)))(cons "2" (vector (vector 1 2 3 4 5)(vector 1 2 3 4 5)(vector 1 2 3 4 5)))(cons "3" (vector (vector 1 2 3 4 5)(vector 1 2 3 4 5)(vector 1 2 3 4 5))))
            (list (cons "1" (vector (vector 1 2 3 4 5)(vector 1 2 3 4 5)(vector 1 2 3 4 5)))(cons "2" (vector (vector 1 2 3 4 5)(vector 1 2 3 4 5)(vector 1 2 3 4 5)))(cons "3" (vector (vector 1 2 3 4 5)(vector 1 2 3 4 5)(vector 1 2 3 4 5))))))
            (cons "3" (vector 
                (list (cons "1" (vector (vector 1 2 3 4 5)(vector 1 2 3 4 5)(vector 1 2 3 4 5)))(cons "2" (vector (vector 1 2 3 4 5)(vector 1 2 3 4 5)(vector 1 2 3 4 5)))(cons "3" (vector (vector 1 2 3 4 5)(vector 1 2 3 4 5)(vector 1 2 3 4 5))))
                (list (cons "1" (vector (vector 1 2 3 4 5)(vector 1 2 3 4 5)(vector 1 2 3 4 5)))(cons "2" (vector (vector 1 2 3 4 5)(vector 1 2 3 4 5)(vector 1 2 3 4 5)))(cons "3" (vector (vector 1 2 3 4 5)(vector 1 2 3 4 5)(vector 1 2 3 4 5))))
                (list (cons "1" (vector (vector 1 2 3 4 5)(vector 1 2 3 4 5)(vector 1 2 3 4 5)))(cons "2" (vector (vector 1 2 3 4 5)(vector 1 2 3 4 5)(vector 1 2 3 4 5)))(cons "3" (vector (vector 1 2 3 4 5)(vector 1 2 3 4 5)(vector 1 2 3 4 5))))))))
