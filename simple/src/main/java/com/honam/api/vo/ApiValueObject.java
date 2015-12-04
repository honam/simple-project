package com.honam.api.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xuhaonan on 15/11/27.
 */
public class ApiValueObject implements Serializable {

    private String description ;
    private String className ;
    private List<ApiField> apiFields=new ArrayList<ApiField>();

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public List<ApiField> getApiFields() {
        return apiFields;
    }

    public void setApiFields(List<ApiField> apiFields) {
        this.apiFields = apiFields;
    }
}
