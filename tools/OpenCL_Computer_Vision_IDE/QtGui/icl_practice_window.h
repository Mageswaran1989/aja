#ifndef ICLPRACTICEWINDOW_H
#define ICLPRACTICEWINDOW_H

#include <QMainWindow>
#include <QAction>
#include <QMenu>
#include <QPushButton>
#include <QLabel>
#include <QLineEdit>
#include <QComboBox>

#include <Qsci/qsciscintilla.h>

namespace Ui {
class iCLPracticeWindow;
}

class iCLPracticeWindow : public QMainWindow
{
    Q_OBJECT

public:
    explicit iCLPracticeWindow(QWidget *parent = 0);
    ~iCLPracticeWindow();
    void createActions();
    void createMenus();
    void createToolBars();

private slots:


    void on_comboBox_8_currentIndexChanged(int index);

    void on_pushButton_genKernelHeaders_clicked();

    void on_lineEdit_kernelName_textChanged(const QString &kernelName);

    void on_pushButton_createArgList_clicked();

    void on_pushButton_deleteArgList_clicked();

    void on_pushButton_compileKernel_clicked();

    void on_pushButton_run_clicked();

private:
    Ui::iCLPracticeWindow *ui;

    //=================Menu Items
    QMenu* _menuFile;

    //=================ToolBar Items
    QToolBar* _fileToolBar;

    //=================Actions
    QAction* actionNew;

    QPushButton *mageswaran;
    QLabel*  _argLabel[20];
    int _numArgs;
    QLineEdit*  _lineEditName[20];
    QComboBox*  _comboBoxStorage[20];

    //==================String Section
    QString _kernelName;

    void createArgumentList();

    //==================>Code Editor
    QsciScintilla* _kernelCodeEdit;
    QString _curFile;

};

#endif // ICLPRACTICEWINDOW_H
