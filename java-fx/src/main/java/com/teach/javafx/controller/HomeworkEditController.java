package com.teach.javafx.controller;

import com.teach.javafx.controller.base.MessageDialog;
import com.teach.javafx.request.*;
import com.teach.javafx.util.CommonMethod;
import com.teach.javafx.util.DateTimeTool;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeworkEditController {
    @FXML
    private ComboBox<OptionItem> courseComboBox;
    private List<OptionItem> courseList;

    @FXML
    private ComboBox<OptionItem> courseExComboBox;
    private List<OptionItem> courseExList;
    @FXML
    private TextField contentField;
    @FXML
    private DatePicker deadlineDatePicker;
    @FXML
    private Spinner<Integer> deadlineHourSpinner;
    @FXML
    private Spinner<Integer> deadlineMinuteSpinner;

    private HomeworkTableController homeworkTableController = null;
    private Integer homeworkId = null;

    @FXML
    public void initialize() {
        // 初始化小时选择器
        SpinnerValueFactory<Integer> hourValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, 0);
        deadlineHourSpinner.setValueFactory(hourValueFactory);

        // 初始化分钟选择器
        SpinnerValueFactory<Integer> minuteValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, 0);
        deadlineMinuteSpinner.setValueFactory(minuteValueFactory);
    }

    //这里在布置作业时将所有作业信息形成一张Map传到HomeworkTableController那里
    @FXML
    public void okButtonClick() {
        Map<String, Object> data = new HashMap<>();
        OptionItem op;

        op = courseComboBox.getSelectionModel().getSelectedItem();
        if (op != null) {
            data.put("courseId", Integer.parseInt(op.getValue()));
        }

        op = courseExComboBox.getSelectionModel().getSelectedItem();
        if (op != null) {
            data.put("courseExId", Integer.parseInt(op.getValue()));
        }
        data.put("homeworkId", homeworkId);

        // 获取当前日期时间并格式化
        String releasingTimeStr = DateTimeTool.getCurrentDate();
        LocalDateTime releasingTime = LocalDateTime.parse(releasingTimeStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        System.out.println("Homework Releasing Time: " + releasingTime);
        System.out.println("Homework Releasing Time: " + releasingTimeStr);
        data.put("homeworkReleasingTime", releasingTimeStr);

        // 获取截止日期选择器的值
        LocalDate deadline = deadlineDatePicker.getValue();
        int deadlineHour = deadlineHourSpinner.getValue();
        int deadlineMinute = deadlineMinuteSpinner.getValue();

        if (deadline != null) {
            // 将 LocalDate 转换为 LocalDateTime 并格式化为 ISO 格式
            LocalDateTime deadlineDateTime = LocalDateTime.of(deadline, LocalTime.of(deadlineHour, deadlineMinute));
            data.put("homeworkDeadLineLocalDateTime", deadlineDateTime);
            String deadlineStr = deadlineDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            System.out.println("Homework Deadline: " + deadlineStr);
            data.put("homeworkDeadline", deadlineStr);
        } else {
            System.out.println("No deadline selected.");
        }

        data.put("content", contentField.getText());
        System.out.println(data);
        homeworkTableController.doClose("ok", data);
    }

    @FXML
    public void cancelButtonClick() {
        homeworkTableController.doClose("cancel", null);
    }

    public void setHomeworkTableController(HomeworkTableController homeworkTableController) {
        this.homeworkTableController = homeworkTableController;
    }

    public void init() {
        // 初始化课程列表
        courseList = homeworkTableController.getCourseList();
        courseComboBox.getItems().addAll(courseList);


        // 添加课程选择的监听器
        courseComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.equals(oldValue)) {
                Integer courseId = Integer.parseInt(newValue.getValue());
                loadCourseExList(courseId);
                System.out.println("NB");
            }
        });
    }

    public void showDialog(Map data) {
        if (data == null) {
            homeworkId = null;

            courseComboBox.getSelectionModel().select(-1);

            courseComboBox.setDisable(false);

            contentField.setText("");

            courseExComboBox.getSelectionModel().select(-1);

            courseExComboBox.setDisable(false);

            deadlineDatePicker.setValue(null);

            deadlineHourSpinner.getValueFactory().setValue(0); // 默认小时为0
            deadlineMinuteSpinner.getValueFactory().setValue(0); // 默认分钟为0
        } else {
//            courseExComboBox.getItems().clear();
            homeworkId = CommonMethod.getInteger(data, "homeworkId");

            // 选择课程
            String courseIdStr = CommonMethod.getString(data, "courseId");
            if (courseList != null) {
                courseComboBox.getSelectionModel().select(CommonMethod.getOptionItemIndexByValue(courseList, courseIdStr));
            } else {
                System.out.println("courseList is null");
            }
            courseComboBox.setDisable(true);

            courseExComboBox.setDisable(false);

            // 设置内容
            contentField.setText(CommonMethod.getString(data, "content"));

            String releasingTimeStr = DateTimeTool.getCurrentDate();
            System.out.println("Homework Releasing Time: " + releasingTimeStr);
            data.put("homeworkReleasingTime", releasingTimeStr);

            // 初始化截止日期
            Object deadlineObj = data.get("homeworkDeadline");
            if (deadlineObj instanceof LocalDate) {
                String deadlineStr = (String) deadlineObj;
                try {
                    LocalDateTime deadlineDateTime = LocalDateTime.parse(deadlineStr, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                    System.out.println("Deadline: " + deadlineDateTime);
                    deadlineDatePicker.setValue(deadlineDateTime.toLocalDate());

                    // 设置小时和分钟
                    int hour = deadlineDateTime.getHour();
                    int minute = deadlineDateTime.getMinute();
                    deadlineHourSpinner.getValueFactory().setValue(hour);
                    deadlineMinuteSpinner.getValueFactory().setValue(minute);
                } catch (DateTimeParseException e) {
                    System.out.println("Failed to parse deadline string: " + deadlineStr);
                }
            }else {
                    System.out.println("Deadline is not a LocalDate or is null");
            }
        }
    }

    public void loadCourseExList(Integer courseId) {
        // 清空现有的课序号列表
        courseExComboBox.getItems().clear();

        // 构造请求参数
        DataRequest req = new DataRequest();
        req.add("courseId", courseId);

        // 在后台线程中发送请求
        new Thread(() -> {
            DataResponse res = HttpRequestUtil.request("/api/CourseEx/getOptionItemList", req);
            List<OptionItem> list = HttpRequestUtil.requestOptionItemList("/api/CourseEx/getOptionItemList", req);

            Platform.runLater(() -> {
                if (res != null) {
                    System.out.println("Response code: " + res.getCode());
                    if (res.getCode() == 0) {
                        try {
                            //List<Map<String, Object>> dataList = (List<Map<String, Object>>) res.getData();
                            List<OptionItem> courseExList = new ArrayList<>();
                            for (var i : list) {
                                courseExList.add(i);
                            }
                            // 将 courseExList 添加到 courseExComboBox 的 items 中
                            courseExComboBox.getItems().addAll(courseExList);
                        } catch (ClassCastException e) {
                            System.err.println("Invalid response data type: " + res.getData().getClass().getName());
                        }
                    } else {
                        System.out.println("Failed to get courseExList. Response code: " + res.getCode());
                    }
                } else {
                    System.out.println("Failed to get courseExList. Response code: null");
                }
            });
        }).start();
    }



}
