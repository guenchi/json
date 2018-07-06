
(define token
        "[{\"Number\":1,\"Name\":\"Laetetia\",\"Gender\":\"female\",\"Age\":16,\"Father\":{\"Number\":2,\"Name\":\"Louis\",\"Age\":48,\"Revenue\":1000000},\"Mother\":{\"Number\":3,\"Name\":\"Lamia\",\"Age\":43,\"Revenue\":800000},\"Revenue\":100000,\"Score\":{\"Math\":{\"School\":8,\"Exam\":9},\"Literature\":{\"School\":9,\"Exam\":9}}},{\"Number\":4,\"Name\":\"Tania\",\"Gender\":\"female\",\"Age\":17,\"Father\":{\"Number\":5,\"Name\":\"Thomas\",\"Age\":45,\"Revenue\":150000},\"Mother\":{\"Number\":6,\"Name\":\"Jenney\",\"Age\":42,\"Revenue\":180000},\"Revenue\":80000,\"Score\":{\"Math\":{\"School\":7,\"Exam\":8},\"Literature\":{\"School\":10,\"Exam\":6}}},{\"Number\":7,\"Name\":\"Anne\",\"Gender\":\"female\",\"Age\":18,\"Father\":{\"Number\":8,\"Name\":\"Alex\",\"Age\":40,\"Revenue\":200000},\"Mother\":{\"Number\":9,\"Name\":\"Sicie\",\"Age\":43,\"Revenue\":50000},\"Revenue\":120000,\"Score\":{\"Math\":{\"School\":8,\"Exam\":8},\"Literature\":{\"School\":6,\"Exam\":8}}}]")



(json-ref (json-reduce (string->json (json->string (string->json token))) 0 "Score" "Math"  (lambda (x y) (json-set y "School" (lambda (x) (+ x 100))))) 0 "Score" "Math" "School")
