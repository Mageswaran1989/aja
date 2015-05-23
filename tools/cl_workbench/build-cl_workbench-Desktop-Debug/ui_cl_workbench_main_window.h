/********************************************************************************
** Form generated from reading UI file 'cl_workbench_main_window.ui'
**
** Created by: Qt User Interface Compiler version 5.2.1
**
** WARNING! All changes made in this file will be lost when recompiling UI file!
********************************************************************************/

#ifndef UI_CL_WORKBENCH_MAIN_WINDOW_H
#define UI_CL_WORKBENCH_MAIN_WINDOW_H

#include <QtCore/QVariant>
#include <QtWidgets/QAction>
#include <QtWidgets/QApplication>
#include <QtWidgets/QButtonGroup>
#include <QtWidgets/QHeaderView>
#include <QtWidgets/QMainWindow>
#include <QtWidgets/QMenuBar>
#include <QtWidgets/QStatusBar>
#include <QtWidgets/QToolBar>
#include <QtWidgets/QWidget>

QT_BEGIN_NAMESPACE

class Ui_cl_workbench_main_window
{
public:
    QMenuBar *menuBar;
    QToolBar *mainToolBar;
    QWidget *centralWidget;
    QStatusBar *statusBar;

    void setupUi(QMainWindow *cl_workbench_main_window)
    {
        if (cl_workbench_main_window->objectName().isEmpty())
            cl_workbench_main_window->setObjectName(QStringLiteral("cl_workbench_main_window"));
        cl_workbench_main_window->resize(400, 300);
        menuBar = new QMenuBar(cl_workbench_main_window);
        menuBar->setObjectName(QStringLiteral("menuBar"));
        cl_workbench_main_window->setMenuBar(menuBar);
        mainToolBar = new QToolBar(cl_workbench_main_window);
        mainToolBar->setObjectName(QStringLiteral("mainToolBar"));
        cl_workbench_main_window->addToolBar(mainToolBar);
        centralWidget = new QWidget(cl_workbench_main_window);
        centralWidget->setObjectName(QStringLiteral("centralWidget"));
        cl_workbench_main_window->setCentralWidget(centralWidget);
        statusBar = new QStatusBar(cl_workbench_main_window);
        statusBar->setObjectName(QStringLiteral("statusBar"));
        cl_workbench_main_window->setStatusBar(statusBar);

        retranslateUi(cl_workbench_main_window);

        QMetaObject::connectSlotsByName(cl_workbench_main_window);
    } // setupUi

    void retranslateUi(QMainWindow *cl_workbench_main_window)
    {
        cl_workbench_main_window->setWindowTitle(QApplication::translate("cl_workbench_main_window", "cl_workbench_main_window", 0));
    } // retranslateUi

};

namespace Ui {
    class cl_workbench_main_window: public Ui_cl_workbench_main_window {};
} // namespace Ui

QT_END_NAMESPACE

#endif // UI_CL_WORKBENCH_MAIN_WINDOW_H
