//Forward declaration
class network;

class layer
{
protected:
    int num_inputs;
    int num_outputs;
    float  *outputs;// pointer to array of outputs
    float  *inputs; // pointer to array of inputs, which
    // are outputs of some other layer
    friend network;
public:
    virtual void calc_out() = 0;
};
