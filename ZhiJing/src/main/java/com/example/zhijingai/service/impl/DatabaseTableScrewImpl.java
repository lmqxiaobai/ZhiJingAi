package com.example.zhijingai.service.impl;

import cn.smallbun.screw.core.Configuration;
import cn.smallbun.screw.core.engine.EngineConfig;
import cn.smallbun.screw.core.engine.EngineFileType;
import cn.smallbun.screw.core.engine.EngineTemplateType;
import cn.smallbun.screw.core.execute.DocumentationExecute;
import cn.smallbun.screw.core.process.ProcessConfig;
import com.example.zhijingai.service.DatabaseTableScrew;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.ArrayList;

@Service
public class DatabaseTableScrewImpl implements DatabaseTableScrew {

    /**
     * MySQL驱动
     */
    @Value("${spring.datasource.driver-class-name}")
    private String Driver_Class_Name;
    /**
     * 数据库URL   characterEncoding=UTF-8: 防止生成后文档乱码
     */
    @Value("${spring.datasource.url}")
    private String DB_URL;
    /**
     * MySQL数据库账号密码
     */
    @Value("${spring.datasource.username}")
    private String DB_USERNAME = "root";
    @Value("${spring.datasource.password}")
    private String DB_PASSWORD = "root";

    /**
     *  生成数据库文档文件路径 可根据本机电脑自行配置
     */
    @Value("${FILE_OUTPUT_DIR}")
    private String FILE_OUTPUT_DIR;

    /**
     * 版本
     */
    @Value("${DOC_VERSION}")
    private String DOC_VERSION;

    /**
     * screw配置的文件类型 HTML->HTML文件  WORD->WORD文件  MD->Markdown文件
     */
    private static final EngineFileType FILE_OUTPUT_TYPE = EngineFileType.WORD;
    /**
     * screw配置的文件名称，即数据库文档名称
     */
    @Value("${DOC_FILE_NAME}")
    private String DOC_FILE_NAME;
    /**
     * 描述
     */
    @Value("${DOC_DESCRIPTION}")
    private String DOC_DESCRIPTION;

    /**
     * 生成 数据库表结构文档
     */
    @Override
    public void generatorDocument() {

        //获取数据源 此方法报No qualifying bean of type 'javax.sql.DataSource' available
//        DataSource dataSource = applicationContext.getBean(DataSource.class);

        //获取数据源  创建HikariConfig配置类
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setDriverClassName(Driver_Class_Name);
        hikariConfig.setJdbcUrl(DB_URL);
        hikariConfig.setUsername(DB_USERNAME);
        hikariConfig.setPassword(DB_PASSWORD);
        //#设置useInformationSchema 可以获取tables表注释信息 即解决数据库表和列字段有说明、生成文档没有说明
        hikariConfig.addDataSourceProperty("useInformationSchema", "true");
        hikariConfig.setMinimumIdle(2);
        hikariConfig.setMaximumPoolSize(5);
        DataSource dataSource = new HikariDataSource(hikariConfig);

        //创建screw的引擎配置
        EngineConfig engineConfig = EngineConfig.builder()
                //生成文件路径
                .fileOutputDir(FILE_OUTPUT_DIR)
                //打开目录
                .openOutputDir(true)
                //文件类型  HTML->HTML文件  WORD->WORD文件  MD->Markdown文件
                .fileType(FILE_OUTPUT_TYPE)
                //生成模板实现
                .produceType(EngineTemplateType.freemarker)
                //自定义文件名称，即数据库文档名称
                .fileName(DOC_FILE_NAME).build();

        //创建screw的配置:生成文档配置（包含以下自定义版本号、描述等配置连接）
        Configuration config = Configuration.builder()
                //版本
                .version(DOC_VERSION)
                //描述
                .description(DOC_DESCRIPTION)
                //数据源
                .dataSource(dataSource)
                //生成配置
                .engineConfig(engineConfig)
                //生成配置
                .produceConfig(getProcessConfig())
                .build();
        //执行screw，生成数据库文档
        new DocumentationExecute(config).execute();
    }

    /**
     * 配置想要生成的表、想要忽略的表
     * @return
     */
    private ProcessConfig getProcessConfig() {

        //创建screw的处理配置，可忽略
        //忽略表
        ArrayList<String> ignoreTableName = new ArrayList<>();
        ignoreTableName.add("test_user");
        ignoreTableName.add("test_group");
        //忽略表前缀，如忽略test_开头的数据库表
        ArrayList<String> ignorePrefix = new ArrayList<>();
        ignorePrefix.add("test_");
        //忽略表后缀
        ArrayList<String> ignoreSuffix = new ArrayList<>();
        ignoreSuffix.add("_test");

        // 需要生成数据库文档的表  如果designatedTablePrefix设置值不为空，则 designatedTableName 中的表名要去掉前缀，不然会重复生成，并且表名和字段注释有误
        ArrayList<String> designatedTableName = new ArrayList<>();
//        designatedTableName.add("user");
//        designatedTableName.add("product");
        // 需要生成数据库文档的表前缀
        ArrayList<String> designatedTablePrefix = new ArrayList<>();
        designatedTablePrefix.add("");

        //指定生成逻辑、当存在指定表、指定表前缀、指定表后缀时，将生成指定表，其余表不生成、并跳过忽略表配置
        ProcessConfig processConfig = ProcessConfig.builder()
                //根据名称指定表生成
//                .designatedTableName(new ArrayList<>())
                .designatedTableName(designatedTableName)
                //根据表前缀生成
//                .designatedTablePrefix(new ArrayList<>())
                .designatedTablePrefix(designatedTablePrefix)
                //根据表后缀生成
                .designatedTableSuffix(new ArrayList<>())
                //忽略表名
                .ignoreTableName(ignoreTableName)
                //忽略表前缀
                .ignoreTablePrefix(ignorePrefix)
                //忽略表后缀
                .ignoreTableSuffix(ignoreSuffix).build();
        return processConfig;
    }
}
