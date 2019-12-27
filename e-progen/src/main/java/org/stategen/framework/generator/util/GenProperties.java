package org.stategen.framework.generator.util;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;

import org.stategen.framework.util.BusinessAssert;

public class GenProperties {


    public static String dir_templates_root = null;
    public static String dalgenHome = null;
    public static String tableName = null;
    
    public static String projectsPath = null;
    public static String projectPath = null;
    public static String projectName = null;
    public static String systemName = null;
    public static String packageName = null;

    public static String cmdPath = null;

    public static DaoType daoType = DaoType.ibatis;

    final static org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(GenProperties.class);

    public static String getGenConfigXml() throws IOException {
        String testClassPath = GenProperties.class.getResource("/").toString();
        projectPath = FileHelpers.getCanonicalPath(testClassPath + "../../");
        projectsPath = FileHelpers.getCanonicalPath(projectPath + "../");
        String genConfigXml = FileHelpers.getCanonicalPath(projectsPath + "/gen_config.xml");
        
        System.setProperty(GenConst.projectsPath, projectsPath);
        System.setProperty(GenConst.projectPath, projectPath);
        
        return genConfigXml;
    }
    
    public static void putStatics(Properties properties) {
        properties.put("generator_tools_class","cn.org.rapid_framework.generator.util.StringHelper,org.apache.commons.lang.StringUtils,org.stategen.framework.util.CollectionUtil,org.stategen.framework.util.Setting,org.stategen.framework.generator.util.CompatibleHelper,org.stategen.framework.util.StringUtil,org.stategen.framework.generator.util.TableHelper");
    }

    public static Properties getAllMergedProps(String genConfigXml) throws IOException {

        Map<String, String> environments = System.getenv();

        String dalgenHome = environments.get(GenConst.DALGENX_HOME);
        BusinessAssert.mustNotBlank(dalgenHome, GenConst.DALGENX_HOME + " 环境变量没有设!");

        String genXml = dalgenHome + "/gen.xml";

        Properties mergedProps = new Properties();
        mergedProps.put("gg_isOverride", "true");

        mergedProps.put("generator_sourceEncoding", "UTF-8");
        mergedProps.put("generator_outputEncoding", "UTF-8");

        //将表名从复数转换为单数
        mergedProps.put("tableNameSingularize", "true");

        mergedProps.putAll(environments);

        Properties systemProperties = System.getProperties();
        mergedProps.putAll(systemProperties);

        Properties genXmlProperties = PropertiesHelpers.load(genXml);
        mergedProps.putAll(genXmlProperties);
        if (FileHelpers.isExists(genConfigXml)) {
            Properties genConfigXmlProperties = PropertiesHelpers.load(genConfigXml);
            mergedProps.putAll(genConfigXmlProperties);
        }

        dalgenHome = (String) mergedProps.get(GenConst.DALGENX_HOME);
        dir_templates_root = dalgenHome + "/templates/";
        mergedProps.put("dir_templates_root", dir_templates_root);
        
        projectsPath=mergedProps.getProperty(GenConst.projectsPath);
        tableName=mergedProps.getProperty(GenConst.tableName);

        return mergedProps;
    }
}
