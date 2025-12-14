object ApacheCommonsLibs {
    object Versions {
        const val digesterVersion = "1.8"
        const val discoveryVersion = "0.2"
        const val elVersion = "1.0"
        const val modelerVersion = "1.1"
        const val netVersion = "3.3"
        const val collectionsVersion = "3.2.2"
        const val ioVersions = "2.11.0"
        const val codecVersion = "1.16.0"
        const val fileuploadVersion = "1.5"
        const val commonsLang3Version = "3.14.0"
        const val commonsMath3Version = "3.6.1"
        const val commonsBeanutilsVersion = "1.9.4"
        const val commonsLoggingVersion = "1.2"
    }

    const val commonsDigester = "commons-digester:commons-digester:${Versions.digesterVersion}"
    const val commonsDiscovery = "commons-discovery:commons-discovery:${Versions.discoveryVersion}"
    const val commonsEl = "commons-el:commons-el:${Versions.elVersion}"
    const val commonsModeler = "commons-modeler:commons-modeler:${Versions.modelerVersion}"
    const val commonsNet = "commons-net:commons-net:${Versions.netVersion}"
    const val commonsCollections = "commons-collections:commons-collections:${Versions.collectionsVersion}"
    const val commonsIo = "commons-io:commons-io:${Versions.ioVersions}"
    const val commonsCodec = "commons-codec:commons-codec:${Versions.codecVersion}"
    const val commonsFileupload = "commons-fileupload:commons-fileupload:${Versions.fileuploadVersion}"
    const val commonsLang3 = "org.apache.commons:commons-lang3:${Versions.commonsLang3Version}"
    const val commonsMath3 = "org.apache.commons:commons-math3:${Versions.commonsMath3Version}"
    const val commonsBeanutils = "commons-beanutils:commons-beanutils:${Versions.commonsBeanutilsVersion}"
    const val commonsLogging = "commons-logging:commons-logging:${Versions.commonsLoggingVersion}"
}

object SpringLibs {
    object Versions {
        const val springVersion = "5.3.23"
        const val springSecurityVersion = "5.7.3"
    }

    const val springContext = "org.springframework:spring-context:${Versions.springVersion}"
    const val springMessaging = "org.springframework:spring-messaging:${Versions.springVersion}"
    const val springWebsocket = "org.springframework:spring-websocket:${Versions.springVersion}"
    const val springWebmvc = "org.springframework:spring-webmvc:${Versions.springVersion}"
    const val springSecurityConfig = "org.springframework.security:spring-security-config:${Versions.springSecurityVersion}"
    const val springSecurityWeb = "org.springframework.security:spring-security-web:${Versions.springSecurityVersion}"
    const val springTx = "org.springframework:spring-tx:${Versions.springVersion}"
    const val springTest = "org.springframework:spring-test:${Versions.springVersion}"
}

object JacksonLibs {
    object Versions {
        const val jacksonVersion = "2.14.1"
        const val jacksonDatabindVersion = "2.14.1"
        const val jacksonKotlinVersion = "2.10.1"
    }

    const val jacksonDatabind = "com.fasterxml.jackson.core:jackson-databind:${Versions.jacksonDatabindVersion}"
    const val jacksonAnnotation = "com.fasterxml.jackson.core:jackson-annotations:${Versions.jacksonVersion}"
    const val jacksonDatatypeJdk8 = "com.fasterxml.jackson.datatype:jackson-datatype-jdk8:${Versions.jacksonVersion}"
    const val jacksonDatatypeJsr310 = "com.fasterxml.jackson.datatype:jackson-datatype-jsr310:${Versions.jacksonVersion}"
    const val jacksonModuleParameterNames = "com.fasterxml.jackson.module:jackson-module-parameter-names:${Versions.jacksonVersion}"
    const val jacksonDataformatXml = "com.fasterxml.jackson.dataformat:jackson-dataformat-xml:${Versions.jacksonVersion}"
    const val jacksonModuleKotlin = "com.fasterxml.jackson.module:jackson-module-kotlin:${Versions.jacksonKotlinVersion}"
}

object JjwtLibs {
    object Versions {
        const val jjwtVersion = "0.10.7"
    }

    const val jjwtApi = "io.jsonwebtoken:jjwt-api:${Versions.jjwtVersion}"
    const val jjwtImpl = "io.jsonwebtoken:jjwt-impl:${Versions.jjwtVersion}"
    const val jjwtJackson = "io.jsonwebtoken:jjwt-jackson:${Versions.jjwtVersion}"
}


