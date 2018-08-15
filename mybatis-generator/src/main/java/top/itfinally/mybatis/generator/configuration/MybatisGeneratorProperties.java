package top.itfinally.mybatis.generator.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import top.itfinally.mybatis.generator.exception.LackOfConfigurationException;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * <pre>
 * *********************************************
 * All rights reserved.
 * Description: ${类文件描述}
 * *********************************************
 *  Version       Date          Author        Desc ( 一句话描述修改 )
 *  v1.0          2018/7/30       itfinally       首次创建
 * *********************************************
 * </pre>
 */
@Configuration
@ConfigurationProperties( prefix = "mybatis.generator" )
public class MybatisGeneratorProperties {

    // 生成 java 文件的路径
    private String javaFilePath;

    // 生成 mapper.xml 文件的路径
    private String resourcesPath;

    // 自定义模板的基础路径
    private String templateBasicPath;

    // 基础包路径, 如 aaa.bbb.ccc
    private String basicPackage;

    // 实体包路径, 如 ddd, 或者 aaa.bbb.ccc.ddd
    private String entityPackage;

    // mapper 类的包路径, 如 eee, 或者 aaa.bbb.ccc.eee
    private String repositoryPackage;

    // entity 模版名
    private String entityTemplateName;

    // repository 模版名
    private String repositoryTemplateName;

    // mapper.xml 模板名
    private String mapperXmlTemplateName;

    // 是否生成 services 类
    private boolean includeServices = false;

    private String servicesTemplateName;

    private String servicePackage;

    // 是否生成 services 类的接口类
    private boolean includeServiceInterfaces = false;

    private String servicesInterfaceTemplateName;

    private String servicesInterfacePackage;

    // 是否生成 controller 类
    private boolean includeController = false;

    private String controllerTemplateName;

    private String controllerPackage;

    // optional

    // 父类实体
    private String superEntity;

    // 父类 services
    private String superServices;

    // 父类 services 接口
    private String superServicesInterface;

    // 父类 controller
    private String superController;

    // 父类 mapper
    private String superRepository;

    // 是否要转换为驼峰命名, true 为转换
    private boolean convertToCamel = true;

    // 是否强制生成文件, true 为强制生成
    private boolean forceToGenerate = false;

    public String getJavaFilePath() {
        if ( StringUtils.isEmpty( javaFilePath ) ) {
            throw new LackOfConfigurationException( "Require to config 'javaFilePath'." );
        }

        return mixPath( this.javaFilePath );
    }

    public String getResourcesPath() {
        if ( StringUtils.isEmpty( resourcesPath ) ) {
            throw new LackOfConfigurationException( "Require to config 'resourcesPath'." );
        }

        return mixPath( this.resourcesPath );
    }

    public String getTemplateBasicPath() {
        return templateBasicPath;
    }

    public String getBasicPackage() {
        if ( StringUtils.isEmpty( basicPackage ) ) {
            throw new LackOfConfigurationException( "Require to config 'basicPackage'." );
        }

        return basicPackage.trim();
    }

    public String getEntityPackage() {
        if ( StringUtils.isEmpty( entityPackage ) ) {
            throw new LackOfConfigurationException( "Require to config 'entityPackage'." );
        }

        return mixPackagePath( entityPackage.trim() );
    }

    public String getRepositoryPackage() {
        if ( StringUtils.isEmpty( repositoryPackage ) ) {
            throw new LackOfConfigurationException( "Require to config 'repositoryPackage'." );
        }

        return mixPackagePath( repositoryPackage.trim() );
    }

    public String getEntityTemplateName() {
        return entityTemplateName;
    }

    public String getRepositoryTemplateName() {
        return repositoryTemplateName;
    }

    public String getMapperXmlTemplateName() {
        return mapperXmlTemplateName;
    }

    public boolean isIncludeServices() {
        return includeServices;
    }

    public String getServicesTemplateName() {
        if ( !isIncludeServices() ) {
            return "";
        }

        if ( StringUtils.isEmpty( servicesTemplateName ) ) {
            throw new LackOfConfigurationException( "Require to config 'servicesTemplateName'." );
        }

        return servicesTemplateName.trim();
    }

    public String getServicePackage() {
        if ( !isIncludeServices() ) {
            return "";
        }

        if ( StringUtils.isEmpty( this.servicePackage ) ) {
            throw new LackOfConfigurationException( "Require to config 'servicePackage'." );
        }

        return mixPackagePath( this.servicePackage.trim() );
    }

    public boolean isIncludeServiceInterfaces() {
        return includeServiceInterfaces;
    }

    public String getServicesInterfaceTemplateName() {
        if ( !isIncludeServiceInterfaces() ) {
            return "";
        }

        if ( StringUtils.isEmpty( servicesInterfaceTemplateName ) ) {
            throw new LackOfConfigurationException( "Require to config 'servicesInterfaceTemplateName'." );
        }

        return servicesInterfaceTemplateName.trim();
    }

    public String getServicesInterfacePackage() {
        if ( !isIncludeServiceInterfaces() ) {
            return "";
        }

        if ( StringUtils.isEmpty( servicesInterfacePackage ) ) {
            throw new LackOfConfigurationException( "Require to config 'servicesInterfacePackage'." );
        }

        return mixPackagePath( servicesInterfacePackage.trim() );
    }

    public boolean isIncludeController() {
        return includeController;
    }

    public String getControllerTemplateName() {
        if ( !isIncludeController() ) {
            return "";
        }

        if ( StringUtils.isEmpty( controllerTemplateName ) ) {
            throw new LackOfConfigurationException( "Require to config 'controllerTemplateName'." );
        }

        return controllerTemplateName.trim();
    }

