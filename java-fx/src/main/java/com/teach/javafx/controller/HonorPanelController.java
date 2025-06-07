package com.teach.javafx.controller;

import com.teach.javafx.MainApplication;
import com.teach.javafx.controller.base.*;
import com.teach.javafx.models.Honor;
import com.teach.javafx.request.DataRequest;
import com.teach.javafx.request.DataResponse;
import com.teach.javafx.request.HttpRequestUtil;
import com.teach.javafx.util.CommonMethod;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HonorPanelController implements HonorEditorOpener {

    @FXML
    public TableColumn<Honor, String> studentNum;
    @FXML
    public TableColumn<Honor, String> studentName;
    @FXML
    public TableColumn<Honor, String> honorType;
    @FXML
    public TableColumn<Honor, HBox> actions;
    @FXML
    public TableColumn<Honor, String> honorContent;
    @FXML
    public TextField numNameField;
    @FXML
    public ComboBox<String> typeCombo;
    @FXML
    public TableView<Honor> tableView;
    public Pagination pagination;
    private int pageSize = 18;
    private int honorCount;

    private List<Honor> honorList = new ArrayList<>();

    @FXML
    public void initialize(){
        //查询数据总条数
        getAllCount();

        studentNum.setCellValueFactory(new HonorValueFactory());
        studentName.setCellValueFactory(new HonorValueFactory());
        honorType.setCellValueFactory(new HonorValueFactory());
        honorContent.setCellValueFactory(new HonorValueFactory());
        actions.setCellValueFactory(new HonorActionValueFactory());

        typeCombo.setItems(FXCollections.observableArrayList("全部","荣誉称号","学科竞赛","社会实践","科技成果","培训讲座","校外实习","创新项目"));
        pagination.setPageCount(getPageCount());
        pagination.setPageFactory(this::createView);
    }

    //根据页码创建视图
    public Node createView(int pageIndex){
        DataRequest req = new DataRequest();
        Map form = new HashMap<>();
        form.put("numName", numNameField.getText());
        form.put("honorType",typeCombo.getSelectionModel().getSelectedItem() == null ? null : Honor.getTypeId(typeCombo.getSelectionModel().getSelectedItem()));
        req.add("form",form);
        //分页查询
        req.add("page",pageIndex);
        req.add("pageSize", pageSize);
        DataResponse res = HttpRequestUtil.request("/api/honor/getHonorList",req);
        assert res != null;
        if(res.getCode() == 0){
            //更新页码数
            List<Map> rawData = (ArrayList<Map>) ((Map)res.getData()).get("honorData");
            int count = (int)(double)((Map)res.getData()).get("count");
            honorCount = count;
            pagination.setPageCount(getPageCount());
            honorList.clear();
            for(Map m : rawData){
                Honor h = new Honor(m);
                Button editButton = new Button("编辑");
                Button deleteButton = new Button("删除");
                editButton.setOnAction(this::onEdit);
                deleteButton.setOnAction(this::onDelete);
                h.getActions().add(editButton);
                h.getActions().add(deleteButton);
                honorList.add(h);
            }
            setDataView();
        }
        return new StackPane();
    }

    //查询数据总条数
    private int getAllCount(){
        DataRequest req = new DataRequest();
        DataResponse res = HttpRequestUtil.request("/api/honor/getHonorCount",req);
        assert res != null;
        if(res.getCode() == 0){
            honorCount = (int)(double)res.getData();
        }
        else{
            MessageDialog.showDialog(res.getMsg());
        }
        return honorCount;
    }

    private int getPageCount(){
        int mod = honorCount % pageSize;
        if(honorCount <= 0){
            return 1;
        }
        if(mod == 0){
            return honorCount / pageSize;
        }
        else{
            return honorCount / pageSize + 1;
        }
    }

    private void setDataView(){
        tableView.getItems().clear();
        tableView.setItems(FXCollections.observableArrayList(honorList));
    }

    public void onQueryButtonClick(){
        createView(0);
        pagination.setCurrentPageIndex(0);
    }

    public void onEdit(ActionEvent event){
        Honor h = (Honor) CommonMethod.getRowValue(event,2,tableView);
        try{
            FXMLLoader loader = new FXMLLoader(MainApplication.class.getResource("honor-editor.fxml"));
            WindowsManager.init();
            WindowsManager.getInstance().openNewWindow(
                    loader, 400, 600, "编辑荣誉信息信息",
                    tableView.getScene().getWindow(), Modality.WINDOW_MODAL,
                    new WindowOpenAction() {
                        @Override
                        public void init(Object controller) {
                            WindowOpenAction.super.init(controller);
                            HonorEditorController heCont = (HonorEditorController) controller;
                            heCont.init(h,HonorPanelController.this);
                        }
                    }
            );
        }
        catch (IOException e){
            e.printStackTrace();
            MessageDialog.showDialog("打开编辑窗口失败!");
        }
    }

    public void onDelete(ActionEvent event){
        Honor h = (Honor) CommonMethod.getRowValue(event,2,tableView);
        int ret = MessageDialog.choiceDialog("你确定要删除该荣誉信息吗?");
        if(ret != MessageDialog.CHOICE_YES){
            return;
        }
        DataRequest req = new DataRequest();
        req.add("honorId",h.getHonorId());
        DataResponse res = HttpRequestUtil.request("/api/honor/deleteHonor",req);
        assert res != null;
        if(res.getCode() == 0){
            MessageDialog.showDialog("删除成功");
            onQueryButtonClick();
        }
        else{
            MessageDialog.showDialog(res.getMsg());
        }

    }

    public void onNew(){
        try{
            FXMLLoader loader = new FXMLLoader(MainApplication.class.getResource("honor-editor.fxml"));

            WindowsManager.init();
            WindowsManager.getInstance().openNewWindow(
                    loader, 400, 600, "编辑荣誉信息信息",
                    tableView.getScene().getWindow(), Modality.WINDOW_MODAL,
                    new WindowOpenAction() {
                        @Override
                        public void init(Object controller) {
                            WindowOpenAction.super.init(controller);
                            HonorEditorController heCont = (HonorEditorController) controller;
                            heCont.init(null,HonorPanelController.this);
                        }
                    }
            );
        }
        catch (IOException e){
            e.printStackTrace();
            MessageDialog.showDialog("打开编辑窗口失败!");
        }
    }



    @Override
    public void hasSaved() {
        onQueryButtonClick();
    }
}
