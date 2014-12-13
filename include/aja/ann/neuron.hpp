/// Class that represents a single neuron
/// A single neuron shall have
/// * weight
/// * input
/// * activation function
/// * thersold value
/// * output

#ifndef AJA_NEURON_H
#define AJA_NEURON_H

#include <stdio.h>
#include <iostream>
//TODO: Replace with vexCL
#include <math.h>

class neuron
{
protected:
    int activation;
    friend class network;
public:
    // Wkj -> connection weight from j to neuron k
    int weightv[4];
    neuron();
    neuron(int *j) ;
    int act(int, int*);
};

#endif
