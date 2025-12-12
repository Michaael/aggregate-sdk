package com.tibbo.aggregate.common.server;

public interface ApplicationContextConstants
{
  String V_RESOURCES = "resources";
  String V_TEXT_RESOURCES = "textResources";
  String V_CONTEXT_VARIABLES = "contextVariables";
  String V_ADDITIONAL_EXPORT_IMPORT_RULES = "additionalExportImportRules";
  String V_APPLICATION_DEPENDENCIES = "applicationDependencies";

  String A_OPEN_APPLICATION = "openApplication";
  String VF_CHILD_APPLICATION_HOME_DASHBOARD = "applicationHomeDashboard";
  String VF_CHILD_APPLICATION_DISABLE_SIMPLE_MODE = "applicationDisableSimpleMode";
  String VF_CHILD_APPLICATION_AUTO_DEPLOY = "applicationAutoDeploy";
  String VF_CHILD_APPLICATION_AUTO_DEPLOY_PARAMETERS = "applicationAutoDeployParameters";
  Boolean VF_CHILD_APPLICATION_DISABLE_SIMPLE_MODE_DEFAULT = false;
  String F_PACK = "pack";
  String F_DELETE_ALL = "deleteAll";
  String F_EXPORT = "export";
  String F_EXPORT_CONTEXT = "exportContext";
  String F_IMPORT = "import";
  String F_UPDATE = "update";
  String F_DEPLOY = "deploy";
  String F_ROLLBACK = "rollback";
  String F_CREATE_TEXT_RESOURCE_BUNDLE = "createTextResourceBundle";
  String F_GET_PLUGIN_ID_MAP = "getPluginIdMap";
  String F_GET_DEFAULT_PLUGIN_ID = "getDefaultPluginId";
  String A_PACK = "pack";
  String A_DEPLOY = "deploy";
  String A_ROLLBACK = "rollback";
  String A_DELETE_ALL = "deleteAll";
  
  String VF_RESOURCE = "resource";
  String VF_DESCRIPTION = "description";
  String VF_CONTEXT_DESCRIPTION = "contextDescription";
  String VF_TYPE = "type";
  String VF_CATEGORY = "category";
  String VF_SUBCATEGORY = "subcategory";
  String VF_GROUP_NAME = "groupName";
  String VF_GROUP_DESCRIPTION = "groupDescription";
  String VF_VERSION = "version";
  String VF_DEPENDENCY = "dependency";
  String VF_DEPENDENCIES = "dependencies";
  String VF_CREATE_ON_FIRST_SERVER_LAUNCH = "createOnFirstServerLaunch";
  String VF_PROTECTED = "protected";
  String VF_INCLUDE_DEPENDENT_RESOURCES = "includeDependentResources";
  String VF_INCLUDE_DEPENDENT_VARIABLES = "includeDependentVariables";
  String VF_DEPENDENT_RESOURCES = "dependentResources";
  String VF_DEPENDENT_VARIABLES = "dependentVariables";
  String VF_DEPENDENT_RESOURCE = "dependentResource";
  String VF_DEPENDENT_RESOURCE_TYPE = "dependentResourceType";
  String VF_LANGUAGE = "language";
  String VF_LANGUAGES = "languages";
  String VF_RESOURCE_ID = "resourceId";
  String VF_TEXT_RESOURCE_BUNDLE = "textResourceBundle";

  String VF_CONTEXT = "context";

  String VF_VARIABLE = "variable";
  String VF_APPLICATION_PLUGIN_ID = "id";
  String VF_RULES_NAME = "name";
  String VF_EXPORT_EXPRESSION = "exportExpression";
  String VF_TEMPLATE_CREATION_EXPRESSION = "templateCreationExpression";
  String VF_CREATION_TEMPLATE= "creationTemplate";
  String VF_IMPORT_EXPRESSION = "importExpression";
  String FIF_APPLICATION_PLUGIN_ID = "id";
  String FIF_PACKAGE = "package";
  String FIF_OPERATION_TYPE = "operationType";
  String FIF_APPLICATION_ARCHIVE = "applicationArchive";
  String FIF_APPLICATION_FOLDER_PATH = "applicationFolderPath";
  String FIF_DEPERSONALIZE_EXPORTED_DATA = "depersonalizeExportedData";
  
  String FIF_ADD_IDE_PROJECT_FILES = "addIdeProjectFiles";
  String FIF_HANDLING_EXISTING_CONTEXTS = "handlingExistingContexts";
  String FIF_DEPENDENCIES_FOLDER_PATH = "dependenciesFolderPath";
  
  String FIF_DELETE_NOT_LISTED_CONTEXTS = "deleteNotListedContexts";
  String FIF_ERROR_HANDLING = "errorHandling";

