#include "Neuron.hpp"

double Neuron::eta = 0.15;
double Neuron::alpha = 0.5;

Neuron::Neuron(unsigned numOutputs, unsigned myIndex)
{
    for (unsigned n = 0; n < numOutputs; ++n)
    {
        m_outputWeights.push_back(Connection());
        m_outputWeights.back().weight = randomWeights();
    }
    m_myIndex = myIndex;
}

void Neuron::feedForward(const Layer &prevLayer)
{
    double sum = 0.0;

    //http://www.hostmath.com/
    //$$output = f(\sum_i{i_i * w_i})
    for (unsigned neuron = 0; neuron < prevLayer.size(); ++neuron)
    {
        sum += prevLayer[neuron].getOutputVal() *
              prevLayer[neuron].m_outputWeights[m_myIndex].weight;
    }

    m_outputVal = Neuron::transferFunction(sum);
}

double Neuron::transferFunction(double sum)
{
    //http://www.hostmath.com/
    //$$\tanh(x) = \frac{e^x - e^{-x}}{e^x + e^{-x}}
    //output range [-1 to +1]
    return tanh(sum);
}

double Neuron::transferFunctionDerivative(double sum)
{
    //http://www.hostmath.com/
    //$$\frac{d}{dx}\tanh(x) = 1 - tanh^2(x) or 1 - x * x;
    return (1.0 - sum * sum);
}

void Neuron::calcOutputGradients(double targetValue)
{
    double delta = targetValue - m_outputVal;
    m_gradient = delta * Neuron::transferFunctionDerivative(m_outputVal);
}

void Neuron::calcHiddenGradients(const Layer& nextLayer)
{
    double delta = sumDOW(nextLayer);
    m_gradient = delta * Neuron::transferFunctionDerivative(m_outputVal);
}

double Neuron::sumDOW(const Layer &nextLayer)
{
    double sum = 0.0;

    //sum our contribution of the errors at the nodes we feed
    for (unsigned neuron = 0; neuron < nextLayer.size() - 1; ++neuron)
    {
        //weigths goes from oue neuron to other neuron indexed by other neuron
        sum += m_outputWeights[neuron].weight * nextLayer[neuron].m_gradient;
    }
    return sum;
}

void Neuron::updateInputWeights(Layer& prevLayer)
{
    // The weights to be updated are in the Connection container
    // in the neurons in the preceding layer

    for (unsigned n = 0; n < prevLayer.size(); ++n) {
        Neuron &neuron = prevLayer[n];
        double oldDeltaWeight = neuron.m_outputWeights[m_myIndex].deltaWeight;

        double newDeltaWeight =
                // Individual input, magnified by the gradient and train rate:
                eta
                * neuron.getOutputVal()
                * m_gradient
                // Also add momentum = a fraction of the previous delta weight;
                + alpha
                * oldDeltaWeight;

        neuron.m_outputWeights[m_myIndex].deltaWeight = newDeltaWeight;
        neuron.m_outputWeights[m_myIndex].weight += newDeltaWeight;
    }
}
