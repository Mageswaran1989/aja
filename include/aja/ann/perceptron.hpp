#ifndef _AJA_PERCEPTRON_H_
#define _AJA_PERCEPTRON_H_

#include <vector> //TODO: Platform independent

//TODO: Header

//TODO: Description
namespace aja
{

class perceptron
{
    public:
        perceptron(const& perceptron);

        // A constructor that takes a single argument is, by default, 
        // an implicit conversion operator, which converts its argument 
        // to an object of its class, the keyword "explicit" can be placed 
        // before the constructor with one parameter to avoid such implicit conversions.
        explicit perceptron(void);
        explicit perceptron(unsigned int num_inputs);
        
        ~perceptron();

         //Operator overloading 
         //TODO: C++ Explanation 
         perceptron operator = (const perceptron&);
         bool operator == (const perceptron&) const;

         enum 
         {
             THRESHOLD_ACTIVATION_FUNCTION,
             LINEAR_ACTIVATION_FUNCTION,
             SIGMOIDAL_ACTIVATION_FUNCTION,
             SIGN_ACTIVATION_FUNCTION,
             LOGRATHEMIC_ACTIVATION_FUNCTION,
             HYPERBOLIC_TANGENT_FUNCTION    
         } activation_functions;
         
         void set(void);
    protected:
    private:
        double bias;
        std::vector<double> synaptic_weights;   
        bool debug_msgs;
        activation_functions activation_function;
}

}

#endif
