package com.teach.javafx.controller.base;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;
import java.util.List;


//一个窗口管理器，单例
public class WindowsManager {
    private static WindowsManager instance;
    private ObservableList<Window> windows;

    private WindowsManager(){
        //获取所有窗口
        this.windows =  Stage.getWindows();
    }

    //初始化窗口管理器
    private static void initWindowsManager(){
        if(instance == null){
            instance = new WindowsManager();
        }
        //获取所有窗口
        instance.windows = Stage.getWindows();
        //添加监听器，为每个新增的窗口设置样式
        instance.windows.addListener(new ListChangeListener<Window>() {
            @Override
            public void onChanged(Change<? extends Window> change) {
                while(change.next()){
                    if(change.wasAdded()){
                        List<Window> addedWindows = (List<Window>) change.getAddedSubList();
                        for(Window w : addedWindows){
                            try{
                                Stage stage = (Stage) w;
                            }
                            catch (ClassCastException e){
                                //该异常并无大碍，直接忽略
                            }
                        }
                    }
                }
            }
        });
    }

    public static WindowsManager getInstance(){
        return instance;
    }

    public static void init(){
        if(instance != null){
            return;
        }
        initWindowsManager();
        System.out.println(instance.windows.size());
    }

    //获取所有窗口
    public List<Window> getWindows(){
        return windows.stream().toList();
    }

    //创建一个新窗口
    //参数含义:
    //loader: 窗口的FXML资源加载器
    //width, height: 窗口的宽高
    //title: 窗口标题
    //owner: 窗口的父窗口
    //modality: 窗口的交互模态
    //action: 在新窗口创建时需要进行的初始化操作
    public Stage openNewWindow(FXMLLoader loader, int width, int height, String title, Window owner, Modality modality, WindowOpenAction action) throws IOException {
        Scene scene = new Scene(loader.load(), width, height);
        Object controller = loader.getController();
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle(title == null ? "" : title);
        if(owner != null){
            stage.initOwner(owner);
        }
        if(modality != null){
            stage.initModality(modality);
        }
        if(action != null){
            action.init(controller);
            action.init(controller, stage);
        }
        stage.show();
        return stage;
    }
}
