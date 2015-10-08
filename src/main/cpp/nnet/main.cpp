#include <iostream>
#include <vector>
#include <cstdlib>

class Neuron;
struct Connection
{
    double weigth;
    double deltaWeigth;
};

typedef std::vector<Neuron> Layer;

class Neuron
{
public:
    //numOutputs, number of Neuron in the next layer
    Neuron(unsigned numOutputs);
private:
    double m_outputVal;
    std::vector<Connection> m_outputWeights;
    static double randomWeights(void) { return rand() / double(RAND_MAX); }
};


Neuron::Neuron(unsigned numOutputs)
{
    for (unsigned n = 0; n < numOutputs; ++n)
    {
        m_outputWeights.push_back(Connection());
        m_outputWeights.back().weigth = randomWeights();
    }
}

class Nnet
{
public:
    Nnet(const std::vector<unsigned> &topology);
    void feedForward(const std::vector<double> &inputVals) {}
    void backProp(const std::vector<double> &targetVals) {}
    void getResults(std::vector<double> &resultVals) const {}
private:
    std::vector<Layer> m_layers; //m_layers[lauerNum][neuronNum]
};

Nnet::Nnet(const std::vector<unsigned> &topology)
{
    unsigned numLayers = topology.size();
    for (unsigned layerNum = 0;  layerNum < numLayers; ++layerNum)
    {
        m_layers.push_back(Layer());
        //If the layer is last one no outputs, otherwise take next layer outputs
        unsigned numOutputs = layerNum == topology.size()-1 ? 0 : topology[layerNum+1];

        //We have made a new Layer, now fill it with neurons, and
        //add a bias neuron to the layer (<'=')
        for (unsigned neuronNum = 0; neuronNum <= topology[neuronNum]; ++neuronNum)
        {
            m_layers.back().push_back(Neuron(numOutputs));
            std::cout<< "Made a neuron!\n";
        }
    }
}

int main()
{
    std::cout << "Neural Network tutorial" << std::endl;

    //eg: 3,2,1
    std::vector<unsigned> topology;
    topology.push_back(3);
    topology.push_back(2);
    topology.push_back(1);
    Nnet myNnet(topology);

    std::vector<double> inputVals;
    std::vector<double> targetValues;
    std::vector<double> resultValues;

    myNnet.feedForward(inputVals);
    myNnet.backProp(targetValues);
    myNnet.getResults(resultValues);

    return 0;
}

