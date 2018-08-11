package top.itfinally.mybatis;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryUtils;

import java.beans.PropertyDescriptor;
import java.util.Arrays;
import java.util.Date;

/**
 * <pre>
 * *********************************************
 * Copyright BAIBU.
 * All rights reserved.
 * Description: ${类文件描述}
 * *********************************************
 *  Version       Date          Author        Desc ( 一句话描述修改 )
 *  v1.0          2018/8/3       itfinally       首次创建
 * *********************************************
 * </pre>
 */
public class BasicEntity {
    private int id;
    private int status;
    private Date createTime;

    public int getId() {
        return id;
    }

    public BasicEntity setId( int id ) {
        this.id = id;
        return this;
    }

    public int getStatus() {
        return status;
    }

    public BasicEntity setStatus( int status ) {
        this.status = status;
        return this;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public BasicEntity setCreateTime( Date createTime ) {
        this.createTime = createTime;
        return this;
    }
}
