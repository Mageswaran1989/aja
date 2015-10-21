#include <vector>
#include "Neuron.hpp"

class Nnet
{
public:
    Nnet(const std::vector<unsigned> &topology);
    void feedForward(const std::vector<double> &inputVals);
    void backProp(const std::vector<double> &targetVals);
    void getResults(std::vector<double> &resultVals) const;
    double getRecentAverageError(void) const
    {
        return m_recentAverageError;
    }
private:
    std::vector<Layer> m_layers; //m_layers[lauerNum][neuronNum]
    double m_error;
    double m_recentAverageError;
    static double m_recentAverageSmoothingFactor;
};
