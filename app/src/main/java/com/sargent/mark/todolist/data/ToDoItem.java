package com.sargent.mark.todolist.data;

/**
 * Created by mark on 7/4/17.
 */

public class ToDoItem {
    //added the category and task in pojo file and generated the getters and setters
    private String categories;
    private String task;
    private String description;
    private String dueDate;

    public ToDoItem(String description, String dueDate, String categories, String task) {
        this.description = description;
        this.dueDate = dueDate;
        this.categories = categories;
        this.task = task;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public String getCategories() {
        return categories;
    }

    public void setCategories(String categories) {
        this.categories = categories;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String status) {
        this.task = task;
    }
}
