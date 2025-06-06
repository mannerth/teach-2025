package cn.edu.sdu.java.server.models;

public enum EHonorType {
    //荣誉称号
    HONOR_TITLE,
    //学科竞赛
    HONOR_CONTEST,
    //社会实践
    HONOR_PRACTICE,
    //科技成果
    HONOR_TECH,
    //培训讲座
    HONOR_LECTURE,
    //校外实习
    HONOR_INTERNSHIP,
    //创新项目
    HONOR_PROJ;

    public static EHonorType fromString(String str){
        return switch (str) {
            case "HONOR_TITLE" -> HONOR_TITLE;
            case "HONOR_CONTEST" -> HONOR_CONTEST;
            case "HONOR_PRACTICE" -> HONOR_PRACTICE;
            case "HONOR_TECH" -> HONOR_TECH;
            case "HONOR_LECTURE" -> HONOR_LECTURE;
            case "HONOR_INTERNSHIP" -> HONOR_INTERNSHIP;
            case "HONOR_PROJ" -> HONOR_PROJ;
            default -> null;
        };
    }
}
