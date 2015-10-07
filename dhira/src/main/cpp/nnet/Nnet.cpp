#include "Nnet.hpp"

double Nnet::m_recentAverageSmoothingFactor = 100.0; // Number of training samples to average over

Nnet::Nnet(const std::vector<unsigned> &topology)
{
    unsigned numLayers = topology.size();
    for (unsigned layerNum = 0;  layerNum < numLayers; ++layerNum)
    {
        m_layers.push_back(Layer());
        //If the layer is last one no outputs, otherwise take next layer outputs
        unsigned numOutputs = layerNum == topology.size() - 1 ? 0 : topology[layerNum+1];

        //We have made a new Layer, now fill it with neurons, and
        //add a bias neuron to the layer (<'=')
        for (unsigned neuronNum = 0; neuronNum <= topology[layerNum]; ++neuronNum)
        {
            m_layers.back().push_back(Neuron(numOutputs, neuronNum));
            std::cout<< "Made a neuron!\n";
        }
    }

    //Force the bias node's output value to 1.0. It is the last neuron created
    m_layers.back().back().setOutputVal(1.0);
}

void Nnet::feedForward(const std::vector<double> &inputVals)
{
    assert(inputVals.size() == m_layers[0].size() - 1);

    //Assign(latch) input values to the input layer
    for (unsigned i = 0; i < m_layers[0].size(); ++i)
    {
        m_layers[0][i].setOutputVal(inputVals[i]);
    }

    //Forward propagate in other layers
    for (unsigned layerNum = 1; layerNum < m_layers.size(); ++layerNum)
    {
        Layer& prevLayer = m_layers[layerNum - 1];
        for (unsigned neur = 0; neur < m_layers[layerNum].size() - 1; ++neur)
        {
            m_layers[layerNum][neur].feedForward(prevLayer);
        }
    }
}

void Nnet::backProp(const std::vector<double> &targetVals)
{
    //Calculate overall net error RMS of output neuron error
    //http://www.hostmath.com/
    //\sqrt{\frac{1}{N}\sum_i^n{(target_i - actual_i})^2}
    Layer&  outputLayer = m_layers.back();
    m_error = 0.0;

    for(unsigned neur = 0; neur < outputLayer.size() - 1; ++neur)
    {
        double delta = targetVals[neur] - outputLayer[neur].getOutputVal();
        m_error += delta * delta;
    }
    m_error /= outputLayer.size() - 1; //get average size of squared error
    m_error = sqrt(m_error);

    //Implement a recent average error measurement:
    m_recentAverageError = (m_recentAverageError * m_recentAverageSmoothingFactor + m_error)
                           / (m_recentAverageSmoothingFactor + 1);

    //Calculate output gradient
    for (unsigned neuron = 0; neuron < outputLayer.size() - 1; ++neuron)
    {
        outputLayer[neuron].calcOutputGradients(targetVals[neuron]);
    }

    //Calculate gradient for hidden layers
    for (unsigned layerNum = m_layers.size() - 2; layerNum > 0; --layerNum)
    {
        Layer& hiddenLayer = m_layers[layerNum];
        Layer& nextLayer = m_layers[layerNum + 1];

        for (unsigned neuron = 0; neuron < hiddenLayer.size(); ++neuron)
        {
            hiddenLayer[neuron].calcHiddenGradients(nextLayer);
        }

    }

    //For all layers from output to first hidden layer,
    //update connection weights
    for (unsigned layerNum = m_layers.size() - 1; layerNum > 0; --layerNum)
    {
        Layer& layer = m_layers[layerNum];
        Layer& prevLayer = m_layers[layerNum - 1];

        for (unsigned neuron = 0; neuron < layer.size() - 1; ++neuron)
        {
            layer[neuron].updateInputWeights(prevLayer);
        }

    }
}

void Nnet::getResults(std::vector<double> &resultVals) const
{
    resultVals.clear();

    for (unsigned neuron = 0; neuron < m_layers.back().size() - 1; ++neuron)
    {
        resultVals.push_back(m_layers.back()[neuron].getOutputVal());
    }
}