    public String getControllerPackage() {
        if ( !isIncludeController() ) {
            return "";
        }

        if ( StringUtils.isEmpty( controllerPackage ) ) {
            throw new LackOfConfigurationException( "Require to config 'controllerPackage'." );
        }

        return mixPackagePath( controllerPackage.trim() );
    }

    public String getSuperEntity() {
        return superEntity;
    }

    public String getSuperServices() {
        return superServices;
    }

    public String getSuperServicesInterface() {
        return superServicesInterface;
    }

    public String getSuperController() {
        return superController;
    }

    public String getSuperRepository() {
        return superRepository;
    }

    public boolean isConvertToCamel() {
        return convertToCamel;
    }

    public boolean isForceToGenerate() {
        return forceToGenerate;
    }

    // inner method

    private String mixPath( String path ) {
        String realPath = String.format( "%s%s", System.getProperty( "user.dir" ), path );
        File file = new File( realPath );

        if ( !( file.exists() || file.mkdirs() ) ) {
            throw new RuntimeException( new FileNotFoundException( String.format( "Folder '%s' is not exist.", realPath ) ) );
        }

        return realPath;
    }

    private String mixPackagePath( String localPackage ) {
        String basicPackage = getBasicPackage();

        if ( localPackage.startsWith( "." ) ) {
            localPackage = localPackage.replaceAll( "^\\.", "" );
        }

        if ( localPackage.endsWith( "." ) ) {
            localPackage = localPackage.replaceAll( "\\.$", "" );
        }

        return localPackage.startsWith( basicPackage ) ? localPackage : String.format( "%s.%s", basicPackage, localPackage );
    }


    // setter


    public MybatisGeneratorProperties setJavaFilePath( String javaFilePath ) {
        this.javaFilePath = javaFilePath;
        return this;
    }

    public MybatisGeneratorProperties setResourcesPath( String resourcesPath ) {
        this.resourcesPath = resourcesPath;
        return this;
    }

    public MybatisGeneratorProperties setTemplateBasicPath( String templateBasicPath ) {
        this.templateBasicPath = templateBasicPath;
        return this;
    }

    public MybatisGeneratorProperties setBasicPackage( String basicPackage ) {
        this.basicPackage = basicPackage;
        return this;
    }

    public MybatisGeneratorProperties setEntityPackage( String entityPackage ) {
        this.entityPackage = entityPackage;
        return this;
    }

    public MybatisGeneratorProperties setRepositoryPackage( String repositoryPackage ) {
        this.repositoryPackage = repositoryPackage;
        return this;
    }

    public MybatisGeneratorProperties setEntityTemplateName( String entityTemplateName ) {
        this.entityTemplateName = entityTemplateName;
        return this;
    }

    public MybatisGeneratorProperties setRepositoryTemplateName( String repositoryTemplateName ) {
        this.repositoryTemplateName = repositoryTemplateName;
        return this;
    }

    public MybatisGeneratorProperties setMapperXmlTemplateName( String mapperXmlTemplateName ) {
        this.mapperXmlTemplateName = mapperXmlTemplateName;
        return this;
    }

    public MybatisGeneratorProperties setIncludeServices( boolean includeServices ) {
        this.includeServices = includeServices;
        return this;
    }

    public MybatisGeneratorProperties setServicesTemplateName( String servicesTemplateName ) {
        this.servicesTemplateName = servicesTemplateName;
        return this;
    }

    public MybatisGeneratorProperties setServicePackage( String servicePackage ) {
        this.servicePackage = servicePackage;
        return this;
    }

    public MybatisGeneratorProperties setIncludeServiceInterfaces( boolean includeServiceInterfaces ) {
        this.includeServiceInterfaces = includeServiceInterfaces;
        return this;
    }

    public MybatisGeneratorProperties setServicesInterfaceTemplateName( String servicesInterfaceTemplateName ) {
        this.servicesInterfaceTemplateName = servicesInterfaceTemplateName;
        return this;
    }

    public MybatisGeneratorProperties setServicesInterfacePackage( String servicesInterfacePackage ) {
        this.servicesInterfacePackage = servicesInterfacePackage;
        return this;
    }

    public MybatisGeneratorProperties setIncludeController( boolean includeController ) {
        this.includeController = includeController;
        return this;
    }

    public MybatisGeneratorProperties setControllerTemplateName( String controllerTemplateName ) {
        this.controllerTemplateName = controllerTemplateName;
        return this;
    }

    public MybatisGeneratorProperties setControllerPackage( String controllerPackage ) {
        this.controllerPackage = controllerPackage;
        return this;
    }

    public MybatisGeneratorProperties setSuperEntity( String superEntity ) {
        this.superEntity = superEntity;
        return this;
    }

    public MybatisGeneratorProperties setSuperServices( String superServices ) {
        this.superServices = superServices;
        return this;
    }

    public MybatisGeneratorProperties setSuperServicesInterface( String superServicesInterface ) {
        this.superServicesInterface = superServicesInterface;
        return this;
    }

    public MybatisGeneratorProperties setSuperController( String superController ) {
        this.superController = superController;
        return this;
    }

    public MybatisGeneratorProperties setSuperRepository( String superRepository ) {
        this.superRepository = superRepository;
        return this;
    }

    public MybatisGeneratorProperties setConvertToCamel( boolean convertToCamel ) {
        this.convertToCamel = convertToCamel;
        return this;
    }

    public MybatisGeneratorProperties setForceToGenerate( boolean forceToGenerate ) {
        this.forceToGenerate = forceToGenerate;
        return this;
    }
}
