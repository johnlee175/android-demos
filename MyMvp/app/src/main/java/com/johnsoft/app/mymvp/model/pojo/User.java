package com.johnsoft.app.mymvp.model.pojo;

/**
 * @author John Kenrinus Lee
 * @version 2016-12-21
 */

public class User {
    private final String id;
    private final String name;
    private final String image;
    private final String age;
    private final String grade;
    private final String description;

    public User(String id, String name, String image, String age, String grade, String description) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.age = age;
        this.grade = grade;
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }

    public String getAge() {
        return age;
    }

    public String getGrade() {
        return grade;
    }

    public String getDescription() {
        return description;
    }
}