object JasperReportsLibs {
    object Versions {
        // Обновлено до 6.20.6 для устранения уязвимостей безопасности (версия 1.3.7)
        // Последняя версия, совместимая с Java 8
        const val jasperReportsVersion = "6.20.6"
        const val bcelVersion = "5.2"
        const val bshVersion = "2.1b5"
        const val groovyAllVersion = "2.4.8"
        const val jaxenVersion = "1.1.1"
        const val pngEncoderVersion = "1.5"
        const val poiVersion = "5.1.0"
        const val saajApiVersion = "1.3"
    }

    const val jasperReports = "net.sf.jasperreports:jasperreports:${Versions.jasperReportsVersion}"
    const val bcel = "org.apache.bcel:bcel:${Versions.bcelVersion}"
    const val bsh = "org.beanshell:bsh:${Versions.bshVersion}"
    const val groovyAll = "org.codehaus.groovy:groovy-all:${Versions.groovyAllVersion}"
    const val jaxen = "jaxen:jaxen:${Versions.jaxenVersion}"
    const val pngEncoder = "com.keypoint:png-encoder:${Versions.pngEncoderVersion}"
    const val poi = "org.apache.poi:poi:${Versions.poiVersion}"
    const val saajApi = "javax.xml.soap:saaj-api:${Versions.saajApiVersion}"
}

object Log4JLibs {
    object Versions {
        const val log4jVersion = "2.23.1"
        const val slf4jVersion = "2.0.9"
    }

    const val log4jApi = "org.apache.logging.log4j:log4j-api:${Versions.log4jVersion}"
    const val log4jCore = "org.apache.logging.log4j:log4j-core:${Versions.log4jVersion}"
    const val log4j12Api = "org.apache.logging.log4j:log4j-1.2-api:${Versions.log4jVersion}"
    const val log4jSlf4jImpl = "org.apache.logging.log4j:log4j-slf4j-impl:${Versions.log4jVersion}"
    const val slf4jApi = "org.slf4j:slf4j-api:${Versions.slf4jVersion}"
}

object JMockLibs {
    object Versions {
        const val jmockVersion = "2.5.1"
        const val objenesisVersion = "1.0"
        const val cglibNodepVersion = "2.1_3"
    }

    const val jmock = "org.jmock:jmock:${Versions.jmockVersion}"
    const val jmockJunit4 = "org.jmock:jmock-junit4:${Versions.jmockVersion}"
    const val jmockLegacy = "org.jmock:jmock-legacy:${Versions.jmockVersion}"
    const val objenesis = "org.objenesis:objenesis:${Versions.objenesisVersion}"
    const val cglibNodep = "cglib:cglib-nodep:${Versions.cglibNodepVersion}"
}

object MockitoLibs {
    object Versions {
        // Mockito 4.11.0 - последняя версия, поддерживающая Java 8
        // Mockito 5.x требует Java 11+
        const val mockitoCoreVersion = "4.11.0"
        const val byteBuddyVersion = "1.14.11"
        const val objenesisVersion = "3.3"
    }

    const val mockitoCore = "org.mockito:mockito-core:${Versions.mockitoCoreVersion}"
    const val byteBuddy = "net.bytebuddy:byte-buddy:${Versions.byteBuddyVersion}"
    const val byteBuddyAgent = "net.bytebuddy:byte-buddy-agent:${Versions.byteBuddyVersion}"
    const val objenesis = "org.objenesis:objenesis:${Versions.objenesisVersion}"
}

object XStreamLibs {
    object Versions {
        const val xpp3MinVersion = "1.1.4c"
        const val xmlpullVersion = "1.1.3.1"
        // Обновлено до 1.4.23 для устранения уязвимостей десериализации (версия 1.3.7)
        // Последняя версия, совместимая с Java 8
        const val xstreamVersion = "1.4.23"
        const val hamcrestAllVersion = "1.1"
    }

    const val xpp3Min = "xpp3:xpp3_min:${Versions.xpp3MinVersion}"
    const val xmlpull = "xmlpull:xmlpull:${Versions.xmlpullVersion}"
    const val xstream = "com.thoughtworks.xstream:xstream:${Versions.xstreamVersion}"
    const val hamcrestAll = "org.hamcrest:hamcrest-all:${Versions.hamcrestAllVersion}"
}

