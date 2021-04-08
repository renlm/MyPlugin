package cn.renlm.plugins;

import static com.baomidou.mybatisplus.core.toolkit.StringPool.DOT_XML;
import static com.baomidou.mybatisplus.core.toolkit.StringPool.SLASH;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.InjectionConfig;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.FileOutConfig;
import com.baomidou.mybatisplus.generator.config.GlobalConfig;
import com.baomidou.mybatisplus.generator.config.IFileCreate;
import com.baomidou.mybatisplus.generator.config.PackageConfig;
import com.baomidou.mybatisplus.generator.config.StrategyConfig;
import com.baomidou.mybatisplus.generator.config.TemplateConfig;
import com.baomidou.mybatisplus.generator.config.builder.ConfigBuilder;
import com.baomidou.mybatisplus.generator.config.converts.PostgreSqlTypeConvert;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.baomidou.mybatisplus.generator.config.rules.DbColumnType;
import com.baomidou.mybatisplus.generator.config.rules.FileType;
import com.baomidou.mybatisplus.generator.config.rules.IColumnType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import cn.hutool.core.util.ClassUtil;
import cn.renlm.plugins.MyUtil.MyXStreamUtil;
import lombok.Data;

/**
 * 代码生成封装类
 * 	spring-boot
 * 		2.3.2.RELEASE
 * 	mybatis-plus-boot-starter
 * 	mybatis-plus-generator
 * 		3.4.1
 *  dynamic-datasource-spring-boot-starter
 *  	3.3.1
 *  freemarker
 *  	2.3.30
 * 
 * @author Renlm
 *
 */
public class MyGeneratorUtil {
	static final String mapperSuffix 			= "Mapper";
	static final String excelXmlSuffix 			= ".excel.xml";
	static final String mapperOutputDir 		= ConstVal.resourcesDir + "/mapper";
	static final String excelXmlOutputDir 		= ConstVal.resourcesDir + "/excel";
	static final String mapperTemplatePath		= "/templates/mapper.xml.ftl";
	static final String excelXmlTemplatePath	= "config/Excel.xml.ftl";
	static final String serviceImplTemplatePath	= "config/ServiceImpl.java";
	static final String dSClassName 			= "com.baomidou.dynamic.datasource.annotation.DS";

	/**
	 * 读取配置并运行
	 * 
	 * @param xml
	 */
	public static final void run(String xml) {
		GeneratorConfig conf = MyXStreamUtil.read(GeneratorConfig.class, xml);
		DataSourceConfig dsc = new DataSourceConfig()
				.setUrl(conf.url)
				.setUsername(conf.username)
				.setPassword(conf.password)
				.setDriverName(conf.driverName);
		DbType dbType = dsc.getDbType();
		if (dbType == DbType.POSTGRE_SQL) {
			dsc.setTypeConvert(new PostgreSqlTypeConvert() {
				@Override
				public IColumnType processTypeConvert(GlobalConfig globalConfig, String fieldType) {
					if (fieldType.toLowerCase().contains("bytea")) {
						return DbColumnType.BYTE_ARRAY;
					}
					return super.processTypeConvert(globalConfig, fieldType);
				}

			});
		}
		conf.modules.forEach(module -> {
			module.tables.forEach(table -> {
				create(conf, dsc, module.pkg, module.name, table);
			});
		});
	}

	private static final void create(GeneratorConfig conf, DataSourceConfig dsc, String pkg, String moduleName,
			GeneratorTable table) {
		AutoGenerator mpg = new AutoGenerator();
		mpg.setTemplateEngine(new FreemarkerTemplateEngine());

		// 全局配置
		GlobalConfig gc = new GlobalConfig();
		gc.setOutputDir(ConstVal.javaDir);
		gc.setAuthor(table.author);
		gc.setOpen(false);
		gc.setIdType(table.idType == null ? IdType.AUTO : IdType.valueOf(table.idType));
		gc.setDateType(DateType.ONLY_DATE);
		mpg.setGlobalConfig(gc);

		// 数据源配置
		mpg.setDataSource(dsc);

		// 包配置
		PackageConfig pc = new PackageConfig();
		pc.setModuleName(moduleName);
		pc.setParent(pkg);
		mpg.setPackageInfo(pc);

		// 自定义配置
		InjectionConfig cfg = new InjectionConfig() {
			@Override
			public void initMap() {
				Map<String, Object> map = new HashMap<String, Object>();
				Class<?> dsClass = null;
				try {
					dsClass = ClassUtil.loadClass(dSClassName);
					map.put("nameOfDS", dsClass.getName());
					map.put("dsName", conf.dsName);
				} catch(Exception e) {}
				this.setMap(map);
			}
		};

		// 自定义输出配置
		List<FileOutConfig> focList = new ArrayList<>();
		// 自定义配置会被优先输出
		focList.add(new FileOutConfig(mapperTemplatePath) {
			@Override
			public String outputFile(TableInfo tableInfo) {
				// 自定义输出文件名 ， 如果你 Entity 设置了前后缀、此处注意 xml 的名称会跟着发生变化！！
				return mapperOutputDir + SLASH 
						+ pc.getModuleName() + SLASH 
						+ tableInfo.getEntityName() + mapperSuffix + DOT_XML;
			}
		});
		focList.add(new FileOutConfig(excelXmlTemplatePath) {
			@Override
			public String outputFile(TableInfo tableInfo) {
				return excelXmlOutputDir + SLASH 
						+ pc.getModuleName() + SLASH 
						+ tableInfo.getEntityName() + excelXmlSuffix;
			}
		});
		cfg.setFileCreate(new IFileCreate() {
			@Override
			public boolean isCreate(ConfigBuilder configBuilder, FileType fileType, String filePath) {
				if (filePath.endsWith(excelXmlSuffix)
						&& !table.configExcel) {
					return false;
				}
				this.checkDir(filePath);
				return (fileType == FileType.ENTITY && table.coverEntity) 
						|| !new File(filePath).exists();
			}

			@Override
			public void checkDir(String filePath) {
				File file = new File(filePath);
				boolean exist = file.exists();
				if (!exist) {
					file.getParentFile().mkdirs();
				}
			}
		});
		cfg.setFileOutConfigList(focList);
		mpg.setCfg(cfg);

		// 配置模板
		TemplateConfig templateConfig = new TemplateConfig();

		templateConfig.setXml(null);
		templateConfig.setServiceImpl(serviceImplTemplatePath);
		templateConfig.setController(null);
		mpg.setTemplate(templateConfig);

		// 策略配置
		StrategyConfig strategy = new StrategyConfig();
		strategy.setNaming(NamingStrategy.underline_to_camel);
		strategy.setColumnNaming(NamingStrategy.underline_to_camel);
		strategy.setEntityLombokModel(true);
		strategy.setEntityTableFieldAnnotationEnable(true);
		strategy.setInclude(table.name);
		mpg.setStrategy(strategy);
		mpg.execute();
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
		 * 数据源-驱动
		 */
		private String driverName;

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
		@XStreamAlias("cover-entity")
		private boolean coverEntity;

		/**
		 * 是否生成Excel表格配置（默认否）
		 */
		@XStreamAsAttribute
		@XStreamAlias("config-excel")
		private boolean configExcel;

	}
}