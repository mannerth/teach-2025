package com.teach.javafx.models;

import com.teach.javafx.util.CommonMethod;
import javafx.scene.control.Button;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

public class Honor {
    private Integer honorId;

    //荣誉类型
    private String honorType;

    //荣誉内容
    private String honorContent;
    private String studentName;
    private String studentNum;
    private Integer studentId;
    private List<Button> actions = new ArrayList<>();

    private static Map<String, String> nameIdMap = new HashMap<>();

    static {
        nameIdMap.put("荣誉称号","HONOR_TITLE");
        nameIdMap.put("学科竞赛","HONOR_CONTEST");
        nameIdMap.put("社会实践","HONOR_PRACTICE");
        nameIdMap.put("科技成果","HONOR_TECH");
        nameIdMap.put("培训讲座","HONOR_LECTURE");
        nameIdMap.put("校外实习","HONOR_INTERNSHIP");
        nameIdMap.put("创新项目","HONOR_PROJ");
    }

    public Honor(){

    }

    public Honor(Map m){
        this.honorId = CommonMethod.getInteger(m, "honorId");
        this.honorType = CommonMethod.getString(m, "honorType");
        this.honorContent = CommonMethod.getString(m, "honorContent");
        this.studentName = CommonMethod.getString(m,"studentName");
        this.studentNum = CommonMethod.getString(m,"studentNum");
        this.studentId = CommonMethod.getInteger(m,"studentId");
    }

    public static String getTypeId(String name){
        return nameIdMap.get(name);
    }

    public static String getTypeName(String id){
        final String[] name = new String[1];
        nameIdMap.forEach(new BiConsumer<String, String>() {
            @Override
            public void accept(String string, String string2) {
                if(id.equals(string2)){
                    name[0] = string;
                }
            }
        });
        return name[0];
    }

    public Integer getHonorId() {
        return honorId;
    }

    public void setHonorId(Integer honorId) {
        this.honorId = honorId;
    }

    public String getHonorType() {
        return honorType;
    }

    public void setHonorType(String honorType) {
        this.honorType = honorType;
    }

    public String getHonorContent() {
        return honorContent;
    }

    public void setHonorContent(String honorContent) {
        this.honorContent = honorContent;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getStudentNum() {
        return studentNum;
    }

    public void setStudentNum(String studentNum) {
        this.studentNum = studentNum;
    }

    public Integer getStudentId() {
        return studentId;
    }

    public void setStudentId(Integer studentId) {
        this.studentId = studentId;
    }

    public List<Button> getActions() {
        return actions;
    }

    public void setActions(List<Button> actions) {
        this.actions = actions;
    }
}