object Tomcat {
    private const val version = "9.0.83"
    private const val group = "org.apache.tomcat"
    const val catalinaHa = "$group:tomcat-catalina-ha:$version"
    const val catalina = "$group:tomcat-catalina:$version"
    const val jdbc = "$group:tomcat-jdbc:$version"
    const val jasper = "$group:tomcat-jasper:$version"
    const val embedWebsocket = "$group.embed:tomcat-embed-websocket:$version"
    const val dbcp = "$group:tomcat-dbcp:$version"
    const val catalinaAnt = "$group:tomcat-catalina-ant:$version"
    const val websocketApi = "$group:tomcat-websocket-api:$version"
    const val ecj = "org.eclipse.jdt.core.compiler:ecj:4.4.2"
}

object JFreeChartLibs {
    const val jfreechart = "org.jfree:jfreechart:1.0.19.0-patched"
    const val jcommon = "org.jfree:jcommon:1.0.23_fork"
    const val itext = "com.lowagie:itext:2.1.7"
}

object JideLibs {

    const val jideDock = "../libs/jide-3.6.8/jide-dock.jar"
    const val jideDiff = "../libs/jide-3.6.8/jide-diff.jar"
    const val jideProperties = "../libs/jide-3.6.8/jide-properties.jar"
    const val jideDashboard = "../libs/jide-3.6.8/jide-dashboard.jar"
    const val jideCommon = "../libs/jide-3.6.8/jide-common.jar"
    const val jideComponents = "../libs/jide-3.6.8/jide-components.jar"
    const val jideEditor = "../libs/jide-3.6.8/jide-editor.jar"
    const val jideGrids = "../libs/jide-3.6.8/jide-grids.jar"
    const val jideShortcut = "../libs/jide-3.6.8/jide-shortcut.jar"
}

object BatikLibs {
    object Versions {
        const val batikVersion = "1.17"
        const val batikJsVersion = "1.8"
        const val xmlApisExtVersion = "1.3.04"
        const val xmlApisVersion = "1.4.01"
        const val xmlGraphicsVersion = "2.9"
    }

    const val batikTranscoder = "org.apache.xmlgraphics:batik-transcoder:${Versions.batikVersion}"
    const val batikCodec = "org.apache.xmlgraphics:batik-codec:${Versions.batikVersion}"
    const val batikSwing = "org.apache.xmlgraphics:batik-swing:${Versions.batikVersion}"
    const val batikAnim = "org.apache.xmlgraphics:batik-anim:${Versions.batikVersion}"
    const val batikCss = "org.apache.xmlgraphics:batik-css:${Versions.batikVersion}"
    const val batikJs = "org.apache.xmlgraphics:batik-js:${Versions.batikJsVersion}"
    const val batikBridge = "org.apache.xmlgraphics:batik-bridge:${Versions.batikVersion}"
    const val batikDom = "org.apache.xmlgraphics:batik-dom:${Versions.batikVersion}"
    const val batikParser = "org.apache.xmlgraphics:batik-parser:${Versions.batikVersion}"
    const val batikUtil = "org.apache.xmlgraphics:batik-util:${Versions.batikVersion}"
    const val batikXml = "org.apache.xmlgraphics:batik-xml:${Versions.batikVersion}"
    const val batikScript = "org.apache.xmlgraphics:batik-script:${Versions.batikVersion}"
    const val batikSvgDom = "org.apache.xmlgraphics:batik-svg-dom:${Versions.batikVersion}"
    const val batikExt = "org.apache.xmlgraphics:batik-ext:${Versions.batikVersion}"
    const val batikExtension = "org.apache.xmlgraphics:batik-extension:${Versions.batikVersion}"
    const val batikGuiUtil = "org.apache.xmlgraphics:batik-gui-util:${Versions.batikVersion}"
    const val batikConstants = "org.apache.xmlgraphics:batik-constants:${Versions.batikVersion}"
    const val batiki18n = "org.apache.xmlgraphics:batik-i18n:${Versions.batikVersion}"
    const val batikGvt = "org.apache.xmlgraphics:batik-gvt:${Versions.batikVersion}"
    const val batikSvgGen = "org.apache.xmlgraphics:batik-svggen:${Versions.batikVersion}"
    const val batikAwtUtil = "org.apache.xmlgraphics:batik-awt-util:${Versions.batikVersion}"
    const val xmlGraphics = "org.apache.xmlgraphics:xmlgraphics-commons:${Versions.xmlGraphicsVersion}"
    const val xmlApisExt = "xml-apis:xml-apis-ext:${Versions.xmlApisExtVersion}"
    const val xmlApis = "xml-apis:xml-apis:${Versions.xmlApisVersion}"
}

