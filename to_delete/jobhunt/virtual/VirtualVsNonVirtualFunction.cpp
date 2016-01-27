#include<iostream>

class Base 
{
   public:
          virtual void virtualFunc() { std::cout<<"I am in Base VF\n"; }
          void baseFunction() {  std::cout<<"I am in Base NF\n";}
};

class Derived: public Base 
{
   public:
          void virtualFunc() { std::cout<<"I am in Derived VF\n"; }
          void baseFunction() {  std::cout<<"I am in Derived NF\n";}

};

int main() 
{
   Base *basePtr = new Derived();
   basePtr->virtualFunc();
   basePtr->baseFunction();
}
