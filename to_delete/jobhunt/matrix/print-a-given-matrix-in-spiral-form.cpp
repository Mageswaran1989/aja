/* This code is adopted from the solution given 
   @ http://effprog.blogspot.com/2011/01/spiral-printing-of-two-dimensional.html */
#include<iostream> 
#include <stdio.h>
#define R 3
#define C 3
 
void spiralPrint(int m, int n, int a[R][C])
{
    int i, k = 0, l = 0;
 
    /*  k - starting row index
        m - ending row index
        l - starting column index
        n - ending column index
        i - iterator
    */
 
    while (k < m && l < n)
    {
        std::cout<< "\nPrint the first row from the remaining rows \n";
        for (i = l; i < n; ++i)
        {
            printf("%d ", a[k][i]);
        }
        k++;
 
        std::cout<< "\n Print the last column from the remaining columns \n";
        for (i = k; i < m; ++i)
        {
            printf("%d ", a[i][n-1]);
        }
        n--;
 
        std::cout <<"\n Print the last row from the remaining rows \n";
        if ( k < m)
        {
            for (i = n-1; i >= l; --i)
            {
                printf("%d ", a[m-1][i]);
            }
            m--;
        }
 
        std::cout <<"\n Print the first column from the remaining columns \n";
        if (l < n)
        {
            for (i = m-1; i >= k; --i)
            {
                printf("%d ", a[i][l]);
            }
            l++;    
        }       
        
        std::cout<< k << " " << m << " | " << l << " " << n <<"\n"; 
    }
}
 
/* Driver program to test above functions */
int main()
{

    if (1 < 1) std::cout<<"If you see this getting print, then seriously C/C++ thinks 1<1 !";
    int a[R][C] = { {1,  2,  3},  {4,  5,  6},
        {7,  8,  9} /*,  10, 11, 12},
        {13, 14, 15, 16, 17, 18}*/
    };
 
    spiralPrint(R, C, a);
    return 0;
}
 
/* OUTPUT:
  1 2 3 4 5 6 12 18 17 16 15 14 13 7 8 9 10 11
*/
