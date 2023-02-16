package cn.renlm.plugins;

import static com.baomidou.mybatisplus.core.toolkit.StringPool.SLASH;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jetbrains.annotations.NotNull;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.GlobalConfig;
import com.baomidou.mybatisplus.generator.config.InjectionConfig;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.config.PackageConfig;
import com.baomidou.mybatisplus.generator.config.StrategyConfig;
import com.baomidou.mybatisplus.generator.config.TemplateConfig;
import com.baomidou.mybatisplus.generator.config.TemplateType;
import com.baomidou.mybatisplus.generator.config.builder.ConfigBuilder;
import com.baomidou.mybatisplus.generator.config.builder.CustomFile;
import com.baomidou.mybatisplus.generator.config.builder.Entity.Builder;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import cn.renlm.plugins.MyUtil.MyXStreamUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

/**
 * 代码生成封装类
 * 	spring-boot
 * 		2.5.3
 *  mybatis-plus-boot-starter
 *  	3.5.3.1
 *  dynamic-datasource-spring-boot-starter
 *  	3.6.1
 *  mybatis-plus-generator
 *  	3.5.3.1
 *  freemarker
 *  	2.3.31
 * 
 * @author Renlm
 *
 */
public class MyGeneratorUtil {
	private static final String excelXmlName 			= "excel.xml";
	private static final String mapperOutputDir 		= ConstVal.resourcesDir + "/mapper";
	private static final String excelXmlOutputDir 		= ConstVal.resourcesDir + "/excel";
	private static final String excelXmlTemplatePath 	= "config/Excel.xml.ftl";
	private static final String EntityTemplatePath 		= "config/Entity.java";
	private static final String serviceImplTemplatePath	= "config/ServiceImpl.java";

	/**
	 * 读取配置并运行
	 * 
	 * @param xml
	 */
	public static final void run(String xml) {
		GeneratorConfig conf = MyXStreamUtil.read(GeneratorConfig.class, xml);
		DataSourceConfig dsc = dataSourceConfig(conf, null);
		conf.modules.forEach(module -> {
			module.tables.forEach(table -> {
				if (StrUtil.isNotBlank(table.getSchema())) {
					create(conf, dataSourceConfig(conf, table.getSchema()), module, table);
				} else {
					create(conf, dsc, module, table);
				}
			});
		});
	}

	/**
	 * 查询数据库信息
	 * 
	 * @param schemaName
	 * @param url
	 * @param username
	 * @param password
	 * @return
	 */
	public static final DbInfo queryDbInfo(String schemaName, String url, String username, String password) {
		GeneratorConfig conf = new GeneratorConfig();
		conf.setUrl(url);
		conf.setUsername(username);
		conf.setPassword(password);
		DataSourceConfig dsc = dataSourceConfig(conf, schemaName);
		ConfigBuilder config = new ConfigBuilder(null, dsc, null, null, null, null);
		DbInfo dbInfo = new DbInfo(dsc.getDbType(), schemaName, config.getTableInfoList());
		return dbInfo;
	}

	/**
	 * 生成代码
	 * 
	 * @param conf
	 * @param dsc
	 * @param module
	 * @param table
	 */
	private static final void create(GeneratorConfig conf, DataSourceConfig dsc, GeneratorModule module,
			GeneratorTable table) {
		AutoGenerator autoGenerator = new AutoGenerator(dsc);
		autoGenerator.injection(injectionConfig(conf, table));
		autoGenerator.template(templateConfig());
		autoGenerator.strategy(strategyConfig(table));
		autoGenerator.packageInfo(packageConfig(module));
		autoGenerator.global(globalConfig(module, table));
		autoGenerator.execute(new FreemarkerTemplateEngine() {
			/**
			 * 是否生成表格配置
			 */
			@Override
			protected void outputCustomFile(@NotNull List<CustomFile> customFiles, @NotNull TableInfo tableInfo,
					@NotNull Map<String, Object> objectMap) {
				if (table.configExcel) {
					String entityName = tableInfo.getEntityName();
					String excelXmlPath = excelXmlOutputDir + SLASH + module.name + SLASH;
					customFiles.forEach(customFile -> {
						String fileName = excelXmlPath + entityName + StrUtil.DOT + customFile.getFileName();
						outputFile(new File(fileName), objectMap, customFile.getTemplatePath(), customFile.isFileOverride());
					});
				}
			}
		});
	}

	/**
	 * 数据源
	 * 
	 * @param conf
	 * @param schema
	 * @return
	 */
	private static final DataSourceConfig dataSourceConfig(GeneratorConfig conf, String schema) {
		DataSourceConfig dataSourceConfig = new DataSourceConfig.Builder(conf.url, conf.username, conf.password)
				.schema(schema)
				.build();
		return dataSourceConfig;
	}

	/**
	 * 注入配置
	 * 
	 * @param conf
	 * @param table
	 * @return
	 */
	private static final InjectionConfig injectionConfig(GeneratorConfig conf, GeneratorTable table) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("nameOfDS", DS.class.getName());
		map.put("dsName", conf.dsName);
		map.put("blobTypeHandler", !BooleanUtil.isFalse(table.getBlobTypeHandler()));

