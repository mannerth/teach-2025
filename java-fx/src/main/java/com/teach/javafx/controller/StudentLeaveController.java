package com.teach.javafx.controller;

import com.teach.javafx.request.DataRequest;
import com.teach.javafx.request.DataResponse;
import com.teach.javafx.request.HttpRequestUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.MapValueFactory;

import java.util.ArrayList;
import java.util.Map;

public class StudentLeaveController {
    @FXML
    private TableView<Map> dataTableView;  //学生信息表
    @FXML
    private TableColumn<Map, String> nameColumn; //学生信息表 名称列
    @FXML
    private TableColumn<Map, String> classNameColumn; //学生信息表 班级列
    @FXML
    private TableColumn<Map, String> reasonColumn; //学生信息表 证件号码列
    @FXML
    private TableColumn<Map, String> startdateColumn; //学生信息表 性别列
    @FXML
    private TableColumn<Map, String> enddateColumn; //学生信息表 出生日期列

    private ArrayList<Map> studentLeaveList = new ArrayList();  // 学生信息列表数据
    private ObservableList<Map> observableList = FXCollections.observableArrayList();  // TableView渲染列表


    /**
     * 将学生数据集合设置到面板上显示
     */
    private void setTableViewData() {
        observableList.clear();
        for (int j = 0; j < studentLeaveList.size(); j++) {
            observableList.addAll(FXCollections.observableArrayList(studentLeaveList.get(j)));
        }
        dataTableView.setItems(observableList);
    }

    /**
     * 页面加载对象创建完成初始化方法，页面中控件属性的设置，初始数据显示等初始操作都在这里完成，其他代码都事件处理方法里
     */

    @FXML
    public void initialize() {
        DataResponse res;
        DataRequest req = new DataRequest();
        req.add("numName", "");
        res = HttpRequestUtil.request("/api/studentLeave/getStudentLeaveList", req); //从后台获取所有学生信息列表集合
        if (res != null && res.getCode() == 0) {
            studentLeaveList = (ArrayList<Map>) res.getData();
        }
        System.out.println(res.getData());
        nameColumn.setCellValueFactory(new MapValueFactory<>("name"));//设置列值工程属性
        classNameColumn.setCellValueFactory(new MapValueFactory<>("className"));
        reasonColumn.setCellValueFactory(new MapValueFactory<>("reason"));
        startdateColumn.setCellValueFactory(new MapValueFactory<>("startDate"));
        enddateColumn.setCellValueFactory(new MapValueFactory<>("endDate"));
        setTableViewData();
    }
}
