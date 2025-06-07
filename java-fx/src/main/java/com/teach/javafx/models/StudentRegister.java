package com.teach.javafx.models;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class StudentRegister {
    private final IntegerProperty personId = new SimpleIntegerProperty();
    private final StringProperty studentName = new SimpleStringProperty();
    private final StringProperty major = new SimpleStringProperty();
    private final StringProperty className = new SimpleStringProperty();
    private final StringProperty subject = new SimpleStringProperty();
    private final StringProperty registrationLocation = new SimpleStringProperty();
    private final StringProperty registrationTime = new SimpleStringProperty();

    // 构造函数
    public StudentRegister() {}

    public StudentRegister(int personId, String studentName, String major,
                           String className, String subject,
                           String registrationLocation, String registrationTime) {
        this.personId.set(personId);
        this.studentName.set(studentName);
        this.major.set(major);
        this.className.set(className);
        this.subject.set(subject);
        this.registrationLocation.set(registrationLocation);
        this.registrationTime.set(registrationTime);
    }

    // Getters and Setters
    public Integer getPersonId() {
        return personId.get();
    }

    public IntegerProperty personIdProperty() {
        return personId;
    }

    public void setPersonId(int personId) {
        this.personId.set(personId);
    }

    public String getStudentName() {
        return studentName.get();
    }

    public StringProperty studentNameProperty() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName.set(studentName);
    }

    public String getMajor() {
        return major.get();
    }

    public StringProperty majorProperty() {
        return major;
    }

    public void setMajor(String major) {
        this.major.set(major);
    }

    public String getClassName() {
        return className.get();
    }

    public StringProperty classNameProperty() {
        return className;
    }

    public void setClassName(String className) {
        this.className.set(className);
    }

    public String getSubject() {
        return subject.get();
    }

    public StringProperty subjectProperty() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject.set(subject);
    }

    public String getRegistrationLocation() {
        return registrationLocation.get();
    }

    public StringProperty registrationLocationProperty() {
        return registrationLocation;
    }

    public void setRegistrationLocation(String registrationLocation) {
        this.registrationLocation.set(registrationLocation);
    }

    public String getRegistrationTime() {
        return registrationTime.get();
    }

    public StringProperty registrationTimeProperty() {
        return registrationTime;
    }

    public void setRegistrationTime(String registrationTime) {
        this.registrationTime.set(registrationTime);
    }
}