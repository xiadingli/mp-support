/**
 * Copyright (c) 2011-2020, hubin (jobob@qq.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package com.nmg.mp.generator.engine;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.nmg.mp.generator.InjectionConfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nmg.mp.generator.config.ConstVal;
import com.nmg.mp.generator.config.FileOutConfig;
import com.nmg.mp.generator.config.GlobalConfig;
import com.nmg.mp.generator.config.TemplateConfig;
import com.nmg.mp.generator.config.builder.ConfigBuilder;
import com.nmg.mp.generator.config.po.TableInfo;
import com.baomidou.mybatisplus.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.toolkit.StringUtils;

/**
 * <p>
 * 模板引擎抽象类
 * </p>
 *
 * @author hubin
 * @since 2018-01-10
 */
public abstract class AbstractTemplateEngine {

    protected static final Logger logger = LoggerFactory.getLogger(AbstractTemplateEngine.class);
    /**
     * 配置信息
     */
    private ConfigBuilder         configBuilder;

    /**
     * <p>
     * 模板引擎初始化
     * </p>
     */
    public AbstractTemplateEngine init(ConfigBuilder configBuilder) {
        this.configBuilder = configBuilder;
        return this;
    }

    /**
     * <p>
     * 输出 java xml 文件
     * </p>
     */
    public AbstractTemplateEngine batchOutput() {
        try {
            List<TableInfo> tableInfoList = this.getConfigBuilder().getTableInfoList();
            for (TableInfo tableInfo : tableInfoList) {
                Map<String, Object> objectMap = this.getObjectMap(tableInfo);
                Map<String, String> pathInfo = this.getConfigBuilder().getPathInfo();
                TemplateConfig template = this.getConfigBuilder().getTemplate();
                // 自定义内容
                InjectionConfig injectionConfig = this.getConfigBuilder().getInjectionConfig();
                if (null != injectionConfig) {
                    injectionConfig.initMap();
                    objectMap.put("cfg", injectionConfig.getMap());
                    List<FileOutConfig> focList = injectionConfig.getFileOutConfigList();
                    if (CollectionUtils.isNotEmpty(focList)) {
                        for (FileOutConfig foc : focList) {
                            if (this.isCreate(foc.outputFile(tableInfo))) {
                                this.writer(objectMap, foc.getTemplatePath(), foc.outputFile(tableInfo));
                            }
                        }
                    }
                }
                // Mp.java
                String entityName = tableInfo.getEntityName();
                if (null != entityName) {
                    String entityFile = String.format((pathInfo.get(ConstVal.ENTITY_PATH) + File.separator + "%s"
                                                       + this.suffixJavaOrKt()),
                                                      entityName);
                    if (this.isCreate(entityFile)) {
                        this.writer(objectMap,
                                    this.templateFilePath(template.getEntity(this.getConfigBuilder().getGlobalConfig().isKotlin())),
                                    entityFile);
                    }
                }
                // MpMapper.java
                if (null != tableInfo.getMapperName()) {
                    String mapperFile = String.format((pathInfo.get(ConstVal.MAPPER_PATH) + File.separator
                                                       + tableInfo.getMapperName() + this.suffixJavaOrKt()),
                                                      entityName);
                    if (this.isCreate(mapperFile)) {
                        this.writer(objectMap, this.templateFilePath(template.getMapper()), mapperFile);
                    }
                }
                // MpMapper.xml
                if (null != tableInfo.getXmlName()) {
                    String xmlFile = String.format((pathInfo.get(ConstVal.XML_PATH) + File.separator
                                                    + tableInfo.getXmlName() + ConstVal.XML_SUFFIX),
                                                   entityName);
                    if (this.isCreate(xmlFile)) {
                        this.writer(objectMap, this.templateFilePath(template.getXml()), xmlFile);
                    }
                }
                
                // IMpService.java
                if (null != tableInfo.getServiceName()) {
                    String serviceFile = String.format((pathInfo.get(ConstVal.SERIVCE_PATH) + File.separator
                                                        + tableInfo.getServiceName() + this.suffixJavaOrKt()),
                                                       entityName);
                    if (this.isCreate(serviceFile)) {
                        this.writer(objectMap, this.templateFilePath(template.getService()), serviceFile);
                    }
                }
                // MpServiceImpl.java
                if (null != tableInfo.getServiceImplName()) {
                    String implFile = String.format((pathInfo.get(ConstVal.SERVICEIMPL_PATH) + File.separator
                                                     + tableInfo.getServiceImplName() + this.suffixJavaOrKt()),
                                                    entityName);
                    if (this.isCreate(implFile)) {
                        this.writer(objectMap, this.templateFilePath(template.getServiceImpl()), implFile);
                    }
                }
                // MpController.java
                if (null != tableInfo.getControllerName()) {
                    String controllerFile = String.format((pathInfo.get(ConstVal.CONTROLLER_PATH) + File.separator
                                                           + tableInfo.getControllerName() + this.suffixJavaOrKt()),
                                                          entityName);
                    if (this.isCreate(controllerFile)) {
                        this.writer(objectMap, this.templateFilePath(template.getController()), controllerFile);
                    }
                }
                // facade.java
                if (null != tableInfo.getFacadeName()) {
                    String facadeFile = String.format((pathInfo.get(ConstVal.FACADE_PATH) + File.separator
                                                       + tableInfo.getFacadeName() + this.suffixJavaOrKt()),
                                                      entityName);
                    if (this.isCreate(facadeFile)) {
                        this.writer(objectMap, this.templateFilePath(template.getFacade()), facadeFile);
                    }
                }
                // facadeImpl.java
                if (null != tableInfo.getFacadeImplName()) {
                    String facadeImplFile = String.format((pathInfo.get(ConstVal.FACADEIMPL_PATH) + File.separator
                                                           + tableInfo.getFacadeImplName() + this.suffixJavaOrKt()),
                                                          entityName);
                    if (this.isCreate(facadeImplFile)) {
                        this.writer(objectMap, this.templateFilePath(template.getFacadeImpl()), facadeImplFile);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("无法创建文件，请检查配置信息！", e);
        }
        return this;
    }

    /**
     * <p>
     * 将模板转化成为文件
     * </p>
     *
     * @param objectMap 渲染对象 MAP 信息
     * @param templatePath 模板文件
     * @param outputFile 文件生成的目录
     */
    public abstract void writer(Map<String, Object> objectMap, String templatePath, String outputFile) throws Exception;

    /**
     * <p>
     * 处理输出目录
     * </p>
     */
    public AbstractTemplateEngine mkdirs() {
        Map<String, String> pathInfo = this.getConfigBuilder().getPathInfo();
        for (Map.Entry<String, String> entry : pathInfo.entrySet()) {
            File dir = new File(entry.getValue());
            if (!dir.exists()) {
                boolean result = dir.mkdirs();
                if (result) {
                    logger.debug("创建目录： [" + entry.getValue() + "]");
                }
            }
        }
        return this;
    }

    /**
     * <p>
     * 打开输出目录
     * </p>
     */
    public void open() {
        if (this.getConfigBuilder().getGlobalConfig().isOpen()) {
            try {
                String osName = System.getProperty("os.name");
                if (osName != null) {
                    if (osName.contains("Mac")) {
                        Runtime.getRuntime().exec("open " + this.getConfigBuilder().getGlobalConfig().getOutputDir());
                    } else if (osName.contains("Windows")) {
                        Runtime.getRuntime().exec("cmd /c start "
                                                  + this.getConfigBuilder().getGlobalConfig().getOutputDir());
                    } else {
                        logger.debug("文件输出目录:" + this.getConfigBuilder().getGlobalConfig().getOutputDir());
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * <p>
     * 渲染对象 MAP 信息
     * </p>
     *
     * @param tableInfo 表信息对象
     * @return
     */
    public Map<String, Object> getObjectMap(TableInfo tableInfo) {
        Map<String, Object> objectMap = new HashMap<>();
        ConfigBuilder config = this.getConfigBuilder();
        if (config.getStrategyConfig().isControllerMappingHyphenStyle()) {
            objectMap.put("controllerMappingHyphenStyle", config.getStrategyConfig().isControllerMappingHyphenStyle());
            objectMap.put("controllerMappingHyphen", StringUtils.camelToHyphen(tableInfo.getEntityPath()));
        }
        objectMap.put("restControllerStyle", config.getStrategyConfig().isRestControllerStyle());
        objectMap.put("package", config.getPackageInfo());
        GlobalConfig globalConfig = config.getGlobalConfig();
        objectMap.put("author", globalConfig.getAuthor());
        objectMap.put("idType", globalConfig.getIdType() == null ? null : globalConfig.getIdType().toString());
        objectMap.put("logicDeleteFieldName", config.getStrategyConfig().getLogicDeleteFieldName());
        objectMap.put("versionFieldName", config.getStrategyConfig().getVersionFieldName());
        objectMap.put("activeRecord", globalConfig.isActiveRecord());
        objectMap.put("kotlin", globalConfig.isKotlin());
        objectMap.put("date", new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        objectMap.put("table", tableInfo);
        objectMap.put("enableCache", globalConfig.isEnableCache());
        objectMap.put("baseResultMap", globalConfig.isBaseResultMap());
        objectMap.put("baseColumnList", globalConfig.isBaseColumnList());
        objectMap.put("entity", tableInfo.getEntityName());
        objectMap.put("entityColumnConstant", config.getStrategyConfig().isEntityColumnConstant());
        objectMap.put("entityBuilderModel", config.getStrategyConfig().isEntityBuilderModel());
        objectMap.put("entityLombokModel", config.getStrategyConfig().isEntityLombokModel());
        objectMap.put("entityBooleanColumnRemoveIsPrefix",
                      config.getStrategyConfig().isEntityBooleanColumnRemoveIsPrefix());
        objectMap.put("superEntityClass", this.getSuperClassName(config.getSuperEntityClass()));
        objectMap.put("superMapperClassPackage", config.getSuperMapperClass());
        objectMap.put("superMapperClass", this.getSuperClassName(config.getSuperMapperClass()));
        objectMap.put("superServiceClassPackage", config.getSuperServiceClass());
        objectMap.put("superServiceClass", this.getSuperClassName(config.getSuperServiceClass()));
        objectMap.put("superServiceImplClassPackage", config.getSuperServiceImplClass());
        objectMap.put("superServiceImplClass", this.getSuperClassName(config.getSuperServiceImplClass()));
        objectMap.put("superControllerClassPackage", config.getSuperControllerClass());
        objectMap.put("superControllerClass", this.getSuperClassName(config.getSuperControllerClass()));

        objectMap.put("superFacadeClassPackage", config.getSuperFacadeClass());
        objectMap.put("superFacadeClass", this.getSuperClassName(config.getSuperFacadeClass()));
        objectMap.put("superFacadeImplClassPackage", config.getSuperFacadeImplClass());
        objectMap.put("superFacadeImplClass", this.getSuperClassName(config.getSuperFacadeImplClass()));

        return objectMap;
    }

    /**
     * 获取类名
     *
     * @param classPath
     * @return
     */
    private String getSuperClassName(String classPath) {
        if (StringUtils.isEmpty(classPath)) {
            return null;
        }
        return classPath.substring(classPath.lastIndexOf(".") + 1);
    }

    /**
     * <p>
     * 模板真实文件路径
     * </p>
     *
     * @param filePath 文件路径
     * @return
     */
    public abstract String templateFilePath(String filePath);

    /**
     * 检测文件是否存在
     *
     * @return 是否
     */
    protected boolean isCreate(String filePath) {
        File file = new File(filePath);
        boolean exist = file.exists();
        if (!exist) {
            this.mkDir(file.getParentFile());
        }
        return !exist || this.getConfigBuilder().getGlobalConfig().isFileOverride();
    }

    protected void mkDir(File file) {
        if (file.getParentFile().exists()) {
            file.mkdir();
        } else {
            mkDir(file.getParentFile());
            file.mkdir();
        }
    }

    /**
     * 文件后缀
     */
    protected String suffixJavaOrKt() {
        return this.getConfigBuilder().getGlobalConfig().isKotlin() ? ConstVal.KT_SUFFIX : ConstVal.JAVA_SUFFIX;
    }

    public ConfigBuilder getConfigBuilder() {
        return configBuilder;
    }

    public AbstractTemplateEngine setConfigBuilder(ConfigBuilder configBuilder) {
        this.configBuilder = configBuilder;
        return this;
    }
}
