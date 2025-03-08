package com.teach.javafx.controller;


import com.teach.javafx.AppStore;
import com.teach.javafx.request.DataRequest;
import com.teach.javafx.request.DataResponse;
import com.teach.javafx.request.HttpRequestUtil;
import com.teach.javafx.util.CommonMethod;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.util.HashMap;
import java.util.Map;

public class CourseListController {

    ///储存课程信息 键”24“表示周二第四节，值用 ',' 分割信息
    protected Map< String, String > course = new HashMap<>();

    @FXML
    private GridPane content;


    @FXML
    public void initialize() {
        setContent();
    }

    /// 向课表中添加课程信息
    private void setContent(){
        moni();
        for(String key : course.keySet()){ //遍历Map
            int day = key.charAt(0)-'0';
            int cl = key.charAt(1)-'0';
            String[] inf = course.get(key).split(","); //分割信息
            VBox cou = new VBox();
            for(String i : inf){  //将信息添加到VBox里
                cou.getChildren().add(new Label(i));
            }
            content.add(cou,day,cl);  //显示VBox
        }
    }

    private void moni(){
//        course.put("61","java开发,软件园实验楼203");
//        course.put("23","高等数学（2）,软件园1区207");
        getCourseMap();
    }

    private void getCourseMap(){
        DataRequest req = new DataRequest();
        req.add("personId", AppStore.getJwt().getId());
        DataResponse res = HttpRequestUtil.request("/api/studentCourseList/getStudentCourseList",req);
        if(res!=null){
            course = (Map<String, String>) res.getData();
        }
    }
}