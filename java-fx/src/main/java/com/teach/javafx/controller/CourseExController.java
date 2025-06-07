package com.teach.javafx.controller;

import com.teach.javafx.AppStore;
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
    @FXML
    private ComboBox<OptionItem> student_selected;
    @FXML
    private Button delete;
    @FXML
    private Button add;
    @FXML
    private Button edit;

    private String[] day = {"周0","周一","周二","周三","周四","周五","周六","周七"};
    private String[] classes = {"","第一节","第二节","第三节","第四节","第五节"};

    private ArrayList<Map> courseExList = new ArrayList();  // 选课信息列表数据
    private ArrayList<Map> selectedCourseList = null;
    private ObservableList<Map> observableList= FXCollections.observableArrayList();  // TableView渲染列表

    @FXML
    private ComboBox<OptionItem> teacherComboBox;
//OptionItem

    private List<OptionItem> teacherList;
    @FXML
    private ComboBox<OptionItem> courseComboBox;

    private String role = "";

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
        String s = teacherComboBox.getSelectionModel().isEmpty()? "" : teacherComboBox.getSelectionModel().getSelectedItem().getValue();
        requestMainContent(id,s);
    }

    private void requestMainContent(int id, String s){
        DataRequest request = new DataRequest();
        request.add("courseId",id);
        request.add("num",s);
        if(isStudent()){
            Boolean is = true;
            request.add("is_choosable", is);
        }
        DataResponse res = HttpRequestUtil.request("/api/CourseEx/findByTeacherCourse",request);
        if(res!=null && res.getCode()==0){
            courseExList = (ArrayList<Map>) res.getData();
        }
        if(isStudent()){
            DataRequest r = new DataRequest();
            r.add("personId", AppStore.getJwt().getId());
            DataResponse response = HttpRequestUtil.request("/api/CourseEx/getSelectedCourse", r);
            selectedCourseList = (ArrayList<Map>) response.getData();
        }
        setTableViewData();
    }
    private boolean isStudent(){
        return role.equals("ROLE_STUDENT");
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
        ArrayList<Map> OpLis = courseExList;
        boolean is_selected = false;
        if(isStudent()){
            is_selected = student_selected.getValue().getId()==1;
            if(is_selected){
                OpLis = selectedCourseList;
            }
        }
        for (int j = 0; j < OpLis.size(); j++) {
            map = OpLis.get(j);
            map.put("time0",map.get("time"));
            map.replace("time",getTimeString(map.get("time").toString()));
            editButton = new Button("编辑");
            editButton.setId("edit"+j);
            if(role.equals("ROLE_STUDENT")){
                if(is_selected){
                    editButton.setText("取消选课");
                    editButton.setOnAction(e->{
                        cancelSelect(((Button) e.getSource()).getId());
                    });
                }else{
                    editButton.setText("选课");
                    editButton.setOnAction(e->{
                        selectCourse(((Button) e.getSource()).getId());
                    });
                }
            }else{
                editButton.setOnAction(e->{
                    editItem(((Button)e.getSource()).getId());
                });
            }
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
        role = AppStore.getJwt().getRole();
        DataRequest req =new DataRequest();
        teacherList = HttpRequestUtil.requestOptionItemList("/api/CourseEx/getTeacherOptionItemList",req); //从后台获取所有学生信息列表集合
        courseList = HttpRequestUtil.requestOptionItemList("/api/score/getCourseItemOptionList",req); //从后台获取所有学生信息列表集合
        OptionItem item = new OptionItem(null,"0","请选择");
        teacherComboBox.getItems().addAll(item);
        teacherComboBox.getItems().addAll(teacherList);
        courseComboBox.getItems().addAll(item);
        courseComboBox.getItems().addAll(courseList);
        dataTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        setRole();
        onQueryButtonClick();
    }

    private void setRole(){
        if(isStudent()){
            selectedCourseList = new ArrayList<>();
            add.setVisible(false);
            delete.setVisible(false);
            edit.setVisible(false);
            List<OptionItem> ss = new ArrayList<>();
            ss.add(new OptionItem(0, "0", "选课列表"));
            ss.add(new OptionItem(1,"1", "已选课程"));
            student_selected.getItems().addAll(ss);
            student_selected.getSelectionModel().select(0);
            student_selected.setOnAction(event->{
                onQueryButtonClick();
            });
        }else{
            student_selected.setVisible(false);
        }
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
    private void selectCourse(String name){
        Integer personId = AppStore.getJwt().getId();
        if(name == null)
            return;
        int j = Integer.parseInt(name.substring(4,name.length()));
        Map data = courseExList.get(j);
        Integer courseExId = CommonMethod.getInteger(data, "courseExId");
        DataRequest request = new DataRequest();
        request.add("personId", personId);
        request.add("courseExId", courseExId);
        DataResponse response = HttpRequestUtil.request("/api/CourseEx/studentSelectCourse", request);
        if(response!=null){
            MessageDialog.showDialog(response.getMsg());
        }
    }
    private void cancelSelect(String name){
        Integer personId = AppStore.getJwt().getId();
        if(name == null)
            return;
        int j = Integer.parseInt(name.substring(4,name.length()));
        Map data = selectedCourseList.get(j);
        Integer courseExId = CommonMethod.getInteger(data, "courseExId");
        DataRequest request = new DataRequest();
        request.add("personId", personId);
        request.add("courseExId", courseExId);
        DataResponse response = HttpRequestUtil.request("/api/CourseEx/studentCancelSelectCourse", request);
        if(response!=null){
            MessageDialog.showDialog(response.getMsg());
            onQueryButtonClick();
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
