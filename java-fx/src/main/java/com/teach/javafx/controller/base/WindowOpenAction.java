package com.teach.javafx.controller.base;

import javafx.stage.Stage;

//该接口用于在新建窗口时对新窗口进行一些初始化操作
public interface WindowOpenAction {
    default void init(Object controller){}
    default void init(Object controller, Stage stage){}
}
