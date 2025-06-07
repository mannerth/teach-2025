package com.teach.javafx.controller;

import com.teach.javafx.MainApplication;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class HonorMenuController {
    @FXML
    private Button openHonorWindow;

    private Stage stage = null;

    @FXML
    public void initialize(){

    }

    public void onOpenWindow(){
        FXMLLoader loader = new FXMLLoader(MainApplication.class.getResource("honor-check-panel.fxml"));
        try{
            Scene scene = new Scene(loader.load(), 700, 400);
            stage = new Stage();
            stage.setTitle("我的荣誉");
            stage.setScene(scene);
            stage.setResizable(false);
            stage.initOwner(MainApplication.getMainStage());
            stage.initModality(Modality.WINDOW_MODAL);
            stage.setOnCloseRequest(windowEvent -> stage = null);
            stage.show();
        }
        catch (IOException o){
            System.out.println("加载荣誉页面失败: " + o.getMessage());
        }
    }
}
