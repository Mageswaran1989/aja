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

void category::setname(char *n)
{
    strcpy(name,n);
}

char * category::getname()
{
    return name;
}

void category::setval(float &h, float &m, float &l)
{
    highval=h;
    midval=m;
    lowval=l;
}

float category::getlowval()
{
    return lowval;
}
float category::getmidval()
{
    return midval;
}
float category::gethighval()
{
    return highval;
}

float category::getshare(const float & input)
{
    // this member function returns the relative membership
    // of an input in a category, with a maximum of 1.0
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
