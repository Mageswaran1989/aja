# We can use the concatenate function to make an ordered list of numbers, e.g.,
vec <- c( 1.9, -2.8, 5.6, 0, 3.4, -4.2 )
vec
# We can use the colon operator to make a sequence of integers, e.g.,
vec <- -5:5
vec
vec <- 5:-5
vec
# We can use the sequence function.  We have to give a starting value, and ending value, and an increment value, e.g., 
vec <- seq(-3, 3, by=0.1)
vec
# We can use the replicate function to a get a vector whose elements are all the same, e.g.,
vec <- rep(1,10)
vec
#We can concatenate two or more vectors to make a larger vector, e.g., 
vec <- c( rep(1,3), 2:5, c(6,7,8))
vec
# We can take one of the columns from a data table and make it a vector, e.g.,
vec <- Data$ACT

# Vector misc functions
# If we want to know how many elements are in the vector, we use the length function, e.g.,
length(vec)
# If we want to reference an element in the vector, we use square brackets, e.g.,
vec[5]
# If we want to reference a consecutive subset of the vector, we use the colon operator within the square brackets, e.g.,
vec[5:10]

