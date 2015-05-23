#include <iostream>
#include <stdlib.h>

void print_(float* list, int length);

// Algorithm
/**
for j = 2 to A.length
    key = A[j]
    //insert A[j] into the sorted sequence A[1,..,j-1]
    i = j -1 
    while i > 0 and A[i] > j
        A[i + 1] = key
        i = i - 1
    A[i + 1] = key
**/

void insertion_sort(float* list, int length)
{
    int i,j;
    float key;
    // Index  : 0 1 2 3 4 5
    // Values : 5 2 4 6 1 3
    for(j = 1; j < length; j++)
    {
        key = list[j];
        i = j - 1;
        while (i >= 0 && list[i] > key)
        {
            list[i + 1] = list[i];
            i--; 
        }
        list[i + 1] = key;
    } 
    print_(list, length);
}

void print_(float* list, int length)
{
    std::cout << "\nSorted list : " ;
    for (int i = 0; i < length; i++)
    {
        std::cout << list[i] << " ";
    }
    std::cout <<"\n";
}

int main(int argc, char* argv[]) //argcount and argvalue
{
    if (argc < 6) // program name + 5 numbers
    {
        std::cout << "Enter atleast 5 number to sort!\n";
    }
    else
    {
        float* list = new float[argc-1];
    
        std::cout << "Entered list of numbers: ";
        for (int i = 1; i < argc; i++)
        {
            list[i-1] = atoi(argv[i]);
            std::cout << list[i-1] << " ";
        }
        std::cout << "\n";
        insertion_sort(list, argc-1);
    }
    return (0);
} 
