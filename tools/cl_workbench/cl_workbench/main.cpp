#include "cl_workbench_main_window.h"
#include <QApplication>

int main(int argc, char *argv[])
{
    QApplication a(argc, argv);
    cl_workbench_main_window w;
    w.show();

    return a.exec();
}
