// g++ -o tiny_xml tiny_xml.cpp -ltinyxml
//http://www.dinomage.com/2012/01/tutorial-using-tinyxml-part-1/
//http://www.w3.org/TR/2004/REC-xml-20040204/
//http://www.grinninglizard.com/tinyxmldocs/tutorial0.html
//https://alikhuram.wordpress.com/2013/06/17/parsing-a-simple-xml-document-using-c/
#include<iostream>
#include <stdexcept> 
#include <exception>
#include <cstdlib> 
#include <tinyxml.h>

class XML
{
  public:
    XML(std::string file_name);
    void read(std::string file_name);
    void write(std::string file_namei);    
    void dump_backpropagation_params();
    void display_in_stdout();
  protected:
  private:
//    std::string m_file_name;
    TiXmlDocument m_xml_handler;
};

XML::XML(std::string file_name) : 
         m_file_name(file_name), m_xml_handler(m_file_name)
{
   if (!m_xml_handler.LoadFile()) 
   {
      throw std::runtime_error("File open error");
   }
  
}

void XML::read(std::string file_name)
{
  
}

void XML::write(std::string file_name)
{
}

/*
<?xml version="1.0" ?>
<Hello>World</Hello>
*/
void XML::dump_backpropagation_params()
{
  TiXmlDeclaration * decl = new TiXmlDeclaration( "1.0", "", "" );
  TiXmlElement * element = new TiXmlElement( "Hello" );
  TiXmlText * text = new TiXmlText( "World" );
  element->LinkEndChild(text);
  m_xml_handler.LinkEndChild( decl );
  m_xml_handler.LinkEndChild( element );
  m_xml_handler.SaveFile(m_file_name );
}

int main(int argc, char* argv[])
{
  XML test("first.xml");
  test.dump_backpropagation_params();
}
