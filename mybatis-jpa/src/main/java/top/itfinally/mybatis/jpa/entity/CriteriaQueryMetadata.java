package top.itfinally.mybatis.jpa.entity;


import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

/**
 * <pre>
 * *********************************************
 * All rights reserved.
 * Description: ${类文件描述}
 * *********************************************
 *  Version       Date          Author        Desc ( 一句话描述修改 )
 *  v1.0          2018/9/9       itfinally       首次创建
 * *********************************************
 * </pre>
 */
public class CriteriaQueryMetadata<Entity> {
    private Class<Entity> mainEntityClass;

    {
        EntityManager manager = null;
        CriteriaBuilder builder = manager.getCriteriaBuilder();
        CriteriaQuery<Object> query = builder.createQuery( Object.class );
        Root<Object> root = query.from( Object.class );
        root.get( "" );

        query.where( root.<Boolean>get( "" ).isNull() );
    }

    public Class<Entity> getMainEntityClass() {
        return mainEntityClass;
    }

    public CriteriaQueryMetadata<Entity> setMainEntityClass( Class<Entity> mainEntityClass ) {
        this.mainEntityClass = mainEntityClass;
        return this;
    }
}
