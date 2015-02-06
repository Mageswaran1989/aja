#include<aja/ann/perceptron.cpp>

namespace aja
{

perceptron::perceptron(const perceptron& other)
{
}

explicit perceptron::perceptron(void)
{
}

explicit perceptron::perceptron(unsigned int num_inputs)
{
}

void perceptron::set(void)
{
    activation_function = HYPERBOLIC_TANGENT_FUNCTION;
}
