#include <iostream>
#include <vector>
#include <cstdlib>
#include <cassert>
#include <cmath>


using namespace std;

class Neuron;

typedef std::vector<Neuron> Layer;

struct Connection
{
    double weight;
    double deltaWeight;
};

class Neuron
{
public:
    //numOutputs, number of outputs/Neuron in the next layer
    Neuron(unsigned numOutputs, unsigned myIndex);
    void setOutputVal(double val)
    {
        m_outputVal = val;
    }
    double getOutputVal(void) const
    {
        return m_outputVal;
    }
    void feedForward(const Layer &prevLayer);
    void calcOutputGradients(double targetValue);
    void calcHiddenGradients(const Layer& nextLayer);
    void updateInputWeights(Layer& prevLayer);
private:
    static double randomWeights(void)
    {
        return rand() / double(RAND_MAX);
    }
    static double transferFunction(double sum);
    static double transferFunctionDerivative(double sum);
    double sumDOW(const Layer &nextLayer);
    double m_outputVal;
    unsigned m_myIndex;
    double m_gradient;
    static double alpha; //[0.0 .. 1.0] multiplier of last delta weight change (momentum)
    static double eta; //[0.0 ... 1.0] overall learning rate
    std::vector<Connection> m_outputWeights;
};


