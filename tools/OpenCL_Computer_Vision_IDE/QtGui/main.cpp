#include "icl_practice_window.h"
#include <QApplication>

int main(int argc, char *argv[])
{
    QApplication a(argc, argv);
    iCLPracticeWindow w;
    w.createActions();
    w.createMenus();
    w.createToolBars();
    w.show();

    return a.exec();
}
