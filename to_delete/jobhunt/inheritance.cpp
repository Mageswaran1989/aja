#include <iostream>

using namespace std;

class Base
{
    public:
        Base() { std::cout << "Base Constructor\n"; }
        void function1();
        virtual void function2();
};

class Derived : public Base
{
    public:
        Derived() { std::cout << "Derived Constructor\n"; }
        void function1();
        void function2();
};

void Base::function1()
{
    std::cout << "I am in base_function1 \n";
}

void Base::function2()
{
    std::cout << "I am in base_function2 (virtual) \n";
}

void Derived::function1()
{
    std::cout << "I am in derived_function1 \n";
}

void Derived::function2()
{
    std::cout << "I am in derived_function2 (virtual) \n";
}

int main()
{
    //Stage 1 : Normal
    std::cout << "*****Normal*****\n";
    Base baseObject;
    Derived derivedObject;
    baseObject.function1();
    baseObject.function2();
    derivedObject.function1();
    derivedObject.function2();

    //Stage 2 : Base pointer with Base class
    std::cout << "*****Base pointer with Base class*****\n";
    Base *basePtr1 = new Base;
    basePtr1->function1();
    basePtr1->function2();

    //Satge 3 : Base pointer with Derived class
    std::cout << "*****Base pointer with Derived class*****\n";
    Base *basePtr2 = new Derived;
    basePtr2->function1();
    basePtr2->function2();

    //Stage 4 : Derived pointer with Derived class
    std::cout << "*****Derived pointer with Derived class*****\n";
    Derived *derivedClassPtr1 = new Derived;
    derivedClassPtr1->function1();
    derivedClassPtr1->function2();

    //Stage 5 : Derived pointer with Derived class
//    std::cout << "*****Derived pointer with Base class*****\n";
//    Derived *derivedClassPtr2 = new Base;
//    derivedClassPtr2->function1();
//    derivedClasPtr2s->function2();

    return 0;
}

