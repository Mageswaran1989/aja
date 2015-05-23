# For example, to make a 4 x 5 matrix named mat consisting of the integers 1 through 20, we can do this:
mat <- array(1:20, dim=c(4,5))
mat

# For example, to make a 2 x 3 matrix named mat consisting of the integers 1 through 6, we can do this:
mat <- matrix( 1:6, nrow=2 )
#or this
mat <- matrix ( 1:6, ncol=3 )
#we get
mat
#Note that R places the row numbers on the left and the column numbers on top.  Also note that R filled in the matrix column-by-column.  If we prefer to fill in the matrix row-by-row, we must activate the byrow setting, e.g.,
mat <- matrix( 1:6, ncol=3, byrow=TRUE )
# In place of the vector 1:6  you would place any vector containing the desired elements of the matrix.

#Extracting a Row of a Matrix
mat[1,]
#Extracting a Column of a Matrix
mat[,1]

#Extracting several Rows and/or columns
mat <- matrix( 1:25, ncol=5, byrow=TRUE )
mat
mat[1:3,2:4]

#You can combine several matrices with the same number of
#columns by joining them as rows/cols, using the rbind()/cbind()
#command
matA <- matrix(c (1 ,3 ,3 ,9 ,6 ,5) ,2 ,3)
matB <- matrix(c (9 ,8 ,8 ,2 ,9 ,0) ,2 ,3)
matA
matB
rbind(matA,matB)
rbind(matB,matA)
cbind(matA,matB)
cbind(matB,matA)

# Matrix Opeartions
# Transpose
matA
t( matA )

# Addition
matA <- matrix ( rep(2,6), nrow=2, ncol=3, byrow=FALSE )
matB <- matrix ( rep(1,6), nrow=2, ncol=3, byrow=FALSE )
matA
matB
matA + matB
matA - matB

#=====================================================================================================================

# Multiplication
#Scalar 
3 * matA
# To multiply two matrices with compatible dimensions (i.e., the number of columns of the first matrix equals the number of rows of the second matrix), we use the matrix multiplication operator %*%.  For example,
matA <- matrix( rep(1,6), nrow=2, ncol=3 )
matB <- matrix( rep(1,6), nrow=3, ncol=2 )
matA %*% matB
#If we just use the multiplication operator *,  R will multiply the corresponding elements of the two matrices, provided they have the same dimensions.  But this is not the way to multiply matrices.

#Likewise, to multiply two vectors to get their scalar (inner) product, we use the same operator, e.g.,
#Technically, we should use the transpose of a.  But R will transpose a for us rather than giving us an error message.
a %*% b

#To create the identity matrix for a desired dimension, we use the diagonal function, e.g.,
I <- diag( 5 ) #5 x 5
I

#To find the determinant of a square matrix N, use the determinant function, e.g.,
A <- matrix( c(1,2,3,0,4,5,1,0,6), nrow=3, byrow=TRUE)
det( A )

# To obtain the inverse N-1 of an invertible square matrix N, we use the solve function, e.g.,
# If the matrix is singular (not invertible), or almost singular, we get an error message.
solve( A )

# Lets test matrix inversion
#AA^-1 = I
A <- matrix (c(1,3,3,9,6,5,9,1,8), ncol=3)
solve(A)
A %*% solve(A)
#See the difference with following code
zapsmall(A %*% solve(A))









