#include <aja/ann/perceptron.hpp>

namespace aja
{

  perceptron::perceptron(const perceptron& other)
  {
  }

  perceptron::perceptron(void)
  {
  }

  perceptron::perceptron(unsigned int num_inputs)
  {
  }

  void perceptron::set(void)
  {
    activation_function = HYPERBOLIC_TANGENT_FUNCTION;
  }

}
