#include<iostream>

using namespace std;

int main()
{
  int height = 5;
  int width = height - 1;
  int minStars = 1;
  int i,j,k;
  
  for (i = 0; i < height; i++)
  {
     for (j = width; j > i; j--)
     {
        cout << " ";
     }
     for( k = 0; k < minStars; k++)
     {
       cout << "*";
     }
     minStars+= 2;
     cout << "\n";
  }
}  
