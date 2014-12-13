#include <aja/ann/network.hpp>

using namespace std;

int network::threshld(int k)
{
    if(k >= 0)
    {
        return (1);
    }
    else
    {
        return (0);
    }
}
network::network(int a[4],int b[4],int c[4],int d[4])
{
    nrn[0] = neuron(a) ;
    nrn[1] = neuron(b) ;
    nrn[2] = neuron(c) ;
    nrn[3] = neuron(d) ;
}
void network::activation(int *patrn)
{
    int i,j;
    for(i = 0; i < 4; i++)
    {
        for(j = 0; j < 4; j++)
        {
            cout << "neuron["<<i<<"].weight_vec["<<j<<"] is "
                 << nrn[i].weightv[j]
                 << "\n";
        }
        nrn[i].activation = nrn[i].act(4,patrn);
        cout << "activation is " << nrn[i].activation
             << "\n";
        output[i]=threshld(nrn[i].activation);
        cout << "output value is " << output[i]
             << "\n";
    }
}
