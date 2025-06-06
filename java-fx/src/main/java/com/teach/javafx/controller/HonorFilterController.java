package com.teach.javafx.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HonorFilterController {
    @FXML
    private GridPane gridPane;

    private List<Map> honors = new ArrayList<>();
    private String honorType;

    @FXML
    public void initialize(){

    }

    public void setDisplay(){
        int row = 0;

        gridPane.add(new Label(honorType),0,row);
        for(Map m : honors){
            gridPane.add(new Label((String) m.get("honorContent")),1,row);
            row++;
        }
        if(honors.isEmpty()){
            gridPane.add(new Label("暂无奖项"), 1, row);
            row++;
        }
    }

    public void setData(List<Map> data, String honorType){
        this.honors = data;
        this.honorType = honorType;
    }
}
