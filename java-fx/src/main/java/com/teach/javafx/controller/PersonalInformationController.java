package com.teach.javafx.controller;

import com.teach.javafx.AppStore;
import com.teach.javafx.request.JwtResponse;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class PersonalInformationController {
    @FXML
    private VBox content;

    @FXML
    public void initialize(){
        ObservableList<Node> inf = content.getChildren();
        JwtResponse jwt = AppStore.getJwt();
        Label l1 = new Label(jwt.getUsername());
        Label l2 = new Label(jwt.getRole());
        inf.add(l1);
    }
}
