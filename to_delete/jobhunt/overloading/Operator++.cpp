#include<iostream>

class Point
{
public:
   // Declare prefix and postfix increment operators.
   Point operator++();       // Prefix increment operator.
   Point operator++(int);     // Postfix increment operator.

   // Declare prefix and postfix decrement operators.
   Point operator--();       // Prefix decrement operator.
   Point operator--(int);     // Postfix decrement operator.

   // Define default constructor.
   Point() { _x = _y = 0; }

   // Define accessor functions.
   int x() { return _x; }
   int y() { return _y; }
private:
   int _x, _y;
};

// Define prefix increment operator.
Point Point::operator++()
{
   _x++;
   _y++;
   return *this;
}

// Define postfix increment operator.
Point Point::operator++(int)
{
   _x++;
   _y++;
   return *this;
/*   Point temp = *this;
   ++*this;
   return temp;*/
}

// Define prefix decrement operator.
Point Point::operator--()
{
   _x--;
   _y--;
   return *this;
}

// Define postfix decrement operator.
Point Point::operator--(int)
{
   Point temp = *this;
   --*this;
   return temp;
}
int main()
{
   Point p;
   ++p;
   std::cout << p.x() <<"\n";
   p++;
   std::cout << p.x() <<"\n";
}
