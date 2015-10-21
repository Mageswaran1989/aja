#include <string>
#include <fstream>
#include <sstream>
#include <vector>
#include <string>

// Silly class to read training data from a text file -- Replace This.
// Replace class TrainingData with whatever you need to get input data into the
// program, e.g., connect to a database, or take a stream of data from stdin, or
// from a file specified by a command line argument, etc.

class TrainingData
{
public:
    TrainingData(const std::string filename);
    bool isEof(void)
    {
        return m_trainingDataFile.eof();
    }
    void getTopology(std::vector<unsigned> &topology);

    // Returns the number of input values read from the file:
    unsigned getNextInputs(std::vector<double> &inputVals);
    unsigned getTargetOutputs(std::vector<double> &targetOutputVals);
    void showVectorVals(std::string name, std::vector<double>& vec);

private:
    std::ifstream m_trainingDataFile;
};


//#include <iostream>
//#include <cmath>
//#include <cstdlib>

//using namespace std;

//int main()
//{
//    //Random traiing sets for XOR - two inputs one output

//    cout << "topology: 2 4 1" << endl;

//    for (int i = 2000; i >= 0; --i)
//    {
//        int n1 = (int) (2.0 * rand() / double(RAND_MAX));
//        int n2 = (int) (2.0 * rand() / double(RAND_MAX));

//        int t = n1 ^ n2; //should be one or zero

//        cout << "in: " << n1 << ".0 " << n2 << ".0" << endl;
//        cout << "out: " << t << ".0" << endl;
//    }
//}
