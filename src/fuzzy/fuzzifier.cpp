// fuzzfier.cpp
// program to fuzzify data

#if _WIN32
#include <iostream.h>
#include <stdlib.h>
#include <time.h>
#include <string.h>
#include <fuzzfier.h>
#else
#include <iostream>
#include <stdlib.h>
#include <time.h>
#include <string.h>
#include <aja/fuzzy/fuzzifier.hpp>
#endif

using namespace std;

void Category::setname(char *n)
{
    strcpy(name,n);
}

char * Category::getname()
{
    return name;
}

void Category::setval(float &h, float &m, float &l)
{
    highval=h;
    midval=m;
    lowval=l;
}

float Category::getlowval()
{
    return lowval;
}
float Category::getmidval()
{
    return midval;
}
float Category::gethighval()
{
    return highval;
}

float Category::getshare(const float & input)
{
    // this member function returns the relative membership
    // of an input in a Category, with a maximum of 1.0
    float output;
    float midlow, highmid;
    midlow=midval-lowval;
    highmid=highval-midval;
    // if outside the range, then output=0
    if ((input <= lowval) || (input >= highval))
    {
        output=0;
    }
    else
    {
        if (input > midval)
        {
            output=(highval-input)/highmid;
        }
        else
        {
            if (input==midval)
            {
                output=1.0;
            }
            else
            {
                output=(input-lowval)/midlow;
            }
        }
    }
    return output;
}

int randomnum(int maxval)
{
    // random number generator
    // will return an integer up to maxval
    srand ((unsigned)time(NULL));
    return rand() % maxval;
}
