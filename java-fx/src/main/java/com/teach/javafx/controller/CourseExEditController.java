package com.teach.javafx.controller;

import com.teach.javafx.controller.base.MessageDialog;
import com.teach.javafx.request.OptionItem;
import com.teach.javafx.util.CommonMethod;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CourseExEditController {
    @FXML
    private ComboBox<OptionItem> teacherComboBox;
    @FXML
    private ComboBox<OptionItem> dayComboBox;
    @FXML
    private ComboBox<OptionItem> classComboBox;
    @FXML
    private ComboBox<OptionItem> stateComboBox;
    @FXML
    private ComboBox<OptionItem> courseComboBox;
    @FXML
    private ComboBox<OptionItem> beginWeekComboBox;
    @FXML
    private ComboBox<OptionItem> endWeekComboBox;
    @FXML
    private TextField course_numText;
    @FXML
    private TextField informationText;
    @FXML
    private TextField placeText;
    @FXML
    private TextField max_numText;

    private Integer courseExId = null;
    boolean hasInit = false;
    private final OptionItem[] c = {new OptionItem(1,"1","第一节"),new OptionItem(2,"2","第二节"),new OptionItem(3,"3","第三节"),new OptionItem(4,"4","第四节"),new OptionItem(5,"5","第五节")};
    private final List<OptionItem> dayO = new ArrayList<>();
    private final List<OptionItem> classOpt = new ArrayList<>(List.of(c));
    private final OptionItem[] s = {new OptionItem(1,"","可选"),new OptionItem(0,"","不可选")};
    private final List<OptionItem> state = new ArrayList<>(List.of(s));
    private CourseExController courseExController;
    private List<OptionItem> teacherOptList;
    private List<OptionItem> courseOptList;
    private List<OptionItem> WeekOptList = new ArrayList<>();
    public void setScoreTableController(CourseExController courseExController) {
        this.courseExController = courseExController;
    }

    public void init(){
        if(hasInit){
            return;
        }
        hasInit = true;
        for(int i = 1; i <= 20; ++i){
            WeekOptList.add(new OptionItem(i,i+"","第"+i+"周"));
        }
        beginWeekComboBox.getItems().clear();
        endWeekComboBox.getItems().clear();
        beginWeekComboBox.getItems().addAll(WeekOptList);
        endWeekComboBox.getItems().addAll(WeekOptList);
        dayComboBox.getItems().clear();
        dayO.add(new OptionItem(1,"1","周一"));
        dayO.add(new OptionItem(2,"2","周二"));
        dayO.add(new OptionItem(3,"3","周三"));
        dayO.add(new OptionItem(4,"4","周四"));
        dayO.add(new OptionItem(5,"5","周五"));
        dayO.add(new OptionItem(6,"6","周六"));
        dayO.add(new OptionItem(7,"7","周日"));
        dayComboBox.getItems().addAll(dayO);
        dayComboBox.setVisibleRowCount(8);
        classComboBox.getItems().clear();
        classComboBox.getItems().addAll(classOpt);
        stateComboBox.getItems().clear();
        stateComboBox.getItems().addAll(state);
        stateComboBox.getSelectionModel().select(0);
        courseOptList = courseExController.getCourseList();
        courseComboBox.getItems().clear();
        courseComboBox.getItems().addAll(courseOptList);
        teacherOptList = courseExController.getTeacherList();
        teacherComboBox.getItems().clear();
        teacherComboBox.getItems().addAll(teacherOptList);
    }

    public void dispose(){
        dayComboBox.getSelectionModel().clearSelection();
        classComboBox.getSelectionModel().clearSelection();
        teacherComboBox.getSelectionModel().clearSelection();
        courseComboBox.getSelectionModel().clearSelection();
        stateComboBox.getSelectionModel().clearSelection();
        beginWeekComboBox.getSelectionModel().clearSelection();
        endWeekComboBox.getSelectionModel().clearSelection();
        informationText.clear();
        max_numText.clear();
        placeText.clear();
        course_numText.clear();
        courseExId = null;
    }

    @FXML
    public void okButtonClick(){
        String course_num = course_numText.getText();
        String maxN = max_numText.getText();
        String place = placeText.getText();
        if(stateComboBox.getSelectionModel().isEmpty()||course_num.isEmpty()||maxN.isEmpty()||place.isEmpty()||dayComboBox.getSelectionModel().isEmpty()||classComboBox.getSelectionModel().isEmpty()||teacherComboBox.getSelectionModel().isEmpty()){
            MessageDialog.showDialog("信息不完整，不能添加");
            return;
        }
        Map<String, Object> map = new HashMap<>();
        map.put("courseId", courseComboBox.getValue().getId());
        map.put("teacherNum", teacherComboBox.getValue().getValue());
        map.put("course_num", course_numText.getText());
        map.put("place", placeText.getText());
        map.put("max_stu_num", max_numText.getText());
        map.put("information", informationText.getText());
        map.put("state", stateComboBox.getValue().getId());
        String timeInf = dayComboBox.getValue().getId().toString()+classComboBox.getValue().getId().toString();
        if(!beginWeekComboBox.getSelectionModel().isEmpty()&&!endWeekComboBox.getSelectionModel().isEmpty()){
            timeInf+=","+beginWeekComboBox.getValue().getId()+"-"+endWeekComboBox.getValue().getId();
        }
        map.put("time", timeInf);
        if(courseExId!=null){
            map.put("courseExId",courseExId);
        }
        courseExController.doClose("ok",map);
        dispose();
    }

    @FXML
    public void cancelButtonClick(){
        courseExController.doClose("q",null);
    }

    public void showDialog(Map data) {
        if(data == null){
            courseExId = null;
            courseComboBox.setDisable(false);
        }else{
            courseExId = CommonMethod.getInteger(data,"courseExId");
            Integer cid = CommonMethod.getInteger(data, "courseId");
            courseComboBox.getSelectionModel().select(CommonMethod.getOptionItemIndexByValue(courseOptList,cid.toString()));
            courseComboBox.setDisable(true);
            teacherComboBox.getSelectionModel().select(CommonMethod.getOptionItemIndexByValue(teacherOptList,CommonMethod.getString(data,"teacher_num")));
            stateComboBox.getSelectionModel().select(CommonMethod.getString(data,"is_choosable").equals("可选")?0:1);
            String s = CommonMethod.getString(data, "time0");
            int x = Integer.parseInt(s.charAt(0)+"");
            int y = Integer.parseInt(s.charAt(1)+"");
            dayComboBox.getSelectionModel().select(x-1);
            classComboBox.getSelectionModel().select(y-1);
            informationText.setText(CommonMethod.getString(data,"information"));
            course_numText.setText(CommonMethod.getString(data,"course_num"));
            max_numText.setText(CommonMethod.getString(data,"max_stu_num"));
            placeText.setText(CommonMethod.getString(data,"place"));
        }
    }
/*课程          courseId
    *教师          teacherNum
    *课序号        course_num
    *上课地点       place
    *上课时间       time
    *最大人数       max_stu_num
    *(课程信息)     information   该字段可拓展
    * */

}
