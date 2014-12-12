// fuzzfier.h
// program to fuzzify data
class Category
{
public:
    Category(){};
    void setname(char *);
    char * getname();
    void setval(float&,float&,float&);
    float getlowval();
    float getmidval();
    float gethighval();
    float getshare(const float&);
    ~Category(){};
private:
    char name[30];
    float lowval,highval,midval;
};
int randnum(int);
