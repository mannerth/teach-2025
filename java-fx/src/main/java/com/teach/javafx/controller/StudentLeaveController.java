package com.teach.javafx.controller;

import com.teach.javafx.controller.base.MessageDialog;
import com.teach.javafx.request.DataRequest;
import com.teach.javafx.request.DataResponse;
import com.teach.javafx.request.HttpRequestUtil;
import com.teach.javafx.request.OptionItem;
import com.teach.javafx.util.CommonMethod;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.MapValueFactory;

import java.util.ArrayList;
import java.util.HashMap;
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
    @FXML
    private TableColumn<Map, String> idColumn;
    @FXML
    private TextField numNameTextField;  //查询 姓名学号输入域

    @FXML
    private TextField nameField;  //学生信息  名称输入域
    @FXML
    private TextField classNameField; //学生信息  院系输入域
    @FXML
    private TextField reasonField; //学生信息  专业输入域
    @FXML
    private TextField startDateField; //学生信息  班级输入域
    @FXML
    private TextField endDateField; //学生信息  证件号码输入域


    @FXML
    private Integer leaveId = null;  //当前编辑修改的学生的主键

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
        idColumn.setCellValueFactory(new MapValueFactory<>("leaveId"));
        nameColumn.setCellValueFactory(new MapValueFactory<>("name"));//设置列值工程属性
        classNameColumn.setCellValueFactory(new MapValueFactory<>("className"));
        reasonColumn.setCellValueFactory(new MapValueFactory<>("reason"));
        startdateColumn.setCellValueFactory(new MapValueFactory<>("startDate"));
        enddateColumn.setCellValueFactory(new MapValueFactory<>("endDate"));
        setTableViewData();
    }

    public void clearPanel() {
        leaveId = null;
        nameField.setText("");
        classNameField.setText("");
        reasonField.setText("");
        startDateField.setText("");
        endDateField.setText("");

    }


    protected void changeStudentLeaveInfo() {
        Map<String,Object> form = dataTableView.getSelectionModel().getSelectedItem();
        if (form == null) {
            clearPanel();
            return;
        }
        leaveId = CommonMethod.getInteger(form, "leaveId");
        DataRequest req = new DataRequest();
        req.add("leaveId", leaveId);
        DataResponse res = HttpRequestUtil.request("/api/studentLeave/getStudentLeaveInfo", req);
        if (res.getCode() != 0) {
            MessageDialog.showDialog(res.getMsg());
            return;
        }
        form = (Map) res.getData();
        nameField.setText(CommonMethod.getString(form, "name"));
        classNameField.setText(CommonMethod.getString(form, "className"));
        reasonField.setText(CommonMethod.getString(form, "reason"));
        startDateField.setText(CommonMethod.getString(form, "startDate"));
        endDateField.setText(CommonMethod.getString(form, "endDate"));

    }




    @FXML
    protected void onQueryButtonClick() {
        String numName = numNameTextField.getText();
        DataRequest req = new DataRequest();
        req.add("numName", numName);
        DataResponse res = HttpRequestUtil.request("/api/studentLeave/getStudentLeaveList", req);
        if (res != null && res.getCode() == 0) {
            studentLeaveList = (ArrayList<Map>) res.getData();
            setTableViewData();
        }
    }

    public void onTableRowSelect(ListChangeListener.Change<? extends Integer> change) {
        changeStudentLeaveInfo();
    }

    @FXML
    protected void onAddButtonClick() {
        clearPanel();
    }

    @FXML
    protected void onDeleteButtonClick() {
        Map form = dataTableView.getSelectionModel().getSelectedItem();
        if (form == null) {
            MessageDialog.showDialog("没有选择，不能删除");
            return;
        }
        int ret = MessageDialog.choiceDialog("确认要删除吗?");
        if (ret != MessageDialog.CHOICE_YES) {
            return;
        }
        leaveId = CommonMethod.getInteger(form, "leaveId");
        System.out.println(leaveId);
        DataRequest req = new DataRequest();
        req.add("leaveId", leaveId);
        DataResponse res = HttpRequestUtil.request("/api/studentLeave/studentLeaveDelete", req);
        if (res != null) {
            if (res.getCode() == 0) {
                MessageDialog.showDialog("删除成功！");
                onQueryButtonClick();
            } else {
                MessageDialog.showDialog(res.getMsg());
            }
        }
    }

    @FXML
    protected void onSaveButtonClick() {
        if (nameField.getText().isEmpty()) {
            MessageDialog.showDialog("姓名为空，不能修改");
            return;
        }
        Map<String,Object> form = new HashMap<>();
        form.put("name", nameField.getText());
        form.put("className", classNameField.getText());
        form.put("reason", reasonField.getText());
        form.put("startDate", startDateField.getText());
        form.put("endDate", endDateField.getText());
        DataRequest req = new DataRequest();
        req.add("leaveId", leaveId);
        req.add("form", form);
        DataResponse res = HttpRequestUtil.request("/api/studentLeave/studentLeaveEditSave", req);
        if (res != null && res.getCode() == 0) {
            leaveId = CommonMethod.getIntegerFromObject(res.getData());
            MessageDialog.showDialog("提交成功！");
            onQueryButtonClick();
        }
    }
}
