#-------------------------------------------------
#
# Project created by QtCreator 2014-04-26T19:00:18
#
#-------------------------------------------------

QT       += core gui

greaterThan(QT_MAJOR_VERSION, 4): QT += widgets

TARGET = iCLPracticeBed
TEMPLATE = app
CONFIG      += qscintilla2

INCLUDEPATH += /usr/include/qt4/
LIBS +=  -lqscintilla2
SOURCES += main.cpp\
        icl_practice_window.cpp

HEADERS  += icl_practice_window.h

FORMS    += icl_practice_window.ui

RESOURCES += icl_practise_window.qrc
