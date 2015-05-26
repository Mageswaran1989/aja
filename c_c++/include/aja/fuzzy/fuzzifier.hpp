// fuzzfier.h
// program to fuzzify data
class category
{
public:
    category(){};
    void setname(char *);
    char * getname();
    void setval(float&,float&,float&);
    float getlowval();
    float getmidval();
    float gethighval();
    float getshare(const float&);
    ~category(){};
private:
    char name[30];
    float lowval,highval,midval;
};
int randomnum(int maxval);
