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
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.baomidou.mybatisplus.generator.config.querys.PostgreSqlQuery;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import cn.renlm.plugins.MyUtil.MyXStreamUtil;
import lombok.Data;

/**
 * 代码生成封装类
 * 	spring-boot
 * 		2.5.3 
 *  mybatis-plus-boot-starter 
 *  	3.4.3.4
 *  dynamic-datasource-spring-boot-starter 
 *  	3.5.0 
 *  mybatis-plus-generator 
 *  	3.5.1
 *  freemarker 
 *  	2.3.31
 * 
 * @author Renlm
 *
 */
public class MyGeneratorUtil {
	private static final String excelXmlName 			= "excel.xml";
	private static final String mapperOutputDir 		= ConstVal.resourcesDir + "/mapper";
	private static final String otherOutputDir 			= ConstVal.resourcesDir + "/excel";
	private static final String excelXmlTemplatePath 	= "config/Excel.xml.ftl";
	private static final String EntityTemplatePath 		= "config/Entity.java";
	private static final String serviceImplTemplatePath = "config/ServiceImpl.java";

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
					create(conf, dataSourceConfig(conf, table.getSchema()), module.pkg, module.name, table);
				} else {
					create(conf, dsc, module.pkg, module.name, table);
				}
			});
		});
	}
	
	/**
	 * 获取数据库表信息
	 * 
	 * @param schema
	 * @param url
	 * @param username
	 * @param password
	 * @return
	 */
	public static final List<TableInfo> getTableInfoList(String schema, String url, String username, String password) {
		GeneratorConfig conf = new GeneratorConfig();
		conf.setUrl(url);
		conf.setUsername(username);
		conf.setPassword(password);
		DataSourceConfig dsc = dataSourceConfig(conf, schema);
		ConfigBuilder config = new ConfigBuilder(null, dsc, null, null, null, null);
		return config.getTableInfoList();
	}

	/**
	 * 生成代码
	 * 
	 * @param conf
	 * @param dsc
	 * @param pkg
	 * @param moduleName
	 * @param table
	 */
	private static final void create(GeneratorConfig conf, DataSourceConfig dsc, String pkg, String moduleName,
			GeneratorTable table) {
		AutoGenerator autoGenerator = new AutoGenerator(dsc);
		autoGenerator.injection(injectionConfig(conf, table));
		autoGenerator.template(templateConfig());
		autoGenerator.strategy(strategyConfig(table));
		autoGenerator.packageInfo(packageConfig(pkg, moduleName));
		autoGenerator.global(globalConfig(table));
		autoGenerator.execute(new FreemarkerTemplateEngine() {
			/**
			 * 是否强制覆盖实体类
			 */
			@Override
			protected void outputEntity(@NotNull TableInfo tableInfo, @NotNull Map<String, Object> objectMap) {
				GlobalConfig globalConfig = this.getConfigBuilder().getGlobalConfig();
				boolean fileOverride = globalConfig.isFileOverride();
				ReflectUtil.setFieldValue(globalConfig, "fileOverride", table.coverEntity ? table.coverEntity : fileOverride);
				super.outputEntity(tableInfo, objectMap);
				ReflectUtil.setFieldValue(globalConfig, "fileOverride", fileOverride);
			}

			/**
			 * 是否生成表格配置
			 */
			@Override
			protected void outputCustomFile(@NotNull Map<String, String> customFile, @NotNull TableInfo tableInfo,
					@NotNull Map<String, Object> objectMap) {
				if (table.configExcel) {
					String entityName = tableInfo.getEntityName();
					String otherPath = getPathInfo(OutputFile.other);
					customFile.forEach((key, value) -> {
						String fileName = otherPath + File.separator + entityName + StrUtil.DOT + key;
						outputFile(new File(fileName), objectMap, value);
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
				.schema(schema).build();
		DbType dbType = dataSourceConfig.getDbType();
		if (dbType == DbType.POSTGRE_SQL) {
			ReflectUtil.setFieldValue(dataSourceConfig, "dbQuery", new PostgreSqlQuery() {
				/**
				 * 修复主键查询BUG
				 */
				@Override
				public String tableFieldsSql() {
					return "SELECT A.attname AS name,format_type (A.atttypid,A.atttypmod) AS type,col_description (A.attrelid,A.attnum) AS comment,\n"
							+ "(CASE WHEN (SELECT COUNT (*) FROM pg_constraint AS PC WHERE PC.conrelid = C.oid AND A.attnum = PC.conkey[1] AND PC.contype = 'p') > 0 THEN 'PRI' ELSE '' END) AS key \n"
							+ "FROM pg_class AS C,pg_attribute AS A WHERE A.attrelid='\"%s\"'::regclass AND A.attrelid= C.oid AND A.attnum> 0 AND NOT A.attisdropped ORDER  BY A.attnum";
				}
			});
		}
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
		return new StrategyConfig.Builder()
				.addInclude(table.name)
				.entityBuilder()
				.idType(StrUtil.isBlank(table.idType) ? IdType.AUTO : IdType.valueOf(table.idType))
				.enableTableFieldAnnotation()
				.enableLombok()
				.enableChainModel()
				.naming(NamingStrategy.underline_to_camel)
				.columnNaming(NamingStrategy.underline_to_camel)
				.build();
	}

	/**
	 * 包配置
	 * 
	 * @param pkg
	 * @param moduleName
	 * @return
	 */
	private static final PackageConfig packageConfig(String pkg, String moduleName) {
		Map<OutputFile, String> pathInfo = new HashMap<>();
		pathInfo.put(OutputFile.mapperXml, mapperOutputDir + SLASH + moduleName + SLASH);
		pathInfo.put(OutputFile.other, otherOutputDir + SLASH + moduleName + SLASH);
		return new PackageConfig.Builder()
				.parent(pkg)
				.moduleName(moduleName)
				.pathInfo(pathInfo)
				.build();
	}

	/**
	 * 全局配置
	 * 
	 * @param table
	 * @return
	 */
	private static final GlobalConfig globalConfig(GeneratorTable table) {
		return new GlobalConfig.Builder()
				.outputDir(ConstVal.javaDir)
				.author(table.author)
				.disableOpenDir()
				.dateType(DateType.ONLY_DATE)
				.build();
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