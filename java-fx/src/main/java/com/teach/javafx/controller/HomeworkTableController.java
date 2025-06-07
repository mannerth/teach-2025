package com.teach.javafx.controller;

import com.teach.javafx.AppStore;
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
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
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
    private TableColumn<Map, String> courseExNumColumn;
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

    private List<OptionItem> studentList;

    @FXML
    private ComboBox<OptionItem> courseExComboBox;

    private List<OptionItem> courseExList;

    private HomeworkEditController homeworkEditController = null;
    private Stage stage = null;

    @FXML
    private FlowPane flowPane;

    @FXML
    private ImageView photoImageView;

    @FXML
    private VBox VBox;

    @FXML
    private Text studentOneText;

    @FXML
    private Text studentTwoText;

    @FXML
    private ImageView studentOnePhotoImageView;
    @FXML
    private ImageView studentTwoPhotoImageView;

    public List<OptionItem> getCourseList() {
        return courseList;
    }

    public List<OptionItem> getCourseExList() {
        return courseExList;
    }

    public List<OptionItem> getCourseExListByCourseId(Integer courseId) {
        return courseExList;
    }

    @FXML
    private void onQueryButtonClick(){
        String roleName = AppStore.getJwt().getRole();
        Integer courseId = 0;
        OptionItem op;
        op = courseComboBox.getSelectionModel().getSelectedItem();
        if(op != null)
            courseId = Integer.parseInt(op.getValue());
        DataResponse res;
        DataRequest req = new DataRequest();
        req.add("courseId",courseId);
        if(roleName.equals("ROLE_TEACHER") || roleName.equals("ROLE_ADMIN")) {
            res = HttpRequestUtil.request("/api/homework/getHomeworkList", req); //从后台获取所有作业列表集合
            if(res != null && res.getCode()== 0) {
                homeworkList = (ArrayList<Map>)res.getData();
            }
        }else if (roleName.equals("ROLE_STUDENT")) {
            req.add("personId",AppStore.getJwt().getId());
            res = HttpRequestUtil.request("/api/studentHomework/getStudentHomeworkList", req); //从后台获取所有作业列表集合
            if(res != null && res.getCode()== 0) {
                homeworkList = (ArrayList<Map>)res.getData();
            }
        }

        setTableViewData();
    }

    private void setTableViewData() {
        String roleName = AppStore.getJwt().getRole();
        observableList.clear();
        Map map;
        Button editButton;
        for (int j = 0; j < homeworkList.size(); j++) {
            map = homeworkList.get(j);
            if(roleName.equals("ROLE_TEACHER")) {
                editButton = new Button("编辑");
                editButton.setId("edit" + j);
                editButton.setOnAction(e -> {
                    editItem(((Button) e.getSource()).getId());
                });
                map.put("edit", editButton);
            }
            try{
                if(roleName.equals("ROLE_STUDENT")) {
                    Button submitButton = new Button("提交作业");
                    // 直接将 homeworkId 存储到按钮的属性中
                    submitButton.getProperties().put("homeworkId", map.get("homeworkId"));
                    Map finalMap = map;
                    submitButton.setOnAction(e -> {
                        // 从按钮的属性中获取 homeworkId
                        this.homeworkId = Integer.parseInt(finalMap.get("homeworkId").toString()); // 设置当前作业的 homeworkId
                        onPhotoButtonClick(); // 调用 onPhotoButtonClick 方法
                    });
                    map.put("submit", submitButton);
                }
            }catch (Exception e){

            }

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

    public void displayPhoto(){
        String roleName = AppStore.getJwt().getRole();

        if(roleName.equals("ROLE_STUDENT")) {
            DataRequest req = new DataRequest();
            req.add("homeworkId", homeworkId +"");
            req.add("studentId", AppStore.getJwt().getId() + "");
            byte[] bytes = HttpRequestUtil.requestByteData("/api/studentHomework/getBlobByteDataByStudentHomework", req);
            if (bytes != null) {
                ByteArrayInputStream in = new ByteArrayInputStream(bytes);
                Image img = new Image(in);

                photoImageView.setImage(img);
            }else {
                photoImageView.setImage(null);
            }
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
//        DataResponse res =HttpRequestUtil.uploadFile("/api/base/uploadPhotoBlob",file.getPath(), homeworkId+"" );  //上传保存在主键为personId的Person记录的的Photo列中
        DataResponse res =HttpRequestUtil.uploadFile("/api/studentHomework/submitHomework",file.getPath(), AppStore.getJwt().getId()+","+homeworkId );
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
        courseExNumColumn.setCellValueFactory(new MapValueFactory<>("course_num"));
        homeworkReleasingTimeColumn.setCellValueFactory(new MapValueFactory<>("homeworkReleasingTime"));
        homeworkDeadlineColumn.setCellValueFactory(new MapValueFactory<>("homeworkDeadline"));
        contentColumn.setCellValueFactory(new MapValueFactory<>("content"));
        editColumn.setCellValueFactory(new MapValueFactory<>("edit"));
        submitColumn.setCellValueFactory(new MapValueFactory<>("submit"));

        // 获取用户信息
        String roleName = AppStore.getJwt().getRole();
        if (roleName.equals("ROLE_STUDENT")) {
            // 如果是学生，只显示提交作业列
            editColumn.setVisible(false);
            submitColumn.setVisible(true);
            flowPane.setVisible(false);
        } else if (roleName.equals("ROLE_TEACHER")) {
            // 如果是学生，隐藏编辑列和提交列
            editColumn.setVisible(true);
            submitColumn.setVisible(false);
            flowPane.setVisible(true);
        } else if (roleName.equals("ROLE_ADMIN")) {
            // 如果是管理员，全部不可见
            editColumn.setVisible(false);
            submitColumn.setVisible(false);
            flowPane.setVisible(true);
        }


        DataRequest req = new DataRequest();

        courseList = HttpRequestUtil.requestOptionItemList("/api/homework/getCourseItemOptionList", req);

        OptionItem item = new OptionItem(null,"0","请选择");

        courseComboBox.getItems().addAll(item);
        courseComboBox.getItems().addAll(courseList);

        dataTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);


        if (roleName.equals("ROLE_STUDENT")) {
            VBox.getChildren().remove(studentOneText);
            VBox.getChildren().remove(studentOnePhotoImageView);
            VBox.getChildren().remove(studentTwoText);
            VBox.getChildren().remove(studentTwoPhotoImageView);


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
        } else if (roleName.equals("ROLE_TEACHER") || roleName.equals("ROLE_ADMIN")) {
            VBox.getChildren().remove(photoImageView);
//            borderPane.getChildren().remove(photoImageView);
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
                        displayStudentOnePhoto();
                        displayStudentTwoPhoto();
                    }
                }
            });
        }


        onQueryButtonClick();


    }

    private void initDialog() {
        if(stage!= null)
            return;
        FXMLLoader fxmlLoader;
        Scene scene = null;
        try {
            fxmlLoader = new FXMLLoader(MainApplication.class.getResource("homework-edit-dialog.fxml"));
            scene = new Scene(fxmlLoader.load(), 260, 260);
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

        Integer courseExId = CommonMethod.getInteger(data,"courseExId");
        if(courseExId == null) {
            MessageDialog.showDialog("没有选中课序号不能添加保存！");
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
        req.add("courseExId",courseExId);
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

        studentOnePhotoImageView.setImage(null);
        studentTwoPhotoImageView.setImage(null);
    }


    // 添加两个方法分别用于加载学生一和学生二的作业图片
    public void displayStudentOnePhoto() {
        displayPhotoForStudent(2); // 假设学生一的 ID 为 2
    }

    public void displayStudentTwoPhoto() {
        displayPhotoForStudent(3); // 假设学生二的 ID 为 3
    }

    // 通用方法用于根据学生 ID 加载作业图片
    private void displayPhotoForStudent(int studentId) {
        DataRequest req = new DataRequest();
        req.add("homeworkId", homeworkId + "");
        req.add("studentId", studentId + "");
        byte[] bytes = HttpRequestUtil.requestByteData("/api/studentHomework/getBlobByteDataByStudentHomework", req);
        if (bytes != null) {
            ByteArrayInputStream in = new ByteArrayInputStream(bytes);
            Image img = new Image(in);
            if (studentId == 2) {
                studentOnePhotoImageView.setImage(img);
            } else if (studentId == 3) {
                studentTwoPhotoImageView.setImage(img);
            }
        } else {
            if (studentId == 2) {
                studentOnePhotoImageView.setImage(null);
            } else if (studentId == 3) {
                studentTwoPhotoImageView.setImage(null);
            }
        }
    }
}
