package top.itfinally.mybatis.generator.core.job;

import top.itfinally.mybatis.generator.core.database.entity.TableEntity;

/**
 * <pre>
 * *********************************************
 * All rights reserved.
 * Description: ${类文件描述}
 * *********************************************
 *  Version       Date          Author        Desc ( 一句话描述修改 )
 *  v1.0          2018/7/31       itfinally       首次创建
 * *********************************************
 * </pre>
 */
public class JobGroup {
    private JobUnit entity;
    private JobUnit services;
    private JobUnit mapperXml;
    private JobUnit repository;
    private JobUnit controller;
    private JobUnit servicesInterface;

    private TableEntity tableEntity;

    public JobUnit getEntity() {
        return entity;
    }

    public JobGroup setEntity( JobUnit entity ) {
        this.entity = entity;
        return this;
    }

    public JobUnit getServices() {
        return services;
    }

    public JobGroup setServices( JobUnit services ) {
        this.services = services;
        return this;
    }

    public JobUnit getMapperXml() {
        return mapperXml;
    }

    public JobGroup setMapperXml( JobUnit mapperXml ) {
        this.mapperXml = mapperXml;
        return this;
    }

    public JobUnit getRepository() {
        return repository;
    }

    public JobGroup setRepository( JobUnit repository ) {
        this.repository = repository;
        return this;
    }

    public JobUnit getController() {
        return controller;
    }

    public JobGroup setController( JobUnit controller ) {
        this.controller = controller;
        return this;
    }

    public JobUnit getServicesInterface() {
        return servicesInterface;
    }

    public JobGroup setServicesInterface( JobUnit servicesInterface ) {
        this.servicesInterface = servicesInterface;
        return this;
    }

    public TableEntity getTableEntity() {
        return tableEntity;
    }

    public JobGroup setTableEntity( TableEntity tableEntity ) {
        this.tableEntity = tableEntity;
        return this;
    }

    @Override
    public String toString() {
        return "JobGroup{" +
                "entity=" + entity +
                ", services=" + services +
                ", mapperXml=" + mapperXml +
                ", repository=" + repository +
                ", controller=" + controller +
                ", servicesInterface=" + servicesInterface +
                ", tableEntity=" + tableEntity +
                '}';
    }
}
