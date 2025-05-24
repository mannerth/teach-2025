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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
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
    @FXML
    private TableColumn<Map, Button> submitColumn;

    private Integer homeworkId = null;


    private ArrayList<Map> homeworkList = new ArrayList();
    private ObservableList<Map> observableList= FXCollections.observableArrayList();

    @FXML
    private ComboBox<OptionItem> courseComboBox;

    private List<OptionItem> courseList;

    private HomeworkEditController homeworkEditController = null;
    private Stage stage = null;

    @FXML
    private ImageView photoImageView;

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

            Button submitButton = new Button("提交作业");
            // 直接将 homeworkId 存储到按钮的属性中
            submitButton.getProperties().put("homeworkId", map.get("homeworkId"));
            Map finalMap = map;
            submitButton.setOnAction(e -> {
                // 从按钮的属性中获取 homeworkId
                int homeworkId = Integer.parseInt(finalMap.get("homeworkId").toString());
                this.homeworkId = homeworkId; // 设置当前作业的 homeworkId
                onPhotoButtonClick(); // 调用 onPhotoButtonClick 方法
            });
            map.put("submit", submitButton);


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

//    public void displayPhoto(){
//        DataRequest req = new DataRequest();
//        req.add("fileName", "photo/" + homeworkId + ".jpg");  //个人照片显示
//        byte[] bytes = HttpRequestUtil.requestByteData("/api/base/getFileByteData", req);
//        if (bytes != null) {
//            ByteArrayInputStream in = new ByteArrayInputStream(bytes);
//            Image img = new Image(in);
//            photoImageView.setImage(img);
//        }
//
//    }

    public void displayPhoto(){
        DataRequest req = new DataRequest();
//        req.add("fileName", "photo/" + homeworkId + ".jpg");  //个人照片显示
//        byte[] bytes = HttpRequestUtil.requestByteData("/api/base/getFileByteData", req);  // 从后端服务器指定木下读取文件
        req.add("homeworkId", homeworkId +"");  //个人照片显示
        byte[] bytes = HttpRequestUtil.requestByteData("/api/base/getBlobByteData", req);  //从后端person表里读取图片
        if (bytes != null) {
            ByteArrayInputStream in = new ByteArrayInputStream(bytes);
            Image img = new Image(in);
            photoImageView.setImage(img);
        }

    }

    @FXML
    public void onPhotoButtonClick(){
        FileChooser fileDialog = new FileChooser();
        fileDialog.setTitle("图片上传");
//        fileDialog.setInitialDirectory(new File("C:/"));
        fileDialog.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("JPG 文件", "*.jpg"));
        File file = fileDialog.showOpenDialog(null);
        if(file == null)
            return;
//        DataResponse res =HttpRequestUtil.uploadFile("/api/base/uploadPhoto",file.getPath(),"photo/" + homeworkId + ".jpg");  //上传保存到服务器目录/photo/主键PersonId.jpg
        DataResponse res =HttpRequestUtil.uploadFile("/api/base/uploadPhotoBlob",file.getPath(), homeworkId+"" );  //上传保存在主键为personId的Person记录的的Photo列中
        if(res.getCode() == 0) {
            MessageDialog.showDialog("上传成功！");
            displayPhoto();
        }
        else {
            MessageDialog.showDialog(res.getMsg());
        }
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
        submitColumn.setCellValueFactory(new MapValueFactory<>("submit"));

        DataRequest req = new DataRequest();

        courseList = HttpRequestUtil.requestOptionItemList("/api/homework/getCourseItemOptionList",req);
        OptionItem item = new OptionItem(null,"0","请选择");

        courseComboBox.getItems().addAll(item);
        courseComboBox.getItems().addAll(courseList);

        dataTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);


        // 添加鼠标点击事件监听器
        dataTableView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 1) { // 单击事件
                Map selectedHomework = dataTableView.getSelectionModel().getSelectedItem();
                if (selectedHomework != null) {
                    Object homeworkIdObj = selectedHomework.get("homeworkId");
                    if (homeworkIdObj instanceof Integer) {
                        this.homeworkId = (int) homeworkIdObj;
                    } else if (homeworkIdObj instanceof String) {
                        this.homeworkId = Integer.parseInt((String) homeworkIdObj);
                    } else {
                        System.err.println("homeworkId is not a valid type");
                    }
                    displayPhoto(); // 调用 displayPhoto 方法
                }
            }
        });


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

        //处理作业截止日期小于或等于作业发布日期的情况
        LocalDateTime homeworkReleasingTime = CommonMethod.getLocalDateTime(data, "homeworkReleasingTime");
        LocalDateTime homeworkDeadline = CommonMethod.getLocalDateTime(data, "homeworkDeadLineLocalDateTime");
//        System.out.println(homeworkReleasingTime);
//        System.out.println(homeworkDeadline);
        if(!homeworkReleasingTime.isBefore(homeworkDeadline)){
            MessageDialog.showDialog("作业发布日期不得大于作业截止日期！");
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
