package com.teach.javafx.controller;

import com.teach.javafx.MainApplication;
import com.teach.javafx.controller.base.MessageDialog;
import com.teach.javafx.request.DataRequest;
import com.teach.javafx.request.DataResponse;
import com.teach.javafx.request.HttpRequestUtil;
import com.teach.javafx.request.OptionItem;
import com.teach.javafx.util.CommonMethod;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
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

public class CourseExController {
    @FXML
    private TableView<Map> dataTableView;
    @FXML
    private TableColumn<Map,String> teacherNumColumn;
    @FXML
    private TableColumn<Map,String> informationColumn;
    @FXML
    private TableColumn<Map,String> maxStuNumColumn;
    @FXML
    private TableColumn<Map,String> courseNumColumn;
    @FXML
    private TableColumn<Map,String> courseNameColumn;
    @FXML
    private TableColumn<Map,String> creditColumn;
    @FXML
    private TableColumn<Map,String> course_numColumn;
    @FXML
    private TableColumn<Map,String> timeColumn;
    @FXML
    private TableColumn<Map,String> placeColumn;
    @FXML
    private TableColumn<Map,String> choosableColumn;
    @FXML
    private TableColumn<Map, Button> editColumn;

    private String[] day = {"周0","周一","周二","周三","周四","周五","周六","周七"};
    private String[] classes = {"","第一节","第二节","第三节","第四节","第五节"};

    private ArrayList<Map> courseExList = new ArrayList();  // 选课信息列表数据
    private ObservableList<Map> observableList= FXCollections.observableArrayList();  // TableView渲染列表

    @FXML
    private ComboBox<OptionItem> teacherComboBox;
//OptionItem

    private List<OptionItem> teacherList;
    @FXML
    private ComboBox<OptionItem> courseComboBox;


    private List<OptionItem> courseList;

    private CourseExEditController courseExEditController = null;

    private Stage stage = null;

    public List<OptionItem> getTeacherList() {
        return teacherList;
    }
    public List<OptionItem> getCourseList() {
        return courseList;
    }


    @FXML
    private void onQueryButtonClick(){
        Integer id;
        if(!courseComboBox.getSelectionModel().isEmpty() && courseComboBox.getValue().getId() != null)
            id = courseComboBox.getValue().getId();
        else{
           id = 0;
        }
        String s = teacherComboBox.getSelectionModel().isEmpty()? "" : ""+teacherComboBox.getSelectionModel().getSelectedItem().getValue();
        requestMainContent(id,s);
    }

    private void requestMainContent(int id, String s){
        DataRequest request = new DataRequest();
        request.add("courseId",id);
        request.add("num",s);
        DataResponse res = HttpRequestUtil.request("/api/CourseEx/findByTeacherCourse",request);
        if(res!=null && res.getCode()==0){
            courseExList = (ArrayList<Map>) res.getData();
        }
        setTableViewData();
    }

    String getTimeString(String s){
        String res = day[s.charAt(0)-'0'] + classes[s.charAt(1)-'0'];;
        if(s.length()>2){
            String[] inf = s.substring(3).split("-");
            if(inf.length==2){
                String beginString = inf[0];
                String endString = inf[1];
                res += "第"+beginString+"周到第"+endString+"周";
            }
        }


        return res;
    }

    private void setTableViewData() {
        observableList.clear();
        Map map;
        Button editButton;
        for (int j = 0; j < courseExList.size(); j++) {
            map = courseExList.get(j);
            map.put("time0",map.get("time"));
            map.replace("time",getTimeString(map.get("time").toString()));
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
        Map data = courseExList.get(j);
        initDialog();
        courseExEditController.showDialog(data);
        MainApplication.setCanClose(false);
        stage.showAndWait();
    }

    @FXML
    public void initialize() {
        course_numColumn.setCellValueFactory(new MapValueFactory<>("course_num"));  //设置列值工程属性
        teacherNumColumn.setCellValueFactory(new MapValueFactory<>("teacher_name"));
        informationColumn.setCellValueFactory(new MapValueFactory<>("information"));
        maxStuNumColumn.setCellValueFactory(new MapValueFactory<>("max_stu_num"));
        courseNumColumn.setCellValueFactory(new MapValueFactory<>("courseNum"));
        courseNameColumn.setCellValueFactory(new MapValueFactory<>("courseName"));
        creditColumn.setCellValueFactory(new MapValueFactory<>("credit"));
        timeColumn.setCellValueFactory(new MapValueFactory<>("time"));
        placeColumn.setCellValueFactory(new MapValueFactory<>("place"));
        choosableColumn.setCellValueFactory((new MapValueFactory<>("is_choosable")));
        editColumn.setCellValueFactory(new MapValueFactory<>("edit"));

        DataRequest req =new DataRequest();
        teacherList = HttpRequestUtil.requestOptionItemList("/api/CourseEx/getTeacherOptionItemList",req); //从后台获取所有学生信息列表集合
        courseList = HttpRequestUtil.requestOptionItemList("/api/score/getCourseItemOptionList",req); //从后台获取所有学生信息列表集合
        OptionItem item = new OptionItem(null,"0","请选择");
        teacherComboBox.getItems().addAll(item);
        teacherComboBox.getItems().addAll(teacherList);
        courseComboBox.getItems().addAll(item);
        courseComboBox.getItems().addAll(courseList);

        dataTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        onQueryButtonClick();
    }

    private void initDialog() {
        if(stage!= null)
            return;
        FXMLLoader fxmlLoader ;
        Scene scene;
        try {
            fxmlLoader = new FXMLLoader(MainApplication.class.getResource("courseEx-edit-dialog.fxml"));
            scene = new Scene(fxmlLoader.load(), 300, 500);
            stage = new Stage();
            stage.initOwner(MainApplication.getMainStage());
            stage.initModality(Modality.NONE);
            stage.setAlwaysOnTop(true);
            stage.setScene(scene);
            stage.setTitle("发布课程对话框");
            stage.setOnCloseRequest(event ->{
                MainApplication.setCanClose(true);
            });
            courseExEditController = (CourseExEditController) fxmlLoader.getController();
            courseExEditController.setScoreTableController(this);
            courseExEditController.init();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void doClose(String cmd, Map<String, Object> data) {
        MainApplication.setCanClose(true);
        stage.close();
        if(!"ok".equals(cmd))
            return;
        DataResponse res;
        DataRequest req = new DataRequest();
        req.setData(data);
        res = HttpRequestUtil.request("/api/CourseEx/postNewCourse",req);
        if(res != null && res.getCode()== 0) {
            requestMainContent(0,"");
        }else{
            MessageDialog.showDialog(res.getMsg());
        }
    }
    @FXML
    private void onAddButtonClick() {
        initDialog();
        courseExEditController.showDialog(null);
        MainApplication.setCanClose(false);
        stage.showAndWait();
    }
    @FXML
    private void onEditButtonClick() {
        dataTableView.getSelectionModel().getSelectedItems();
        Map data = dataTableView.getSelectionModel().getSelectedItem();
        if(data == null) {
            MessageDialog.showDialog("没有选中，不能修改！");
            return;
        }
        initDialog();
        courseExEditController.showDialog(data);
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
        Integer courseExId = CommonMethod.getInteger(form,"courseExId");
        DataRequest req = new DataRequest();
        req.add("courseExId", courseExId);
        DataResponse res = HttpRequestUtil.request("/api/CourseEx/deleteCourse",req);
        if(res.getCode() == 0) {
            onQueryButtonClick();
        }
        else {
            MessageDialog.showDialog(res.getMsg());
        }
    }
}
