package com.honam.api.vo;

import java.io.Serializable;

/**
 * Created by xuhaonan on 15/11/27.
 */
public class ApiField implements Serializable{
    private String name;
    private String className ;
    private String description ;
    private Boolean isValueObject=false ;
    private Boolean isCollection=false ;
    private String CollectionClass="" ;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getIsValueObject() {
        return isValueObject;
    }

    public void setIsValueObject(Boolean isValueObject) {
        this.isValueObject = isValueObject;
    }

    public Boolean getIsCollection() {
        return isCollection;
    }

    public void setIsCollection(Boolean isCollection) {
        this.isCollection = isCollection;
    }

    public String getCollectionClass() {
        return CollectionClass;
    }

    public void setCollectionClass(String collectionClass) {
        CollectionClass = collectionClass;
    }
}
