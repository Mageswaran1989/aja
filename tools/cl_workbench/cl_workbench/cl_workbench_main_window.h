#ifndef CL_WORKBENCH_MAIN_WINDOW_H
#define CL_WORKBENCH_MAIN_WINDOW_H

#include <QMainWindow>

namespace Ui {
class cl_workbench_main_window;
}

class cl_workbench_main_window : public QMainWindow
{
    Q_OBJECT

public:
    explicit cl_workbench_main_window(QWidget *parent = 0);
    ~cl_workbench_main_window();

private:
    Ui::cl_workbench_main_window *ui;
};

#endif // CL_WORKBENCH_MAIN_WINDOW_H
