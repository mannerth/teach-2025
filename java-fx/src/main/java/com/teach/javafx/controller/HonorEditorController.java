package com.teach.javafx.controller;

import com.teach.javafx.controller.base.MessageDialog;
import com.teach.javafx.models.Honor;
import com.teach.javafx.models.Student;
import com.teach.javafx.request.DataRequest;
import com.teach.javafx.request.DataResponse;
import com.teach.javafx.request.HttpRequestUtil;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.util.*;

public class HonorEditorController {
    public ComboBox<Student> studentCombo;
    public ComboBox<String> typeCombo;
    public TextArea contentField;

    private List<Student> studentList = new ArrayList<>();

    private HonorEditorOpener opener;
    private Honor honor;

    @FXML
    public void initialize(){
        getStudentList();
        typeCombo.setItems(FXCollections.observableArrayList("荣誉称号","学科竞赛","社会实践","科技成果","培训讲座","校外实习","创新项目"));
    }

    public void init(Honor h, HonorEditorOpener o){
        this.honor = h;
        this.opener = o;
        setDataView(h);
    }

    private void setDataView(Honor h) {
        if(h == null){
            return;
        }
        typeCombo.getSelectionModel().select(Honor.getTypeName(h.getHonorType()));
        studentCombo.getSelectionModel().select(findStudentById(h.getStudentId()));
        contentField.setText(h.getHonorContent());
    }

    private Student findStudentById(Integer id){
        if(id == null){
            return null;
        }
        for(Student s : studentList){
            if(Objects.equals(s.getPersonId(), id)){
                return s;
            }
        }
        return null;
    }

    //从后端获取所有学生
    public void getStudentList(){
        DataRequest req = new DataRequest();
        DataResponse res = HttpRequestUtil.request("/api/student/getStudentList",req);
        assert res != null;
        if(res.getCode() == 0){
            List<Map> rawData = (ArrayList<Map>)res.getData();
            studentList.clear();
            for(Map m : rawData){
                Student s = new Student(m);
                studentList.add(s);
            }
            studentCombo.setItems(FXCollections.observableArrayList(studentList));
        }
    }

    public void onSave(){
        //数据验证
        Student s = studentCombo.getSelectionModel().getSelectedItem();
        String typeName = typeCombo.getSelectionModel().getSelectedItem();
        String content = contentField.getText();
        if(s == null){
            MessageDialog.showDialog("请选择学生!");
            return;
        }
        if(typeName == null){
            MessageDialog.showDialog("请选择荣誉类型");
            return;
        }
        if(content == null || content.isEmpty()){
            MessageDialog.showDialog("请输入荣誉内容");
            return;
        }
        DataRequest req = new DataRequest();
        req.add("personId",s.getPersonId());
        req.add("honorType",Honor.getTypeId(typeName));
        req.add("honorContent",content);
        req.add("honorId",honor == null ? null : honor.getHonorId());
        DataResponse res = HttpRequestUtil.request("/api/honor/saveHonor",req);
        assert res != null;
        if(res.getCode() == 0){
            MessageDialog.showDialog("保存成功");
            opener.hasSaved();
            ((Stage)contentField.getScene().getWindow()).close();
        }
        else{
            MessageDialog.showDialog(res.getMsg());
        }
    }
}
