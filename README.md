# MathStudent
Solves arithmetic word problems that contains '+' and '-'

## Installation
To Run the code with eclipse, please do the following
1. checkout Optimization Package - https://github.com/ari9dam/Optimization
2. checkout nlpUtl package -https://github.com/ari9dam/nlpUtil
3. Download Wordnet and install it in C:\WordNet-3.0.
4. Download maxima equation solver from http://maxima.sourceforge.net/download.html and install in C:// or update EquationSolver.java with correct path (line 35)
5. Add Jaws-bin from /jar to classpath.
5. Checkout this package.


## NOTE
On IXL dataset, the problem "Nicole found an orange caterpillar and a green caterpillar in her backyard . The green caterpillar was 3 inches long and the orange caterpillar was 1.1666666666666667 inches long . How much longer was the green caterpillar than the orange caterpillar ?" is correctly solved. But the annotated solution is 1.8333333333333335, and the system computes 1.8333333333333333. It's an issue with rounding and it was counted as correct. The code will output 114/140 excluding this one.  Adding this it will get 115/140 correct which was reported in paper.

