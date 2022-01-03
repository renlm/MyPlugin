package cn.renlm.plugins;

import static com.baomidou.mybatisplus.core.toolkit.StringPool.SLASH;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.GlobalConfig;
import com.baomidou.mybatisplus.generator.config.InjectionConfig;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.config.PackageConfig;
import com.baomidou.mybatisplus.generator.config.StrategyConfig;
import com.baomidou.mybatisplus.generator.config.TemplateConfig;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import cn.hutool.core.util.ClassUtil;
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
	static final String excelXmlSuffix 			= ".excel.xml";
	static final String mapperOutputDir 		= ConstVal.resourcesDir + "/mapper";
	static final String excelXmlOutputDir 		= ConstVal.resourcesDir + "/excel";
	static final String excelXmlTemplatePath 	= "config/Excel.xml.ftl";
	static final String serviceImplTemplatePath = "config/ServiceImpl.java";
	static final String dSClassName 			= DS.class.getName();

	/**
	 * 读取配置并运行
	 * 
	 * @param xml
	 */
	public static final void run(String xml) {
		GeneratorConfig conf = MyXStreamUtil.read(GeneratorConfig.class, xml);
		DataSourceConfig dsc = new DataSourceConfig.Builder(conf.url, conf.username, conf.password).build();
		conf.modules.forEach(module -> {
			module.tables.forEach(table -> {
				create(conf, dsc, module.pkg, module.name, table);
			});
		});
	}

	private static final void create(GeneratorConfig conf, DataSourceConfig dsc, String pkg, String moduleName,
			GeneratorTable table) {
		AutoGenerator autoGenerator = new AutoGenerator(dsc);
		autoGenerator.injection(injectionConfig(conf));
		autoGenerator.template(templateConfig());
		autoGenerator.strategy(strategyConfig(table));
		autoGenerator.packageInfo(packageConfig(pkg, moduleName));
		autoGenerator.global(globalConfig(table));
		autoGenerator.execute();

	}

	/**
	 * 注入配置
	 * 
	 * @param conf
	 * @return
	 */
	private static final InjectionConfig injectionConfig(GeneratorConfig conf) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("nameOfDS", ClassUtil.loadClass(dSClassName).getName());
		map.put("dsName", conf.dsName);
		
		Map<String, String> customFile = new HashMap<>();
		customFile.put(excelXmlSuffix, excelXmlTemplatePath);
		
		return new InjectionConfig.Builder()
				.customMap(map)
				.customFile(customFile)
				.build();
	}

	/**
	 * 模板配置
	 */
	private static final TemplateConfig templateConfig() {
		return new TemplateConfig.Builder()
				.serviceImpl(serviceImplTemplatePath)
				.controller(null)
				.build();
	}

	/**
	 * 策略配置
	 */
	private static final StrategyConfig strategyConfig(GeneratorTable table) {
		return new StrategyConfig.Builder()
				.addInclude(table.name)
				.entityBuilder()
				.idType(StrUtil.isBlank(table.idType) ? IdType.AUTO : IdType.valueOf(table.idType))
				.enableTableFieldAnnotation()
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
		@XStreamAlias("entity")
		private boolean coverEntity;

		/**
		 * 是否配置Excel表格（默认否）
		 */
		@XStreamAsAttribute
		@XStreamAlias("excel")
		private boolean configExcel;

	}
}