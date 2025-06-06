package com.teach.javafx.controller.base;

import com.teach.javafx.models.Honor;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;
import com.teach.javafx.util.CommonMethod;

import java.util.Objects;

public class HonorValueFactory implements Callback<TableColumn.CellDataFeatures<Honor, String>, ObservableValue<String>> {
    @Override
    public ObservableValue<String> call(TableColumn.CellDataFeatures<Honor, String> param) {
        Honor h = param.getValue();
        String id = param.getTableColumn().getId();
        if(h == null){
            return new SimpleStringProperty(Objects.requireNonNullElse("----", "----"));
        }
        return switch (id) {
            case "studentName" ->
                    new SimpleStringProperty(h.getStudentName() == null ? "----" : h.getStudentName());
            case "studentNum" -> new SimpleStringProperty(h.getStudentNum() == null ? "----" : h.getStudentNum());
            case "honorType" -> new SimpleStringProperty(h.getHonorType() == null ? "----" : Honor.getTypeName(h.getHonorType()));
            case "honorContent" -> new SimpleStringProperty(h.getHonorContent() == null ? "----" : h.getHonorContent());
            default -> new SimpleStringProperty("----");
        };
    }
}
