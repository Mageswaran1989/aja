#include <iostream>

class Singleton
{
  public:
    static Singleton* getInstance()
    {
      if (mInstance == NULL)
      {
        //Imagine the class needs to be initialized
        //with some default values
        mInstance = new Singleton(5,10);
      }
      return mInstance;
    }
    void printDimension() const
    {
      std::cout << "Width  : " << mWidth
                << " Height : " << mHeight
                << "\n";
    }
    
  private:
    Singleton(int width, int height)
    {
      mWidth = width;
      mHeight = height;
    }
    /// Eventhough the class is under construction
    /// pointer to the class can be created, by default
    /// it takes size of (native Proceassor address lines / 8)
    static Singleton* mInstance;
    int mWidth, mHeight;
};
//Intialize the static variable
//before the class object is created
Singleton* Singleton::mInstance = NULL;

int main(void) /// void is used to say no arguments
{
  //    Singleton obj1; //error: ‘Singleton::Singleton()’ is private
  Singleton* obj2 = Singleton::getInstance();
  obj2->printDimension();
}