  String FIF_DEPLOYMENT_USER_MODE = "deploymentUserMode";
  String FIF_DEPLOYMENT_USER = "deploymentUser";
  String FIF_DEPLOYMENT_REMOVE_ORPHANS = "deploymentRemoveOrphans";
  
  String FIF_LANGUAGE = "language";
  String FIF_LANGUAGES = "languages";
  String FIF_TEXT_RESOURCE_BUNDLE = "textResourceBundle";

  String FOF_PERSISTENT_PROPERTIES = "persistentProperties";
  String FOF_PACK_RESULT = "result";
  
  String TEMP_FOLDER_SUFFIX = "_temp/";
  String RESOURCES = "resources.tbl";
  String LINE_SEPARATOR = "\n";
  
  String JAVA_ARCHIVE_EXTENSION = ".jar";
  String CLASS_FILE_EXTENSION = ".class";
  String JAVA_FILE_EXTENSION = ".java";
  String PROPERTIES_FILE_EXTENSION = ".prs";
  String TEXT_RESOURCE_FILE_EXTENSION = ".properties";
  String TABLE_EXTENSION = ".tbl";
  
  String PLUGIN_NAME_TEMPLATE = "%pluginName%";
  String PLUGIN_ID_TEMPLATE = "%pluginId%";
  String PLUGIN_DOC_TEXT_TEMPLATE = "%pluginDocText%";
  String PLUGIN_CLASS_NAME_TEMPLATE = "%pluginClassName%";
  String PLUGIN_DEPENDENCY = "%pluginDependency%";
  String PLUGIN_REQUIRES = "%pluginRequires%";
  String PLUGIN_CLASS_NAME_SUFFIX = "ContextPlugin";
  String PLUGIN_DESCRIPTOR_FILE_NAME = "plugin.xml";
  String PLUGIN_ID_PREFIX = "com.tibbo.linkserver.plugin.context.";
  String APPLICATION_ID_PREFIX = "com.tibbo.linkserver.plugin.context.application.";
  
  String PLUGIN_PACKAGE_TEMPLATE = "%pluginPackage%";
  String PACKAGE = "%package%";
  
  String SOURCE_FOLDER_NAME = "src/main/java";
  String LRES = "Lres";
  String RES = ".res";
  String RES_LRES = ".res.Lres";
  String GRADLE_FILE = "build.gradle.kts";
  
  String RESOURCE_BUNDLE_PROPERTY = "resourceBundle";
  
  String USE_APPLICATION_NAME = "useApplicationName";
  String USE_APPLICATION_DESCRIPTION = "useApplicationDescription";
  String USE_CONTEXT_DESCRIPTION = "useContextDescription";
  String DO_NOT_CREATE_GROUP = "doNotCreateGroup";
  
  String APPLICATION_TYPE = "application";
  String LOCAL = "local";
  String REMOTE = "remote";
  
  // Properties for application.properties file
  String APPLICATION_PROPERTIES = "application.properties";
  String MAIN_APPLICATION_FILE_NAME = "mainApplicationFileName";
  String SERVER_VERSION = "serverVersion";
  String DEPENDENCIES = "dependencies";
  String ID = "id";
  
  int SKIP = 0;
  int REPLACE = 1;
  int UPDATE = 2;
  
  int IGNORE = 0;
  int REVERT = 1;
  
  String MAIN_PLUGIN_CLASS = "public class " + PLUGIN_CLASS_NAME_TEMPLATE + " extends com.tibbo.linkserver.plugin.context.applications.AbstractApplicationContextPlugin {}";
  String MAIN_PLUGIN_JAVA_CLASS = PLUGIN_PACKAGE_TEMPLATE +
      "\r\n" +
      "  public class " + PLUGIN_CLASS_NAME_TEMPLATE + " extends com.tibbo.linkserver.plugin.context.applications.AbstractApplicationContextPlugin\r\n" +
      "  {\r\n" +
      "    \r\n" +
      "  }";
  
  String PACKAGE_TEMPLATE = "package " + PACKAGE + ";\r\n";
  
  String REQUIRES = "    <import plugin-id=\"" + PLUGIN_DEPENDENCY + "\"/>\r\n";
  
