#include "icl_practice_window.h"
#include "ui_icl_practice_window.h"

iCLPracticeWindow::iCLPracticeWindow(QWidget *parent) :
    QMainWindow(parent),
    ui(new Ui::iCLPracticeWindow)
{
    ui->setupUi(this);
    _kernelName = " iVizon";

    _kernelCodeEdit = new QsciScintilla();

    ui->verticalLayout_codeEditor->addWidget(_kernelCodeEdit);

    ui->label_argId->setHidden(true);
    ui->label_storage->setHidden(true);
    ui->label_img_src->setHidden(true);
    ui->label_name->setHidden(true);
    ui->label_img_dst->setHidden(true);
    ui->label_io->setHidden(true);
    ui->label_io_data->setHidden(true);
    ui->label_type->setHidden(true);
    ui->label_argId->setHidden(true);
    ui->pushButton_compileKernel->setHidden(true);
    ui->pushButton_generateCode->setHidden(true);
}

iCLPracticeWindow::~iCLPracticeWindow()
{
    delete ui;
}

void iCLPracticeWindow::createActions()
{
    actionNew = new QAction(QIcon(":/images/new.png"), tr("&New"), this);
    actionNew->setShortcut(tr("Ctrl+N"));
    actionNew->setStatusTip(tr("Create a new file"));
    //connect(actionNew, SIGNAL(triggered()), this, SLOT(newFile()));
}

void iCLPracticeWindow::on_comboBox_8_currentIndexChanged(int index)
{
    _numArgs = index;
}

void iCLPracticeWindow::createArgumentList()
{
    for(int i=0; i<_numArgs; i++)
    {
        _argLabel[i] = new QLabel(QString::number(i));
        //_argLabel[i].setText(QString::number(i));
        ui->verticalLayout_ardID->addWidget(_argLabel[i]);

        _lineEditName[i] = new QLineEdit();
        ui->verticalLayout_name->addWidget(_lineEditName[i]);

        _comboBoxStorage[i] = new QComboBox;
        _comboBoxStorage[i]->addItem("__global");
        _comboBoxStorage[i]->addItem("__local");
        _comboBoxStorage[i]->addItem("__read_only");
        _comboBoxStorage[i]->addItem("__write_only");

        ui->verticalLayout_storage->addWidget(_comboBoxStorage[i]);
    }

}

void iCLPracticeWindow::createMenus()
{
    _menuFile = menuBar()->addMenu(tr("&File"));
    _menuFile->addAction(actionNew);
}

void iCLPracticeWindow::createToolBars()
{
    ui->mainToolBar = addToolBar(tr("&File"));
    ui->mainToolBar->addAction(actionNew);
}

void iCLPracticeWindow::on_pushButton_genKernelHeaders_clicked()
{
    ui->pushButton_compileKernel->setHidden(false);


    QString kernelSource("__kernel void");
    kernelSource = kernelSource + _kernelName + " (";
    _kernelCodeEdit->setText(kernelSource);
}

void iCLPracticeWindow::on_lineEdit_kernelName_textChanged(const QString &kernelName)
{
    _kernelName = kernelName;

}

void iCLPracticeWindow::on_pushButton_createArgList_clicked()
{
    ui->label_argId->setHidden(false);
    ui->label_storage->setHidden(false);
    ui->label_img_src->setHidden(false);
    ui->label_name->setHidden(false);
    ui->label_img_dst->setHidden(false);
    ui->label_io->setHidden(false);
    ui->label_io_data->setHidden(false);
    ui->label_type->setHidden(false);
    ui->label_argId->setHidden(false);
    createArgumentList();
}

void iCLPracticeWindow::on_pushButton_deleteArgList_clicked()
{
    for(int i=0; i<_numArgs; i++)
    {
        ui->verticalLayout_ardID->removeWidget(_argLabel[i]);

        ui->verticalLayout_name->removeWidget(_lineEditName[i]);

        ui->verticalLayout_storage->removeWidget(_comboBoxStorage[i]);
    }
    //  ui->verticalLayout_storage->
    //   delete _argLabel ;
    //   delete[] _lineEditName ;
    //   delete[]  _comboBoxStorage ;
}

void iCLPracticeWindow::on_pushButton_compileKernel_clicked()
{
    ui->pushButton_generateCode->setHidden(false);
}

void iCLPracticeWindow::on_pushButton_run_clicked()
{
    //fork();
    //execlp("ps", "ax", 0);
   // execlp("gcc", "gcc", "-o mageswaran_e mageswaran.c", 0);
   system("gcc -o openGLESSample mageswaran.c  -lX11 -lXi -lXmu -lglut -lGL -lGLU -lm ");
   system("./openGLESSample");

}