		Map<String, String> customFile = new HashMap<>();
		customFile.put(excelXmlName, excelXmlTemplatePath);

		return new InjectionConfig.Builder()
				.customMap(map)
				.customFile(customFile)
				.build();
	}

	/**
	 * 模板配置
	 * 
	 * @return
	 */
	private static final TemplateConfig templateConfig() {
		return new TemplateConfig.Builder()
				.entity(EntityTemplatePath)
				.serviceImpl(serviceImplTemplatePath)
				.disable(TemplateType.CONTROLLER)
				.build();
	}

	/**
	 * 策略配置
	 * 
	 * @param table
	 * @return
	 */
	private static final StrategyConfig strategyConfig(GeneratorTable table) {
		Builder builder = new StrategyConfig.Builder()
				.addInclude(table.name)
				.entityBuilder()
				.idType(StrUtil.isBlank(table.idType) ? IdType.AUTO : IdType.valueOf(table.idType))
				.enableTableFieldAnnotation()
				.enableLombok()
				.enableChainModel()
				.naming(NamingStrategy.underline_to_camel)
				.columnNaming(NamingStrategy.underline_to_camel);
		if (table.coverEntity) {
			builder.enableFileOverride();
		}
		return builder.build();
	}

	/**
	 * 包配置
	 * 
	 * @param module
	 * @return
	 */
	private static final PackageConfig packageConfig(GeneratorModule module) {
		Map<OutputFile, String> pathInfo = new HashMap<>();
		pathInfo.put(OutputFile.xml, mapperOutputDir + SLASH + module.name + SLASH);
		return new PackageConfig.Builder()
				.parent(module.pkg)
				.moduleName(module.name)
				.pathInfo(pathInfo)
				.build();
	}

	/**
	 * 全局配置
	 * 
	 * @param module
	 * @param table
	 * @return
	 */
	private static final GlobalConfig globalConfig(GeneratorModule module, GeneratorTable table) {
		GlobalConfig.Builder globalConfigBuilder = new GlobalConfig.Builder()
				.outputDir(ConstVal.javaDir)
				.author(table.author)
				.disableOpenDir()
				.dateType(DateType.ONLY_DATE);
		if (BooleanUtil.isTrue(module.isEnableSwagger())) {
			globalConfigBuilder.enableSwagger();
		}
		return globalConfigBuilder.build();
	}

	/**
	 * 数据库信息
	 */
	@Getter
	@AllArgsConstructor
	public static final class DbInfo implements Serializable {
		private static final long serialVersionUID = 1L;

		private final DbType dbType;

		private final String schemaName;

		private final List<TableInfo> tableInfoList;

	}

	/**
	 * 代码生成配置
	 */
	@Data
	@XStreamAlias("generator")
	public static final class GeneratorConfig implements Serializable {
		private static final long serialVersionUID = 1L;

		/**
		 * 数据源-名称（多数据源时使用）
		 */
		@XStreamAsAttribute
		private String dsName;

		/**
		 * 数据源-数据库地址
		 */
		private String url;

		/**
		 * 数据源-用户名
		 */
		private String username;

		/**
		 * 数据源-密码
		 */
		private String password;

		/**
		 * 基础配置
		 */
		private MyGeneratorConf config;

		/**
		 * 模块集
		 */
		@XStreamImplicit(itemFieldName = "module")
		private List<GeneratorModule> modules;

	}

	/**
	 * 模块
	 */
	@Data
	public static final class GeneratorModule implements Serializable {
		private static final long serialVersionUID = 1L;

		/**
		 * 模块名
		 */
		@XStreamAsAttribute
		private String name;

		/**
		 * 包路径
		 */
		@XStreamAsAttribute
		@XStreamAlias("package")
		private String pkg;

		/**
		 * 开启 swagger 模式
		 */
		@XStreamAsAttribute
		private boolean enableSwagger;

		/**
		 * 数据库表集
		 */
		@XStreamImplicit(itemFieldName = "table")
		private List<GeneratorTable> tables;

	}

	/**
	 * 数据库表
	 */
	@Data
	public static final class GeneratorTable implements Serializable {
		private static final long serialVersionUID = 1L;

		/**
		 * 表归属
		 */
		@XStreamAsAttribute
		private String schema;

		/**
		 * 创建人
		 */
		@XStreamAsAttribute
		private String author;

		/**
		 * 表名
		 */
		@XStreamAsAttribute
		private String name;

		/**
		 * 主键类型
		 */
		@XStreamAsAttribute
		private String idType;

		/**
		 * 是否覆盖已存在的实体类（默认否）
		 */
		@XStreamAsAttribute
		@XStreamAlias("entity")
		private boolean coverEntity;

		/**
		 * 是否配置Excel表格（默认否）
		 */
		@XStreamAsAttribute
		@XStreamAlias("excel")
		private boolean configExcel;

		/**
		 * 是否添加Blob字段转换（默认是）
		 */
		@XStreamAsAttribute
		private Boolean blobTypeHandler;

	}

}
