package com.teach.javafx.controller;

import com.teach.javafx.AppStore;
import com.teach.javafx.controller.base.MessageDialog;
import com.teach.javafx.request.DataRequest;
import com.teach.javafx.request.DataResponse;
import com.teach.javafx.request.HttpRequestUtil;
import com.teach.javafx.request.JwtResponse;
import com.teach.javafx.util.CommonMethod;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.Map;

public class PersonalInformationController {
    @FXML
    private TextArea address;
    @FXML
    private TextArea email;
    @FXML
    private TextArea phone;
    @FXML
    private TextArea dept;
    @FXML
    private TextArea major;
    @FXML
    private TextArea className;
    @FXML
    private TextArea num;
    @FXML
    private TextArea gender;
    @FXML
    private TextArea name;


    private Map inf_map;
    JwtResponse jwt;

    @FXML
    public void initialize(){
        requestInformation();
        showInf();
    }

    private void requestInformation(){
        jwt = AppStore.getJwt();
        DataRequest dataRequest = new DataRequest();
        dataRequest.getData().put("personId",jwt.getId());
        DataResponse res = HttpRequestUtil.request("/api/student/getStudentInfo",dataRequest);
        inf_map = (Map<String, Object>) res.getData();
    }

    private void showInf(){
        name.setText(inf_map.get("name").toString());
        gender.setText(inf_map.get("genderName").toString());
        num.setText(inf_map.get("num").toString());
        className.setText(inf_map.get("className").toString());
        major.setText(inf_map.get("major").toString());
        dept.setText(inf_map.get("dept").toString());
        phone.setText(inf_map.get("phone")==null? null : inf_map.get("phone").toString() );
        address.setText(inf_map.get("address")==null ? null : inf_map.get("address").toString());
        email.setText(inf_map.get("email")==null? null : inf_map.get("email").toString());
    }
    @FXML
    private void onSave(){
        DataRequest req = new DataRequest();
        Integer personId = jwt.getId();
        req.add("personId",personId);
        inf_map.put("phone", phone.getText());
        inf_map.put("email", email.getText());
        inf_map.put("address",address);
        req.add("form",inf_map);
        DataResponse res = HttpRequestUtil.request("/api/student/studentEditSave", req);
        if (res.getCode() == 0) {
            MessageDialog.showDialog("提交成功！");
            initialize();
        } else {
            MessageDialog.showDialog(res.getMsg());
        }
    }
}
