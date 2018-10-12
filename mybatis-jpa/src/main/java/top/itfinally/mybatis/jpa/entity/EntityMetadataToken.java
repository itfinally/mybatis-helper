package top.itfinally.mybatis.jpa.entity;

/**
 * <pre>
 * *********************************************
 * All rights reserved.
 * Description: ${类文件描述}
 * *********************************************
 *  Version       Date          Author        Desc ( 一句话描述修改 )
 *  v1.0          2018/10/11       itfinally       首次创建
 * *********************************************
 * </pre>
 */
public class EntityMetadataToken extends EntityMetadata {
    private String token;

    public String getToken() {
        return token;
    }

    public EntityMetadataToken setToken( String token ) {
        this.token = token;
        return this;
    }
}
