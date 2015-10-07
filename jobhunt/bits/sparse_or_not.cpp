/*
Check if a given number is sparse or not
A number is said to be a sparse number if in binary representation of the number no two or more consecutive bits are set. Write a function to check if a given number is Sparse or not.

Input:  x  = 72
Output: true
Explanation:  Binary representation of 72 is 01001000. 
There are no two consecutive 1's in binary representation

Input:  x  = 12
Output: false
Explanation:  Binary representation of 12 is 1100. 
Third and fourth bits (from end) are set.
*/

#include<iostream>

int main()
{
  int input_number, shiffted_number;
  std::cout<<"Enter a number : ";
  std::cin>>input_number;
  if((input_number & (input_number >> 1)) == 0)
   std::cout<<"Parse\n";
  else
   std::cout<<"Dense\n";
}