object JungLibs {
    object Versions {
        const val jungVersion = "2.1.1"
        const val jungCollectionGenericVersion = "4.01"
        const val jungAlgorithmsVersion = "2.1.1"
    }

    const val jungGraphImpl = "net.sf.jung:jung-graph-impl:${Versions.jungVersion}"
    const val jungApi = "net.sf.jung:jung-api:${Versions.jungVersion}"
    const val jungVisualization = "net.sf.jung:jung-visualization:${Versions.jungVersion}"
    const val jungIo = "net.sf.jung:jung-io:${Versions.jungVersion}"
    const val jungAlgorithms = "net.sf.jung:jung-algorithms:${Versions.jungAlgorithmsVersion}"
    const val jungCollectionsGeneric = "net.sourceforge.collections:collections-generic:${Versions.jungCollectionGenericVersion}"
}

object IoNetty {
    object Versions {
        const val nettyAllVersion = "4.1.86.Final"
    }
    const val nettyAll = "io.netty:netty-all:${Versions.nettyAllVersion}"
}

object ApacheCassandra {
    object Versions {
        const val apacheCassandraVersion = "3.11.15"
        const val apacheCassandraThriftVersion = "3.11.15"
        const val cassandraDriverCoreVersion = "3.4.0"
    }

    const val apacheCassandra = "org.apache.cassandra:cassandra-all:${Versions.apacheCassandraVersion}"
    const val apacheCassandraThrift = "org.apache.cassandra:cassandra-thrift:${Versions.apacheCassandraThriftVersion}"
    const val cassandraDriverCore = "com.datastax.cassandra:cassandra-driver-core:${Versions.cassandraDriverCoreVersion}"
}

object Neo4j {
    object Versions {
        const val neo4j = "4.4.21"
    }

    const val neo4jCollections = "org.neo4j:neo4j-collections:${Versions.neo4j}"
    const val neo4jCommon = "org.neo4j:neo4j-common:${Versions.neo4j}"
    const val neo4jConfigurations = "org.neo4j:neo4j-configuration:${Versions.neo4j}"
    const val neo4jCsv = "org.neo4j:neo4j-csv:${Versions.neo4j}"
    const val neo4jGraphdb = "org.neo4j:neo4j-graphdb-api:${Versions.neo4j}"
    const val neo4jIndex = "org.neo4j:neo4j-index:${Versions.neo4j}"
    const val neo4jIo = "org.neo4j:neo4j-io:${Versions.neo4j}"
    const val neo4jKernel = "org.neo4j:neo4j-kernel:${Versions.neo4j}"
    const val neo4jLogging = "org.neo4j:neo4j-logging:${Versions.neo4j}"
    const val neo4jLuceneIndex = "org.neo4j:neo4j-lucene-index:${Versions.neo4j}"
    const val neo4jResource = "org.neo4j:neo4j-resource:${Versions.neo4j}"
    const val neo4jSsl = "org.neo4j:neo4j-ssl:${Versions.neo4j}"
    const val neo4jUnsafe = "org.neo4j:neo4j-unsafe:${Versions.neo4j}"
}

object GoogleLibs {
    object Versions {
        const val guavaVersion = "32.1.3-jre"
    }

    const val guava = "com.google.guava:guava:${Versions.guavaVersion}"
}

object TestContainers {
    const val group = "org.testcontainers"
    const val version = "1.19.3"

    const val forJUnit5 = "org.testcontainers:junit-jupiter:$version"

    fun gav(module: String) = "$group:$module:$version"
}

object JMHLibs {
    object Versions {
        // JMH 1.36 - последняя версия, поддерживающая Java 8
        // JMH 1.37+ требует Java 11+
        const val jmhVersion = "1.36"
    }

    const val jmhCore = "org.openjdk.jmh:jmh-core:${Versions.jmhVersion}"
    const val jmhGeneratorAnnProcess = "org.openjdk.jmh:jmh-generator-annprocess:${Versions.jmhVersion}"
}