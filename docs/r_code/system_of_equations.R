A <- array(c(3,2,-1,2,-2,0.5,-1,4,-1), dim=c(3,3))
b <- c(1,-2,0)
solve(A,b)

A %*% solve(A,b)
#A * x = b
zapsmall(A %*% solve(A,b))
# A^-1 * b = x
solve(A) %*% b 
