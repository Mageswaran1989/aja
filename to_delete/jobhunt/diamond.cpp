#include <iostream>

using namespace std;

class Base
{
    public:
         Base() { std::cout << "Base Constructor\n"; }
        void function1() { std::cout << "Base function1 \n"; }
        virtual void function2() { std::cout << "Base function2 (virtual)\n"; }
};

class LeftDerived : virtual public Base
{
    public:
        LeftDerived() { std::cout << "LeftDerived Constructor\n"; }
        void function1() { std::cout << "LeftDerived function1 \n"; }
        void function2() { std::cout << "LeftDerived function2 (virtual)\n"; }

};

class RightDerived : virtual public Base
{
    public:
        RightDerived() { std::cout << "RightDerived Constructor\n"; }
        void function1() { std::cout << "RightDerived function1 \n"; }
        void function2() { std::cout << "RightDerived function2 (virtual)\n"; }
};

class DerivedSquare : public LeftDerived, RightDerived
{
    public:
        DerivedSquare() { std::cout << "DerivedSquare Constructor\n"; }
        void function1() { std::cout << "DerivedSquare function1 \n"; }
        void function2() { std::cout << "DerivedSquare function2 (virtual)\n"; }
};


int main()
{
    cout << "*****Base class object***** \n";
    Base base;
    base.function1();
    base.function2();

    cout << "*****DerivedSquare class object***** \n";
    DerivedSquare derivedSquare;
    derivedSquare.function1();
    derivedSquare.function2();

}
