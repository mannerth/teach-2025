package com.teach.javafx.models;

/**
 * *@Author：Cui
 * *@Date：2025/6/4  1:23
 */

public class Teacher {
    private Integer personId;
    private String num;
    private String name;
    private String type;
    private String dept;
    private String card;
    private String gender;
    private String genderName;
    private String birthday;
    private String email;
    private String phone;
    private String address;
    private String introduce;
    private String title; // 新增职称字段

    public Teacher(){}

    public Teacher(String num, String name){
        this.num = num;
        this.name = name;
    }

    // Getters and Setters
    public Integer getPersonId() { return personId; }
    public void setPersonId(Integer personId) { this.personId = personId; }
    public String getNum() { return num; }
    public void setNum(String num) { this.num = num; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getDept() { return dept; }
    public void setDept(String dept) { this.dept = dept; }
    public String getCard() { return card; }
    public void setCard(String card) { this.card = card; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    public String getGenderName() { return genderName; }
    public void setGenderName(String genderName) { this.genderName = genderName; }
    public String getBirthday() { return birthday; }
    public void setBirthday(String birthday) { this.birthday = birthday; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getIntroduce() { return introduce; }
    public void setIntroduce(String introduce) { this.introduce = introduce; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String toString(){
        return num + "-" + name;
    }
}