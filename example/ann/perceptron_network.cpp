#include <stdio.h>
#include <iostream>
#include <math.h>
#include "stdio.h"
#include "stdlib.h"

using namespace std;

class input_neuron
{
protected:
    float weight;
    float activation;
    friend class output_neuron;
public:
    input_neuron()
    {

    }
    input_neuron(float j)
    {
        weight= j;
    }

    float act(float x)
    {
        float a;
        a = x*weight;
        return a;
    }
};

class output_neuron
{
protected:
    int output;
    float activation;
    friend class network;
public:
    output_neuron()
    {
    }

    void actvtion(float x[4], input_neuron *nrn)
    {
        int i;
        activation = 0;
        for(i=0;i<4;i++)
        {
            cout<<"\nweight for neuron "<<i+1<<" is"<<nrn[i].weight;
            nrn[i].activation = nrn[i].act(inputv[i]);
            cout<<"activation is"<<nrn[i].activation;
            activation += nrn[i].activation;
        }
        cout<<"\n\nactivation is "<<activation<<"\n";
    }

    int outvalue(float j)
    {
        if(activation>=j)
        {
            cout<<"\nthe output neuron activation \
                  exceeds the threshold value of "<<j<<"\n";
                  output = 1;
        }
        else
        {
            cout<<"\nthe output neuron activation \
                  is smaller than the threshold value of "<<j<<"\n";
                  output = 0;
        }
        cout<<" output value is "<< output;
        return (output);
    }
};

class network
{
public:
    input_neuron
    nrn[4];
    output_neuron
    onrn;
    network(float a,float b,float c,float d)
    {
        nrn[0] = input_neuron(a) ;
        nrn[1] = input_neuron(b) ;
        nrn[2] = input_neuron(c) ;
        nrn[3] = input_neuron(d) ;
        onrn = output_neuron();
        onrn.activation = 0;
        onrn.output = 0;
    }
};

int main (int argc, char * argv[])
{
    float inputv1[]= {1.95,0.27,0.69,1.25};
    float wtv1[]= {2,3,3,2};
    float wtv2[]= {3,0,6,2};

    FILE * wfile, * infile;
    int num=0, vecnum=0, i;
    float threshold = 7.0;

    if (argc < 2)
    {
        cerr << "Usage: percept Weightfile Inputfile";
        exit(1);
    }

    // open files
    wfile= fopen(argv[1], "r");
    infile= fopen(argv[2], "r");

    if ((wfile == NULL) || (infile == NULL))
    {
        cout << " Can't open a file\n";
        exit(1);
    }

    cout<<"\nThis program is for a perceptron network with input layer of";
    cout<<"\n4 neurons, each connected to the output neuron.\n";
    cout<<"\nThis example takes Real number as Input Signals\n";

    //create the network by calling its constructor.
    //the constructor calls neuron constructor as many times as the number of
    //neurons in input layer of the network.
    cout<<"please enter the number of weights/vectors \n";
    cin >> vecnum;

    for (i=1;i<=vecnum;i++)
    {
        fscanf(wfile,"%f %f %f %f\n", &wtv1[0],&wtv1[1],&wtv1[2],&wtv1[3]);
        network h1(wtv1[0],wtv1[1],wtv1[2],wtv1[3]);
        fscanf(infile,"%f %f %f %f \n",
               &inputv1[0],&inputv1[1],&inputv1[2],&inputv1[3]);
        cout<<"this is vector # " << i << "\n";
        cout << "please enter a threshold value, eg 7.0\n";
        cin >> threshold;
        h1.onrn.actvtion(inputv1, h1.nrn);
        h1.onrn.outvalue(threshold);
        cout<<"\n\n";
    }

    fclose(wfile);
    fclose(infile);
}
