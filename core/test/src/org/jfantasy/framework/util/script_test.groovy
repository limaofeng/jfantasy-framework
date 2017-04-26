class Question {
    def id
    def value
    def score = 0
}

println "Welcome to $language"

def q1 = new Question(['value':'1'])
def q2 = new Question(['value':'1'])
def q3 = new Question(['value':30])


switch (q1.value){
    case '1':
        q1.score = 1
        break
    case '2':
        q1.score = 2
        break
}

switch (q2.value){
    case '1':
        if(q3.value <= 30){
            q3.score = 20
        }
        break
    case '2':
        if(q3.value >= 30){
            q3.score = 20
        }
        break
}

return [A:q1.score + q3.score]