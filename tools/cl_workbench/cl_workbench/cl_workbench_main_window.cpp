#include "cl_workbench_main_window.h"
#include "ui_cl_workbench_main_window.h"

cl_workbench_main_window::cl_workbench_main_window(QWidget *parent) :
    QMainWindow(parent),
    ui(new Ui::cl_workbench_main_window)
{
    ui->setupUi(this);
}

cl_workbench_main_window::~cl_workbench_main_window()
{
    delete ui;
}
