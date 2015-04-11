#include <aja/ann/perceptron.hpp>

namespace aja
{

  Perceptron::Perceptron(const Perceptron& other)
  {
  }

  Perceptron::Perceptron(void)
  {
  }

  Perceptron::Perceptron(unsigned int num_inputs)
  {
  }

  void Perceptron::set(void)
  {
    activation_function = HYPERBOLIC_TANGENT_FUNCTION;
  }

}
