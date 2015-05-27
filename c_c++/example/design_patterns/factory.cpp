#include <iostream>
#include <map>

//Interface/base class that are shared between implementations
class i_animal
{
    public:
        virtual int get_number_of_legs() const = 0;
        virtual void speak() = 0;
        virtual void free() = 0;
};

typedef i_animal* (*create_animal_fn)(void);

// i_animal implementations
class cat : public i_animal
{
    public:
        int get_number_of_legs() const
        { return 4; }
        void speak() { std::cout << "Meow" << std::endl; }
        void free() { delete this; }

        static i_animal* create() { return new cat(); }
};

class dog : public i_animal
{
    public:
        int get_number_of_legs() const { return 4; }
        void speak() { std::cout << "Woof" << std::endl; }
        void free() { delete this; }

        static i_animal* create() { return new dog(); }
};

class spider : public i_animal // Yeah it isn’t really an animal…
{
    public:
        int get_number_of_legs() const { return 8; }
        void speak() { std::cout << std::endl; }
        void free() { delete this; }

        static i_animal* create() { return new spider(); }
};

class horse : public i_animal
{
    public:
        int get_number_of_legs() const { return 4; }
        void speak() { std::cout << "A horse is a horse, of course, of course." << std::endl; }
        void free() { delete this; }

        static i_animal* create() { return new horse(); }
};
// Factory for creating instances of i_animal
class animal_factory
{
    private:
        animal_factory();
        animal_factory(const animal_factory &) { }
        animal_factory &operator=(const animal_factory &) { return *this; }

        typedef std::map<std::string, create_animal_fn> factory_map;
        factory_map m_factory_map;
    public:
        ~animal_factory() { m_factory_map.clear(); }

        static animal_factory *get()
        {
            static animal_factory instance;
            return &instance;
        }

        void register_animals(const std::string &animal_name, create_animal_fn pfncreate);
        i_animal *createAnimal(const std::string &animal_name);
};
/* Animal factory constructor.
register_animals the types of animals here.
*/
animal_factory::animal_factory()
{
    register_animals("horse", &horse::create);
    register_animals("cat", &cat::create);
    register_animals("dog", &dog::create);
    register_animals("spider", &spider::create);
}

void animal_factory::register_animals(const std::string &animal_name, create_animal_fn pfncreate)
{
    m_factory_map[animal_name] = pfncreate;
}

i_animal *animal_factory::createAnimal(const std::string &animal_name)
{
    factory_map::iterator it = m_factory_map.find(animal_name);
    if( it != m_factory_map.end() )
        return it->second();
    return NULL;
}

int main( int argc, char **argv )
{
    i_animal *p_animal = NULL;
    std::string animal_name;

    while( p_animal == NULL )
    {
        std::cout << "Type the name of an animal or ‘q’ to quit: ";
        std::cin >> animal_name;

        if( animal_name == "q" )
            break;

        i_animal *p_animal = animal_factory::get()->createAnimal(animal_name);
        if( p_animal )
        {
            std::cout << "Your animal has " << p_animal->get_number_of_legs() << " legs." << std::endl;
            std::cout << "Your animal says: ";
            p_animal->speak();
        }
        else
        {
            std::cout << "That animal doesn’t exist in the farm! Choose another!" << std::endl;
        }
        if( p_animal )
            p_animal->free();
        p_animal = NULL;
        animal_name.clear();
    }
    return 0;
}


