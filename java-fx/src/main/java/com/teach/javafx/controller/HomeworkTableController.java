package com.teach.javafx.controller;

import com.teach.javafx.MainApplication;
import com.teach.javafx.controller.base.MessageDialog;
import com.teach.javafx.request.HttpRequestUtil;
import com.teach.javafx.request.OptionItem;
import com.teach.javafx.request.DataRequest;
import com.teach.javafx.request.DataResponse;
import com.teach.javafx.util.CommonMethod;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.MapValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HomeworkTableController {
    @FXML
    private TableView<Map> dataTableView;
    @FXML
    private TableColumn<Map,String> homeworkIdColumn;
    @FXML
    private TableColumn<Map, String> courseNameColumn;
    @FXML
    private TableColumn<Map, String> courseNumColumn;
    @FXML
    private TableColumn<Map, String> homeworkReleasingTimeColumn;
    @FXML
    private TableColumn<Map, String> homeworkDeadlineColumn;

    @FXML
    private TableColumn<Map,String> contentColumn;
    @FXML
    private TableColumn<Map, Button> editColumn;


    private ArrayList<Map> homeworkList = new ArrayList();
    private ObservableList<Map> observableList= FXCollections.observableArrayList();

    @FXML
    private ComboBox<OptionItem> courseComboBox;

    private List<OptionItem> courseList;

    private HomeworkEditController homeworkEditController = null;
    private Stage stage = null;

    public List<OptionItem> getCourseList() {
        return courseList;
    }

    @FXML
    private void onQueryButtonClick(){
        Integer courseId = 0;
        OptionItem op;
        op = courseComboBox.getSelectionModel().getSelectedItem();
        if(op != null)
            courseId = Integer.parseInt(op.getValue());
        DataResponse res;
        DataRequest req = new DataRequest();
        req.add("courseId",courseId);
        res = HttpRequestUtil.request("/api/homework/getHomeworkList",req); //从后台获取所有作业列表集合
        if(res != null && res.getCode()== 0) {
            homeworkList = (ArrayList<Map>)res.getData();
        }
        setTableViewData();
    }

    private void setTableViewData() {
        observableList.clear();
        Map map;
        Button editButton;
        for (int j = 0; j < homeworkList.size(); j++) {
            map = homeworkList.get(j);
            editButton = new Button("编辑");
            editButton.setId("edit"+j);
            editButton.setOnAction(e->{
                editItem(((Button)e.getSource()).getId());
            });
            map.put("edit",editButton);
            observableList.addAll(FXCollections.observableArrayList(map));
        }
        dataTableView.setItems(observableList);
    }
    public void editItem(String name){
        if(name == null)
            return;
        int j = Integer.parseInt(name.substring(4,name.length()));
        Map data = homeworkList.get(j);
        initDialog();
        homeworkEditController.showDialog(data);
        MainApplication.setCanClose(false);
        stage.showAndWait();
    }

    @FXML
    public void initialize() {
        homeworkIdColumn.setCellValueFactory(new MapValueFactory<>("homeworkId"));
        courseNameColumn.setCellValueFactory(new MapValueFactory<>("courseName"));
        courseNumColumn.setCellValueFactory(new MapValueFactory<>("courseNum"));
        homeworkReleasingTimeColumn.setCellValueFactory(new MapValueFactory<>("homeworkReleasingTime"));
        homeworkDeadlineColumn.setCellValueFactory(new MapValueFactory<>("homeworkDeadline"));
        contentColumn.setCellValueFactory(new MapValueFactory<>("content"));
        editColumn.setCellValueFactory(new MapValueFactory<>("edit"));

        DataRequest req = new DataRequest();

        courseList = HttpRequestUtil.requestOptionItemList("/api/homework/getCourseItemOptionList",req);
        OptionItem item = new OptionItem(null,"0","请选择");

        courseComboBox.getItems().addAll(item);
        courseComboBox.getItems().addAll(courseList);

        dataTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        onQueryButtonClick();
    }

    private void initDialog() {
        if(stage!= null)
            return;
        FXMLLoader fxmlLoader ;
        Scene scene = null;
        try {
            fxmlLoader = new FXMLLoader(MainApplication.class.getResource("homework-edit-dialog.fxml"));
            scene = new Scene(fxmlLoader.load(), 260, 140);
            stage = new Stage();
            stage.initOwner(MainApplication.getMainStage());
            stage.initModality(Modality.NONE);
            stage.setAlwaysOnTop(true);
            stage.setScene(scene);
            stage.setTitle("作业编辑对话框！");
            stage.setOnCloseRequest(event ->{
                MainApplication.setCanClose(true);
            });
            homeworkEditController = (HomeworkEditController) fxmlLoader.getController();
            homeworkEditController.setHomeworkTableController(this);
            homeworkEditController.init();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //对于布置作业对话框的处理
    public void doClose(String cmd, Map<String, Object> data) {

        MainApplication.setCanClose(true);
        stage.close();
        if(!"ok".equals(cmd))
            return;

        Integer courseId = CommonMethod.getInteger(data,"courseId");
        if(courseId == null) {
            MessageDialog.showDialog("没有选中课程不能添加保存！");
            return;
        }

        String content = CommonMethod.getString(data, "content");
        if(content.isEmpty()) {
            MessageDialog.showDialog("作业内容不能为空，请添加！");
            return;
        }

        String deadline = CommonMethod.getString(data, "homeworkDeadline");
        if(deadline.isEmpty()) {
            MessageDialog.showDialog("请设置作业截止日期！");
            return;
        }

        DataRequest req = new DataRequest();
        req.add("courseId",courseId);
        req.add("homeworkId",CommonMethod.getInteger(data,"homeworkId"));
        req.add("content",content);
        req.add("releasingTime",CommonMethod.getString(data, "homeworkReleasingTime"));
        req.add("deadline",deadline);
        System.out.println(req.getData());


        DataResponse res;
        res = HttpRequestUtil.request("/api/homework/homeworkSave",req); //从后台获取所有学生信息列表集合
        if(res != null && res.getCode()== 0) {
            onQueryButtonClick();
        }
    }

    @FXML
    private void onAddButtonClick() {
        initDialog();
        homeworkEditController.showDialog(null);
        MainApplication.setCanClose(false);
        stage.showAndWait();
    }

    //修改作业功能
    @FXML
    private void onEditButtonClick() {
//        dataTableView.getSelectionModel().getSelectedItems();
        Map data = dataTableView.getSelectionModel().getSelectedItem();
        if(data == null) {
            MessageDialog.showDialog("没有选中，不能修改！");
            return;
        }
        initDialog();
        homeworkEditController.showDialog(data);
        MainApplication.setCanClose(false);
        stage.showAndWait();
    }

    @FXML
    private void onDeleteButtonClick() {
        Map<String,Object> form = dataTableView.getSelectionModel().getSelectedItem();
        if(form == null) {
            MessageDialog.showDialog("没有选择，不能删除");
            return;
        }
        int ret = MessageDialog.choiceDialog("确认要删除吗?");
        if(ret != MessageDialog.CHOICE_YES) {
            return;
        }
        Integer homeworkId = CommonMethod.getInteger(form,"homeworkId");
        DataRequest req = new DataRequest();
        req.add("homeworkId", homeworkId);
        DataResponse res = HttpRequestUtil.request("/api/homework/homeworkDelete",req);
        if(res.getCode() == 0) {
            onQueryButtonClick();
        }
        else {
            MessageDialog.showDialog(res.getMsg());
        }
    }
}
