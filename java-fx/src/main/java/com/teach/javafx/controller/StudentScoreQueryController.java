package com.teach.javafx.controller;

import com.teach.javafx.AppStore;
import com.teach.javafx.request.DataRequest;
import com.teach.javafx.request.DataResponse;
import com.teach.javafx.request.HttpRequestUtil;
import com.teach.javafx.util.CommonMethod;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.MapValueFactory;

import javax.swing.text.TabableView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StudentScoreQueryController {
    @FXML
    private TableView<Map> dataTableView;
    @FXML
    private TableColumn<Map, String> courseNumColumn;
    @FXML
    private TableColumn<Map, String> courseNameColumn;
    @FXML
    private TableColumn<Map, String> creditColumn;
    @FXML
    private TableColumn<Map, String> markColumn;
    @FXML
    private TableColumn<Map, String> gpaColumn;

    private List<HashMap<String, Object>> scoreList;
    private ObservableList<Map> observableList = FXCollections.observableArrayList();

    @FXML
    public void initialize(){
        DataRequest dataRequest = new DataRequest();
        HashMap map = new HashMap<String, Integer>();
        map.put("personId", AppStore.getJwt().getId());
        dataRequest.setData(map);
        DataResponse res = HttpRequestUtil.request("/api/score/getScoreList",dataRequest);
        System.out.println(res.getData());
        if(res.getCode()!=200){

        }
        scoreList = (ArrayList)(res.getData());
        courseNumColumn.setCellValueFactory(new MapValueFactory<>("courseNum"));
        courseNameColumn.setCellValueFactory(new MapValueFactory<>("courseName"));
        creditColumn.setCellValueFactory(new MapValueFactory<>("credit"));
        markColumn.setCellValueFactory(new MapValueFactory<>("mark"));
        gpaColumn.setCellValueFactory(new MapValueFactory<>("gpa"));
        setTableViewData();
    }

    private void setTableViewData(){
        observableList.clear();
        for (Map<String, Object> score : scoreList) {
            observableList.add(score);
        }
        dataTableView.setItems(observableList);
    }
}
