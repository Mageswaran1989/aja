/// class that stores neural network state

#ifndef AJA_NETWORK_H
#define AJA_NETWORK_H

#include <stdio.h>
#include <iostream>
//TODO: Replace with vexCL
#include <math.h>
#include <aja/ann/neuron.hpp>

class network
{
public:
    neuron
    nrn[4];
    int output[4];
    int threshld(int) ;
    void activation(int j[4]);
    network(int*,int*,int*,int*);
};

#endif
