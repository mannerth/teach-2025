package com.teach.javafx.controller;

import com.teach.javafx.models.StudentRegister;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class StudentRegisterController {

    @FXML
    private TableView<StudentRegister> studentRegisterTableView;
    @FXML
    private TableColumn<StudentRegister, Number> personIdColumn;
    @FXML
    private TableColumn<StudentRegister, String> studentNameColumn;
    @FXML
    private TableColumn<StudentRegister, String> majorColumn;
    @FXML
    private TableColumn<StudentRegister, String> classNameColumn;
    @FXML
    private TableColumn<StudentRegister, String> subjectColumn;
    @FXML
    private TableColumn<StudentRegister, String> registrationLocationColumn;
    @FXML
    private TableColumn<StudentRegister, String> registrationTimeColumn;

    @FXML
    private TextField searchField;
    @FXML
    private TextField personIdField;
    @FXML
    private TextField studentNameField;
    @FXML
    private TextField majorField;
    @FXML
    private TextField classNameField;
    @FXML
    private TextField subjectField;
    @FXML
    private TextField registrationLocationField;
    @FXML
    private TextField registrationTimeField;

    @FXML
    private Button photoButton; // 添加@FXML注解

    private final ObservableList<StudentRegister> students = FXCollections.observableArrayList();
    private final AtomicInteger nextPersonId = new AtomicInteger(1000);
    private StudentRegister selectedStudent = null;
    private boolean isEditMode = false;

    @FXML
    public void initialize() {
        // 初始化表格列映射
        personIdColumn.setCellValueFactory(new PropertyValueFactory<>("personId"));
        studentNameColumn.setCellValueFactory(new PropertyValueFactory<>("studentName"));
        majorColumn.setCellValueFactory(new PropertyValueFactory<>("major"));
        classNameColumn.setCellValueFactory(new PropertyValueFactory<>("className"));
        subjectColumn.setCellValueFactory(new PropertyValueFactory<>("subject"));
        registrationLocationColumn.setCellValueFactory(new PropertyValueFactory<>("registrationLocation"));
        registrationTimeColumn.setCellValueFactory(new PropertyValueFactory<>("registrationTime"));

        // 设置表格选择事件
        studentRegisterTableView.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> showStudentDetails(newValue));

        // 加载初始数据
        initializeTestData();
    }

    private void initializeTestData() {
        students.add(new StudentRegister(nextPersonId.getAndIncrement(), "张三", "计算机科学", "计科1班", "Java编程", "教学楼101", "2025-09-01"));
        students.add(new StudentRegister(nextPersonId.getAndIncrement(), "李四", "电子工程", "电信2班", "电路分析", "实验楼302", "2025-09-02"));
        students.add(new StudentRegister(nextPersonId.getAndIncrement(), "王五", "机械工程", "机械1班", "机械设计", "实训楼205", "2025-09-03"));

        studentRegisterTableView.setItems(students);
    }

    private void showStudentDetails(StudentRegister student) {
        if (student != null) {
            selectedStudent = student;
            personIdField.setText(String.valueOf(student.getPersonId()));
            studentNameField.setText(student.getStudentName());
            majorField.setText(student.getMajor());
            classNameField.setText(student.getClassName());
            subjectField.setText(student.getSubject());
            registrationLocationField.setText(student.getRegistrationLocation());
            registrationTimeField.setText(student.getRegistrationTime());
            isEditMode = true;
        } else {
            clearForm();
            isEditMode = false;
        }
    }

    private void clearForm() {
        personIdField.clear();
        studentNameField.clear();
        majorField.clear();
        classNameField.clear();
        subjectField.clear();
        registrationLocationField.clear();
        registrationTimeField.clear();
        selectedStudent = null;
        isEditMode = false;
    }

    @FXML
    private void handleAddButtonClick() {
        clearForm();
        isEditMode = false;
    }

    @FXML
    private void handleDeleteButtonClick() {
        if (selectedStudent == null) {
            showAlert("操作提示", "请先选择要删除的学生记录", Alert.AlertType.WARNING);
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("确认删除");
        alert.setHeaderText("删除学生记录");
        alert.setContentText("确定要删除该学生记录吗？");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            students.remove(selectedStudent);
            clearForm();
            showAlert("操作成功", "学生记录已成功删除", Alert.AlertType.INFORMATION);
        }
    }

    @FXML
    private void handleImportButtonClick() {
        // 这里采用模拟数据的方式替代从Excel导入
        List<StudentRegister> mockData = new ArrayList<>();
        mockData.add(new StudentRegister(nextPersonId.getAndIncrement(), "赵六", "数学", "数学1班", "高等数学", "教学楼201", "2025-09-05"));
        mockData.add(new StudentRegister(nextPersonId.getAndIncrement(), "孙七", "英语", "英语2班", "英语口语", "教学楼303", "2025-09-06"));

        students.addAll(mockData);
        showAlert("导入成功", "学生数据已成功导入", Alert.AlertType.INFORMATION);
    }

    @FXML
    private void handleQueryButtonClick() {
        String keyword = searchField.getText().trim();
        if (keyword.isEmpty()) {
            studentRegisterTableView.setItems(students);
        } else {
            List<StudentRegister> filtered = students.stream()
                    .filter(s -> s.getStudentName().contains(keyword) ||
                            s.getMajor().contains(keyword) ||
                            s.getClassName().contains(keyword))
                    .collect(Collectors.toList());
            studentRegisterTableView.setItems(FXCollections.observableArrayList(filtered));
        }
    }

    @FXML
    private void handleSaveButtonClick() {
        if (!validateForm()) {
            return;
        }

        if (isEditMode) {
            // 更新现有学生
            selectedStudent.setStudentName(studentNameField.getText());
            selectedStudent.setMajor(majorField.getText());
            selectedStudent.setClassName(classNameField.getText());
            selectedStudent.setSubject(subjectField.getText());
            selectedStudent.setRegistrationLocation(registrationLocationField.getText());
            selectedStudent.setRegistrationTime(registrationTimeField.getText());

            studentRegisterTableView.refresh();
            showAlert("操作成功", "学生信息已更新", Alert.AlertType.INFORMATION);
        } else {
            // 添加新学生
            StudentRegister newStudent = new StudentRegister(
                    nextPersonId.getAndIncrement(),
                    studentNameField.getText(),
                    majorField.getText(),
                    classNameField.getText(),
                    subjectField.getText(),
                    registrationLocationField.getText(),
                    registrationTimeField.getText()
            );

            students.add(newStudent);
            showAlert("操作成功", "学生信息已添加", Alert.AlertType.INFORMATION);
        }

        clearForm();
    }

    @FXML
    private void handleCancelButtonClick() {
        clearForm();
    }

    private boolean validateForm() {
        if (studentNameField.getText().trim().isEmpty()) {
            showAlert("验证错误", "学生姓名不能为空", Alert.AlertType.ERROR);
            return false;
        }

        if (majorField.getText().trim().isEmpty()) {
            showAlert("验证错误", "专业不能为空", Alert.AlertType.ERROR);
            return false;
        }

        return true;
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // 照片按钮事件处理（空实现，可根据需求扩展）
    @FXML
    private void onPhotoButtonClick(ActionEvent actionEvent) {
        if (selectedStudent != null) {
            showAlert("照片功能", "查看 " + selectedStudent.getStudentName() + " 的照片", Alert.AlertType.INFORMATION);
            // 这里可以添加实际的照片查看逻辑
        } else {
            showAlert("操作提示", "请先选择一名学生", Alert.AlertType.WARNING);
        }
    }
}