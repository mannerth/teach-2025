package com.teach.javafx.controller;

import com.teach.javafx.AppStore;
import com.teach.javafx.MainApplication;
import com.teach.javafx.request.DataRequest;
import com.teach.javafx.request.DataResponse;
import com.teach.javafx.request.HttpRequestUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HonorCheckController {
    private final String HONOR_TITLE = "HONOR_TITLE";
    private final String HONOR_CONTEST = "HONOR_CONTEST";
    private final String HONOR_TECH = "HONOR_TECH";
    private final String HONOR_PRACTICE = "HONOR_PRACTICE";
    private final String HONOR_INTERNSHIP = "HONOR_INTERNSHIP";
    private final String HONOR_PROJ = "HONOR_PROJ";
    private final String HONOR_LECTURE = "HONOR_LECTURE";
    @FXML
    private GridPane gridPane;
    @FXML
    private BorderPane borderPane;

    private List<Map> honors = new ArrayList<>();
    private List<Map> title = new ArrayList<>();
    private List<Map> practice = new ArrayList<>();
    private List<Map> internship = new ArrayList<>();
    private List<Map> contest = new ArrayList<>();
    private List<Map> tech = new ArrayList<>();
    private List<Map> lecture = new ArrayList<>();
    private List<Map> proj = new ArrayList<>();

    private Stage filterStage = null;

    @FXML
    public void initialize(){
        requestHonors();
        arrangeHonor();
        setDisplay();
    }

    public void requestHonors(){
        DataRequest req = new DataRequest();
        req.add("personId", AppStore.getJwt().getId());
        String BASE_URL = "/api/honor";
        DataResponse res = HttpRequestUtil.request(BASE_URL +"/getHonors",req);
        if(res != null && res.getCode() == 0){
            honors = (ArrayList<Map>)(res.getData());
        }
        else {
            System.out.println("获取荣誉信息失败! ");
        }
    }

    //将不同荣誉按类别分配到相应的数组中
    public void arrangeHonor(){
        for(Map m : honors){
            String type = (String) m.get("honorType");
            switch (type) {
                case HONOR_TITLE -> title.add(m);
                case HONOR_CONTEST -> contest.add(m);
                case HONOR_INTERNSHIP -> internship.add(m);
                case HONOR_PRACTICE -> practice.add(m);
                case HONOR_PROJ -> proj.add(m);
                case HONOR_TECH -> tech.add(m);
                case HONOR_LECTURE -> lecture.add(m);
            }
        }
    }

    public void setDisplay(){
        gridPane.getChildren().clear();
        int row = 0;

        gridPane.add(new Label("荣誉称号"), 0, row);
        row = setItem(row, title);

        gridPane.add(new Label("学科竞赛"), 0, row);
        row = setItem(row, contest);

        gridPane.add(new Label("社会实践"), 0, row);
        row = setItem(row, practice);

        gridPane.add(new Label("科技成果"), 0, row);
        row = setItem(row, tech);

        gridPane.add(new Label("校外实习"), 0, row);
        row = setItem(row, internship);

        gridPane.add(new Label("培训讲座"), 0, row);
        row = setItem(row, lecture);

        gridPane.add(new Label("创新项目"), 0, row);
        row = setItem(row, proj);

        int numOfHonors = honors.size();
        if( numOfHonors == 0){
            gridPane.add(new Label("你目前没有个人荣誉诶\n继续加油吧！"), 3, 0);
        }
        else if(numOfHonors <= 2){
            gridPane.add(new Label("你目前有 " + numOfHonors + " 个荣誉耶\n小有成就！"), 3, 0);
        }
        else if(numOfHonors <= 5){
            gridPane.add(new Label("真棒！ 你现在有 " + numOfHonors + " 个荣誉\n再接再厉吧！"), 3, 0);
        }
        else {
            gridPane.add(new Label("Wow，你现在有 " + numOfHonors + " 个荣誉\nAmazing！"), 3, 0);
        }
    }

    private int setItem(int row, List<Map> title) {
        for(Map m : title){
            gridPane.add(new Label((String) m.get("honorContent")), 1, row);
            row++;
        }
        if(title.isEmpty()){
            gridPane.add(new Label("暂无奖项"),1,row);
            row++;
        }
        return row;
    }

    public void openFilterWindow(List<Map> honors, String honorType){
        FXMLLoader loader = new FXMLLoader(MainApplication.class.getResource("honor-filter-panel.fxml"));
        try {
            Scene scene = new Scene(loader.load(), 600, 400);
            HonorFilterController honorFilterController = (HonorFilterController) loader.getController();
            honorFilterController.setData(honors, honorType);
            honorFilterController.setDisplay();
            filterStage = new Stage();
            filterStage.setScene(scene);
            filterStage.setTitle(honorType);
            filterStage.initOwner(getStage());
            filterStage.initModality(Modality.WINDOW_MODAL);
            filterStage.setOnCloseRequest(windowEvent -> filterStage = null);
            filterStage.setResizable(false);
            filterStage.show();
        }
        catch (IOException o){
            System.out.println("打开分类浏览荣誉的视图失败: " + o.getMessage());
        }
    }

    public void onFilterButtonClick(ActionEvent event) {
        Button btn = (Button) event.getTarget();
        String text = btn.getText();
        switch (text) {
            case "荣誉称号" -> openFilterWindow(title, text);
            case "学科竞赛" -> openFilterWindow(contest, text);
            case "科技成果" -> openFilterWindow(tech, text);
            case "社会实践" -> openFilterWindow(practice, text);
            case "培训讲座" -> openFilterWindow(lecture, text);
            case "创新项目" -> openFilterWindow(proj, text);
            case "校外实习" -> openFilterWindow(internship, text);
        }
    }

    private Stage getStage(){
        Scene scene = this.borderPane.getScene();
        return (Stage) scene.getWindow();
    }
}
