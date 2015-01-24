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
    float input_neuron_activation;
    friend class output_neuron;
public:
    input_neuron()
    {

    }
    input_neuron(float usr_weight)
    {
        weight = usr_weight;
    }

    float activation(float feature_value)
    {
        float a;
        a = feature_value * weight;
        return a;
    }
};

class output_neuron
{
protected:
    int output;
    float output_neuron_activation;
    friend class network;
public:
    output_neuron()
    {
    }

    void activation(float inputv[4], input_neuron *in_nrn)
    {
        int i;
        output_neuron_activation = 0;
        for(i=0;i<4;i++)
        {
            cout << "\nInput neuron " << i+1
                 << " value is : " << inputv[i]
                 << " weight is : " << in_nrn[i].weight;
            in_nrn[i].input_neuron_activation = in_nrn[i].activation(inputv[i]);
            cout<<"  and its activation is : "<<in_nrn[i].input_neuron_activation;
            output_neuron_activation += in_nrn[i].input_neuron_activation;
        }
        cout<<"\n\nOutput neuron activation is : "<<output_neuron_activation<<"\n";
    }

    int outvalue(float actual_ouput)
    {
        if(output_neuron_activation >= actual_ouput)
        {
            cout <<"\nThe output neuron activation exceeds the threshold value of "
                 << actual_ouput <<"\n";
            output = 1;
        }
        else
        {
            cout <<"\nThe output neuron activation is smaller than the threshold value of "
                 << actual_ouput <<"\n";
            output = 0;
        }
        cout<<"Output value is "<< output;
        return (output);
    }
};

class network
{
public:
    input_neuron input_layer_nrn[4];
    output_neuron output_layer_nrn;
    network(float feature_1,float feature_2,
            float feature_3,float feature_4)
    {
        input_layer_nrn[0] = input_neuron(feature_1);
        input_layer_nrn[1] = input_neuron(feature_2);
        input_layer_nrn[2] = input_neuron(feature_3);
        input_layer_nrn[3] = input_neuron(feature_4);

        output_layer_nrn = output_neuron();
        output_layer_nrn.output_neuron_activation = 0;
        output_layer_nrn.output = 0;
    }
};

int main (int argc, char * argv[])
{
    //intialized with the same values as that of .dat file.
    //Ignored and read directly from .dat file
    float inputv1[] = {1.95,0.27,0.69,1.25};
    float wtv1[] = {2,3,3,2};
    float wtv2[] = {3,0,6,2};

    FILE *wfile, *infile;
    int num = 0, vecnum = 0, i;
    float threshold = 7.0;

    if (argc < 2)
    {
        cerr << "Usage: perceptron_network weight.dat input.dat";
        exit(1);
    }


    //Assumption is someone has done the training for us and
    //with big heart has given those weights for our use!
    // open files
    wfile = fopen(argv[1], "r");
    infile = fopen(argv[2], "r");

    if ((wfile == NULL) || (infile == NULL))
    {
        cout << "Can't open a file\n";
        exit(1);
    }

    cout<<"\nThis program is for a perceptron network with input layer of";
    cout<<"\n4 neurons, each connected to the output neuron.\n";
    cout<<"\nThis example takes Real number as Input Signals\n";

    //create the network by calling its constructor.
    //the constructor calls neuron constructor as many times as the number of
    //neurons in input layer of the network.

    cout<<"Please enter the number of weights/vectors : ";
    cin >> vecnum;

    for (i = 1; i <= vecnum; i++)
    {
        fscanf(wfile,"%f %f %f %f\n",
               &wtv1[0],&wtv1[1],&wtv1[2],&wtv1[3]);

        network h1(wtv1[0],wtv1[1],wtv1[2],wtv1[3]);

        fscanf(infile,"%f %f %f %f \n",
               &inputv1[0],&inputv1[1],&inputv1[2],&inputv1[3]);

        cout <<"This is vector # " << i << "\n";
        cout <<"Please enter a threshold value for output neuron, eg 7.0 : ";
        cin >> threshold;

        h1.output_layer_nrn.activation(inputv1, h1.input_layer_nrn);
        h1.output_layer_nrn.outvalue(threshold);
        cout << "\n\n";
    }
    fclose(wfile);
    fclose(infile);
}