  String PLUGIN_DESCRIPTOR = "<?xml version=\"1.0\" ?>\r\n" +
      "<!DOCTYPE plugin PUBLIC \"-//JPF//Java Plug-in Manifest 0.4\" \"http://jpf.sourceforge.net/plugin_0_4.dtd\">\r\n" +
      "<plugin id=\"" + PLUGIN_ID_TEMPLATE + "\" version=\"0.0.1\" class=\"" + PLUGIN_CLASS_NAME_TEMPLATE + "\">\r\n" +
      "  <doc>\r\n" +
      "    <doc-text>" + PLUGIN_DOC_TEXT_TEMPLATE + "</doc-text>\r\n" +
      "  </doc>\r\n" +
      "  <requires>\r\n" +
      "    <import plugin-id=\"com.tibbo.aggregate.common.plugin.extensions\"/>\r\n" +
      "    <import plugin-id=\"com.tibbo.linkserver.plugin.context.applications\"/>\r\n" +
      PLUGIN_REQUIRES +
      "  </requires>\r\n" +
      "  <runtime>\r\n" +
      "    <library id=\"tibbo\" path=\"/\" type=\"code\">\r\n" +
      "      <export prefix=\"*\" />\r\n" +
      "    </library>\r\n" +
      "  </runtime>\r\n" +
      "  <extension plugin-id=\"com.tibbo.aggregate.common.plugin.extensions\" point-id=\"context\" id=\"" + PLUGIN_NAME_TEMPLATE + "\"/>\r\n" +
      "</plugin>\r\n";
  
  String GRADLE = "import provider.aggregateVersion\r\n" +
      "import provider.groupCore\r\n" +
      "import provider.groupContext\r\n" +
      "\r\n" +
      "val mavenJar by configurations.creating\r\n" +
      "val jarName = \"" + PLUGIN_NAME_TEMPLATE + "\"\r\n" +
      "\r\n" +
      "dependencies {\r\n" +
      "    implementation(project(\":aggregate-api\"))\r\n" +
      "    implementation(project(\":context-applications\"))\r\n" +
      "\r\n" +
      "    mavenJar(\"$groupCore.aggregate-extensions:aggregate-extensions:$aggregateVersion\")\r\n" +
      "    mavenJar(\"$groupContext.applications:applications:$aggregateVersion\")\r\n" +
      "}\r\n" +
      "\r\n" +
      "tasks {\r\n" +
      "      withType<Jar> {\r\n" +
      "           duplicatesStrategy = DuplicatesStrategy.EXCLUDE\r\n" +
      "           archiveFileName.set(\"$jarName.jar\")\r\n" +
      "           destinationDirectory.set(File(\"../linkserver-core/plugins/context\"))\r\n" +
      "           from(\"build/classes/java/main\")\r\n" +
      "           from(\"build/classes/java/test\")\r\n" +
      "           from(\"build/resources/main\")\r\n" +
      "           from(\"plugin.xml\")\r\n" +
      "      }\r\n" +
      "      clean {\r\n" +
      "          delete(\"../linkserver-core/plugins/context/$jarName.jar\")\r\n" +
      "      }\r\n" +
      "  }\r\n" +
      "\r\n" +
      "publishing {\r\n" +
      "    publications {\r\n" +
      "        register(\"mavenJava\", MavenPublication::class) {\r\n" +
      "            groupId = \"${groupContext}.${jarName}\"\r\n" +
      "            artifactId = jarName\r\n" +
      "            version = aggregateVersion\r\n" +
      "            pom.withXml {\r\n" +
      "                val dependenciesNode = asNode().appendNode(\"dependencies\")\r\n" +
      "\r\n" +
      "                mavenJar.allDependencies.forEach { dependency ->\r\n" +
      "                    val dependencyNode = dependenciesNode.appendNode(\"dependency\")\r\n" +
      "                    dependencyNode.appendNode(\"groupId\", dependency.group)\r\n" +
      "                    dependencyNode.appendNode(\"artifactId\", dependency.name)\r\n" +
      "                    dependencyNode.appendNode(\"version\", dependency.version)\r\n" +
      "                    dependencyNode.appendNode(\"scope\", \"compile\")\r\n" +
      "                }\r\n" +
      "            }\r\n" +
      "            artifact(\"../linkserver-core/plugins/context/$jarName.jar\")\r\n" +
      "        }\r\n" +
      "    }\r\n" +
      "}";
  
  String LRES_FILE = PLUGIN_PACKAGE_TEMPLATE +
      "\r\n" +
      "import java.util.*;\r\n" +
      "\r\n" +
      "import com.tibbo.aggregate.common.resource.*;\r\n" +
      "import com.tibbo.aggregate.common.util.*;\r\n" +
      "\r\n" +
      "public class Lres\r\n" +
      "{\r\n" +
      "  private static ResourceBundle BUNDLE = ResourceAccessor.fetch(Lres.class, ResourceManager.getLocale(), Lres.class.getClassLoader());\r\n" +
      "  \r\n" +
      "  public static ResourceBundle get()\r\n" +
      "  {\r\n" +
      "    return BUNDLE;\r\n" +
      "  }\r\n" +
      "  \r\n" +
      "  public void reinit(Locale locale)\r\n" +
      "  {\r\n" +
      "    BUNDLE = ResourceAccessor.fetch(Lres.class, locale);\r\n" +
      "  }\r\n" +
      "}";
  
}
