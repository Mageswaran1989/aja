#include <iostream>
#include <thread>
//-std=c++0x -pthread

void foo();

std::thread first (foo);
std::thread second (foo);

void foo()
{
   static int i = 0;
   for(int j=0; j<10; j++)
   {
     i = i +1;
     std::cout << i << "\n";
   }
}

int main()
{
  std::cout << "first and second are running cocurrently\n";

  first.join();
  second.join();
  
  std::cout << "thread completed;";
  
  return 0;
} 
