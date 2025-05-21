package com.teach.javafx.controller;


import com.teach.javafx.AppStore;
import com.teach.javafx.controller.base.MessageDialog;
import com.teach.javafx.request.DataRequest;
import com.teach.javafx.request.DataResponse;
import com.teach.javafx.request.HttpRequestUtil;
import com.teach.javafx.util.CommonMethod;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CourseListController {

    ///储存课程信息 键”24“表示周二第四节，值用 ',' 分割信息     26,1-16:name,place
    protected Map< String, String > course = new HashMap<>();
    protected int theWeek = 1;
    protected List<VBox> present = new ArrayList<>();
    @FXML
    private GridPane content;           //课表主要内容
    @FXML
    private TextField courseName;       //添加课程的课程名称输入框
    @FXML
    private TextField coursePlace;      //添加课程的课程地址输入框
    @FXML
    private ComboBox time;              //添加课程的课程时间复选框
    @FXML
    private ComboBox time1;             //添加课程的课程节次复选框
    @FXML
    private ComboBox week;              //切换周次的复选框
    @FXML
    private ComboBox beginWeek;         //添加课程周次复选框
    @FXML
    private ComboBox endWeek;

    private Integer mx = null; //鼠标点击格节次
    private Integer my = null; //鼠标点击格日期
    @FXML
    private Label testU;

    @FXML
    public void initialize() {
        getCourseMap();
        setContent();
        initComBox();
    }

    public void delete(){
        if(mx==null||my==null){
            MessageDialog.showDialog("当前未选中课程，无法删除");
            return;
        }
        String s = my.toString()+ mx.toString();
        if(MessageDialog.choiceDialog("要删除周"+my+"第"+ mx + "节，确定吗？")!=MessageDialog.CHOICE_YES){
            return;
        }
        DataRequest request = new DataRequest();
        request.add("preKey",s);
        request.add("week",theWeek);
        request.add("personId", AppStore.getJwt().getId());
        DataResponse response = HttpRequestUtil.request("/api/studentCourseList/deleteCourse",request);
        if(response!=null){
            MessageDialog.showDialog(response.getMsg());
            if(response.getCode()==1){
                getCourseMap();
                setContent();
                my = null;
                mx = null;
                testU.setText("春风得意马蹄疾，不信人间有别离");
            }
        }else{
            MessageDialog.showDialog("发生错误course-list-1");
        }
    }

    public void onAddNewCourse(){
        String theName = courseName.getText();
        String thePlace = coursePlace.getText();
        Map<Character,Integer> convert = new HashMap<>();
        convert.put('一',1);convert.put('二',2);convert.put('三',3);convert.put('四',4);convert.put('五',5);convert.put('六',6);convert.put('日',7);convert.put('七',7);
        if(theName.isEmpty() || thePlace.isEmpty()||time.getSelectionModel().isEmpty()||time1.getSelectionModel().isEmpty()){
            MessageDialog.showDialog("请输入完整的信息");
            return;
        }
        char course_time = ((String) time.getValue()).charAt(1);
        char course_time_c = ((String)time1.getValue()).charAt(1);
        String inf = "" + convert.get(course_time) + convert.get(course_time_c);
        if(beginWeek.getValue()!=null&&endWeek.getValue()!=null){
            int a = Integer.parseInt(beginWeek.getValue().toString());
            if(a>=1&&a<=20)
                inf += ","+beginWeek.getValue()+"-"+endWeek.getValue();
        }
        DataRequest req = new DataRequest();
        req.add("personId", AppStore.getJwt().getId());
        req.add("content",theName+","+thePlace);
        req.add("time",inf);
        DataResponse res = HttpRequestUtil.request("/api/studentCourseList/addCourse",req);
        MessageDialog.showDialog(res.getMsg());
        getCourseMap();
        setContent();
        clearAddInf();
    }

    /// 清除添加信息框内容
    private void clearAddInf(){
        courseName.clear();
        coursePlace.clear();
        time.setValue(time.getPromptText());
        time1.setValue(time1.getPromptText());
        beginWeek.setValue(beginWeek.getPromptText());
        endWeek.setValue(endWeek.getPromptText());
    }

    /// 初始化复选框
    private void initComBox(){
        time.getItems().addAll("周一","周二","周三","周四","周五","周六","周日");
        time1.getItems().addAll("第一节","第二节","第三节","第四节","第五节");
        week.getItems().addAll("第1周","第2周","第3周","第4周","第5周","第6周","第7周","第8周","第9周","第10周","第11周","第12周","第13周","第14周","第15周","第16周","第17周","第18周","第19周","第20周");
        week.setVisibleRowCount(6);
        week.setValue("第1周");
        week.valueProperty().addListener(
                (observable, oldValue, newValue) -> {
                    theWeek = ((String)newValue).charAt(1) - '0';
                    setContent();
                }
        );
        beginWeek.getItems().addAll(1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20);
        endWeek.getItems().addAll(1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20);
    }

    /// 显示课表课程信息   33,4-4:nnn,ppp
    private void setContent(){
        content.getChildren().removeAll(present);
        present.clear();
        for(String key : course.keySet()){ //遍历Map
            int day = key.charAt(0)-'0';
            int cl = key.charAt(1)-'0';
            if(key.length()>=6){
                String subStr = key.substring(3);
                String[] durationStr = subStr.split("-");
                int begin = 0,end = 0;
                for(int i = 0; i < durationStr[0].length(); ++i){
                    begin += (durationStr[0].charAt(durationStr[0].length()-1-i) - '0')*Math.pow(10,i);
                }
                for(int i = 0; i < durationStr[1].length(); ++i){
                    end += (durationStr[1].charAt(durationStr[1].length()-1-i) - '0')*Math.pow(10,i);
                }
                if(theWeek<begin||theWeek>end)
                    continue;
            }
            String[] inf = course.get(key).split(","); //分割信息
            VBox cou = new VBox();
            cou.setPrefHeight(content.getPrefHeight());
            cou.setPrefWidth(content.getPrefWidth());
            for(String i : inf){  //将信息添加到VBox里
                TextArea textArea = new TextArea(i);
                textArea.setEditable(false);
                textArea.setWrapText(true);
                textArea.setOnMouseClicked(event -> {//鼠标点击位置监听
                    mx = cl;
                    my = day;
                    testU.setText("当前鼠标选中周"+day+"第"+ cl + "节");
                });
                cou.getChildren().add(textArea);
            }
            present.add(cou);
            content.add(cou,day,cl);  //显示VBox
        }
    }

    /// 向后端请求课表信息
    private void getCourseMap(){
        DataRequest req = new DataRequest();
        req.add("personId", AppStore.getJwt().getId());
        DataResponse res = HttpRequestUtil.request("/api/studentCourseList/getStudentCourseList",req);
        if(res!=null){
            course = (Map<String, String>) res.getData();
        }
    }
}