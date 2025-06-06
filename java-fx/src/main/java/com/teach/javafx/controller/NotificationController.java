package com.teach.javafx.controller;

import com.teach.javafx.AppStore;
import com.teach.javafx.controller.base.MessageDialog;
import com.teach.javafx.controller.base.ToolController;
import com.teach.javafx.request.DataRequest;
import com.teach.javafx.request.DataResponse;
import com.teach.javafx.request.HttpRequestUtil;
import com.teach.javafx.util.CommonMethod;
import com.teach.javafx.util.DateTimeTool;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.MapValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class NotificationController extends ToolController {
    private ImageView photoImageView;


    @FXML
    private HBox hBox;

    @FXML
    private FlowPane flowPane;

    @FXML
    private SplitPane splitPane;
    @FXML
    private VBox vBox;


    @FXML
    private TableView<Map> dataTableView;  //通知信息表
    @FXML
    private TableColumn<Map, String> numColumn;   //通知信息表 编号列
    @FXML
    private TableColumn<Map, Date> dateColumn; //通知信息表 发布日期列
    @FXML
    private TableColumn<Map, String> titleColumn;  //通知信息表 主题列

    @FXML
    private TextField numField; //通知信息  编号输入域

    @FXML
    private TextField titleField; //通知信息  主题输入域



    @FXML
    private TextField numNameTextField;  //查询 编号主题输入域

    private Integer notificationId = null;  //当前编辑修改的通知的主键

    private ArrayList<Map> notificationList = new ArrayList();  // 通知信息列表数据
    private ObservableList<Map> observableList = FXCollections.observableArrayList();  // TableView渲染列表


    /**
     * 将通知消息集合设置到面板上显示
     */
    private void setTableViewData() {
        observableList.clear();
        for (int j = 0; j < notificationList.size(); j++) {
            observableList.addAll(FXCollections.observableArrayList(notificationList.get(j)));
        }
        dataTableView.setItems(observableList);
    }

    /**
     * 页面加载对象创建完成初始化方法，页面中控件属性的设置，初始数据显示等初始操作都在这里完成，其他代码都事件处理方法里
     */

    @FXML
    public void initialize() {
        String roleName = AppStore.getJwt().getRole();
        photoImageView = new ImageView();
        photoImageView.setFitHeight(100);
        photoImageView.setFitWidth(100);
        DataResponse res;
        DataRequest req = new DataRequest();
        req.add("numName", "");
        res = HttpRequestUtil.request("/api/notification/getNotificationList", req); //从后台获取所有通知信息列表集合
        System.out.println(res.getData());
        if (res != null && res.getCode() == 0) {
            notificationList = (ArrayList<Map>) res.getData();
        }
        numColumn.setCellValueFactory(new MapValueFactory<>("num"));  //设置列值工程属性
        dateColumn.setCellValueFactory(new MapValueFactory<>("releaseTime"));
        titleColumn.setCellValueFactory(new MapValueFactory<>("title"));
        if(roleName.equals("ROLE_STUDENT") || roleName.equals("ROLE_TEACHER")) {
            hBox.getChildren().remove(flowPane);
            splitPane.getItems().remove(vBox);
        }

        TableView.TableViewSelectionModel<Map> tsm = dataTableView.getSelectionModel();
        ObservableList<Integer> list = tsm.getSelectedIndices();
        list.addListener(this::onTableRowSelect);
        setTableViewData();
    }


    /**
     * 清除通知表单中输入信息
     */
    public void clearPanel() {
        notificationId = null;
        numField.setText("");
        titleField.setText("");
    }

    protected void changeNotificationInfo() {
        Map<String,Object> form = dataTableView.getSelectionModel().getSelectedItem();
        if (form == null) {
            clearPanel();
            return;
        }
        notificationId = CommonMethod.getInteger(form, "notificationId");
        DataRequest req = new DataRequest();
        req.add("notificationId", notificationId);
        DataResponse res = HttpRequestUtil.request("/api/notification/getNotificationInfo", req);
        if (res.getCode() != 0) {
            MessageDialog.showDialog(res.getMsg());
            return;
        }
        form = (Map) res.getData();
        numField.setText(CommonMethod.getString(form, "num"));
        titleField.setText(CommonMethod.getString(form, "title"));
    }

    /**
     * 点击通知列表的某一行，根据notificationId ,从后台查询通知的基本信息，切换通知的编辑信息
     */

    public void onTableRowSelect(ListChangeListener.Change<? extends Integer> change) {
        changeNotificationInfo();
    }

    /**
     * 点击查询按钮，从从后台根据输入的串，查询匹配的通知在通知列表中显示
     */
    @FXML
    protected void onQueryButtonClick() {
        String numName = numNameTextField.getText();
        DataRequest req = new DataRequest();
        req.add("numName", numName);
        DataResponse res = HttpRequestUtil.request("/api/notification/getNotificationList", req);
        if (res != null && res.getCode() == 0) {
            notificationList = (ArrayList<Map>) res.getData();
            setTableViewData();
        }
    }


    /**
     * 添加新通知， 清空输入信息， 输入相关信息，点击保存即可添加新的通知
     */
    @FXML
    protected void onAddButtonClick() {
        clearPanel();
    }

    /**
     * 点击删除按钮 删除当前编辑的通知的数据
     */
    @FXML
    protected void onDeleteButtonClick() {
        Map form = dataTableView.getSelectionModel().getSelectedItem();
        if (form == null) {
            MessageDialog.showDialog("没有选择，不能删除");
            return;
        }
        int ret = MessageDialog.choiceDialog("确认要删除吗?");
        if (ret != MessageDialog.CHOICE_YES) {
            return;
        }
        notificationId = CommonMethod.getInteger(form, "notificationId");
        DataRequest req = new DataRequest();
        System.out.println("选中的表单数据: " + form);
        System.out.println(notificationId);
        req.add("notificationId", notificationId);

        DataResponse res = HttpRequestUtil.request("/api/notification/notificationDelete", req);
        if(res!= null) {
            if (res.getCode() == 0) {
                MessageDialog.showDialog("删除成功！");
                onQueryButtonClick();
            } else {
                MessageDialog.showDialog(res.getMsg());
            }
        }
    }

    /**
     * 点击保存按钮，保存当前编辑的通知信息，如果是新添加的通知，后台添加通知
     */
    @FXML
    protected void onSaveButtonClick() {
        if (numField.getText().isEmpty()) {
            MessageDialog.showDialog("编号为空，不能修改");
            return;
        }else if(numField.getText().length() >= 10) {
            MessageDialog.showDialog("编号超出长度限制，请输入9位及以下编号！");
            return;
        }
        Map<String,Object> form = new HashMap<>();
        form.put("num", numField.getText());
        form.put("title", titleField.getText());
        String a = DateTimeTool.getCurrentDate();
        System.out.println(a);
        form.put("releaseTime", a);

        DataRequest req = new DataRequest();
        req.add("notificationId", notificationId);
        req.add("form", form);
        DataResponse res = HttpRequestUtil.request("/api/notification/notificationEditSave", req);
        if (res.getCode() == 0) {
            notificationId = CommonMethod.getIntegerFromObject(res.getData());
            MessageDialog.showDialog("提交成功！");
            onQueryButtonClick();
            clearPanel();
        } else {
            MessageDialog.showDialog(res.getMsg());
        }
    }

    /**
     * doNew() doSave() doDelete() 重写 ToolController 中的方法， 实现选择 新建，保存，删除 对通知的增，删，改操作
     */
    public void doNew() {
        clearPanel();
    }

    public void doSave() {
        onSaveButtonClick();
    }

    public void doDelete() {
        onDeleteButtonClick();
    }
}
