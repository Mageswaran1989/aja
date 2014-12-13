#include <aja/ann/neuron.hpp>

using namespace std;

neuron::neuron()
{

}

neuron::neuron(int *j)
{
    int i;
    for(i = 0; i < 4; i++)
    {
        weightv[i]= *(j + i);
    }
}
int neuron::act(int m, int *x)
{
    int i;
    int a=0;
    for(i = 0; i < m; i++)
    {
        a += x[i] * weightv[i];
    }
    return a;
}
