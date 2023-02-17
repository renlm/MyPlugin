package cn.renlm.plugins;

import static cn.renlm.plugins.MyGeneratorConf.entityPlaceholder;
import static com.baomidou.mybatisplus.core.toolkit.StringPool.SLASH;

import java.io.File;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.sql.Types;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.jetbrains.annotations.NotNull;

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
import com.baomidou.mybatisplus.generator.config.po.TableField;
import com.baomidou.mybatisplus.generator.config.po.TableField.MetaInfo;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.baomidou.mybatisplus.generator.config.rules.IColumnType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import com.baomidou.mybatisplus.generator.type.ITypeConvertHandler;
import com.baomidou.mybatisplus.generator.type.TypeRegistry;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import cn.renlm.plugins.MyGeneratorConf._Controller;
import cn.renlm.plugins.MyGeneratorConf._Entity;
import cn.renlm.plugins.MyGeneratorConf._JavaSqlType;
import cn.renlm.plugins.MyGeneratorConf._Mapper;
import cn.renlm.plugins.MyGeneratorConf._PackageConfig;
import cn.renlm.plugins.MyGeneratorConf._Service;
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
 * @author RenLiMing(任黎明)
 *
 */
public class MyGeneratorUtil {
	private static final String excelXmlName 			= "excel.xml";
	private static final String mapperOutputDir 		= ConstVal.resourcesDir + "/mapper";
	private static final String excelXmlOutputDir 		= ConstVal.resourcesDir + "/excel";
	private static final String excelXmlTemplatePath 	= "config/Excel.xml.ftl";
	private static final String EntityTemplatePath 		= "config/Entity.java";
	private static final String serviceImplTemplatePath	= "config/ServiceImpl.java";
	private static final String DS_CLASS_NAME 			= "com.baomidou.dynamic.datasource.annotation.DS";

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
		autoGenerator.template(templateConfig(conf));
		autoGenerator.strategy(strategyConfig(conf, table));
		autoGenerator.packageInfo(packageConfig(conf, module));
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
				.typeConvertHandler(new ITypeConvertHandler() {
					
					@Override
					public @NotNull IColumnType convert(GlobalConfig globalConfig, TypeRegistry typeRegistry, MetaInfo metaInfo) {
						IColumnType type = conf.getColumnType(metaInfo);
						if (type == null) {
							return typeRegistry.getColumnType(metaInfo);
						} else {
							return type;
						}
					}
					
				})
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
		map.put("nameOfDS", DS_CLASS_NAME);
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
	 * @param conf
	 * @return
	 */
	private static final TemplateConfig templateConfig(GeneratorConfig conf) {
		TemplateConfig.Builder builder = new TemplateConfig.Builder()
				.entity(EntityTemplatePath)
				.serviceImpl(serviceImplTemplatePath);
		/** ================== 自定义配置项 Start ================== */
		boolean enableController = conf.getConfig() != null 
				&& conf.getConfig().getTemplateConfig() != null
				&& conf.getConfig().getTemplateConfig().isEnableController();
		if (!enableController) {
			builder.disable(TemplateType.CONTROLLER);
		}
		/** ================== 自定义配置项 End ================== */
		return builder.build();
	}

	/**
	 * 策略配置
	 * 
	 * @param conf
	 * @param table
	 * @return
	 */
	private static final StrategyConfig strategyConfig(GeneratorConfig conf, GeneratorTable table) {
		Builder builder = new StrategyConfig.Builder()
				.addInclude(table.name)
				.entityBuilder()
				.idType(StrUtil.isBlank(table.idType) ? IdType.AUTO : IdType.valueOf(table.idType))
				.enableTableFieldAnnotation()
				.enableLombok()
				.enableChainModel()
				.naming(NamingStrategy.underline_to_camel)
				.columnNaming(NamingStrategy.underline_to_camel);
		
		/** ================== Entity 配置 Start ================== */
		boolean enableStrategyConfigEntity = conf.getConfig() != null 
				&& conf.getConfig().getStrategyConfig() != null 
				&& conf.getConfig().getStrategyConfig().getEntity() != null;
		if (enableStrategyConfigEntity) {
			_Entity entity = conf.getConfig().getStrategyConfig().getEntity();
			if (StrUtil.isNotBlank(entity.getFormatFileName())) {
				builder.formatFileName(entityPlaceholder(entity.getFormatFileName()));
			}
			if (entity.isDisableSerialVersionUID()) {
				builder.disableSerialVersionUID();
			}
			boolean enableSuperClass = StrUtil.isNotBlank(entity.getSuperClass());
			if (enableSuperClass) {
				builder.superClass(entity.getSuperClass());
				if (entity.getSuperEntityColumns() != null && CollUtil.isNotEmpty(entity.getSuperEntityColumns().getSuperEntityColumns())) {
					List<String> columns = entity.getSuperEntityColumns().getSuperEntityColumns().stream().map(it -> it.getText()).collect(Collectors.toList());
					CollUtil.removeBlank(columns);
					columns = CollUtil.distinct(columns);
					if (CollUtil.isNotEmpty(columns)) {
						builder.addSuperEntityColumns(columns);
					}
				}
			}
		}
		if (table.coverEntity) {
			builder.enableFileOverride();
		}
		/** ================== Entity 配置 End ================== */
		
		/** ================== Controller 配置 Start ================== */
		boolean enableStrategyConfigController = conf.getConfig() != null 
				&& conf.getConfig().getStrategyConfig() != null 
				&& conf.getConfig().getStrategyConfig().getController() != null;
		if (enableStrategyConfigController) {
			_Controller controller = conf.getConfig().getStrategyConfig().getController();
			if (StrUtil.isNotBlank(controller.getFormatFileName())) {
				builder.controllerBuilder().formatFileName(entityPlaceholder(controller.getFormatFileName()));
			}
		}
		/** ================== Controller 配置 End ================== */
		
		/** ================== Service 配置 Start ================== */
		boolean enableStrategyConfigService = conf.getConfig() != null 
				&& conf.getConfig().getStrategyConfig() != null 
				&& conf.getConfig().getStrategyConfig().getService() != null;
		if (enableStrategyConfigService) {
			_Service service = conf.getConfig().getStrategyConfig().getService();
			if (StrUtil.isNotBlank(service.getFormatServiceFileName())) {
				builder.serviceBuilder().formatServiceFileName(entityPlaceholder(service.getFormatServiceFileName()));
			}
			if (StrUtil.isNotBlank(service.getFormatServiceImplFileName())) {
				builder.serviceBuilder().formatServiceImplFileName(entityPlaceholder(service.getFormatServiceImplFileName()));
			}
		}
		/** ================== Service 配置 End ================== */
		
		/** ================== Mapper 配置 Start ================== */
		boolean enableStrategyConfigMapper = conf.getConfig() != null 
				&& conf.getConfig().getStrategyConfig() != null 
				&& conf.getConfig().getStrategyConfig().getMapper() != null;
		if (enableStrategyConfigMapper) {
			_Mapper mapper = conf.getConfig().getStrategyConfig().getMapper();
			if (StrUtil.isNotBlank(mapper.getFormatMapperFileName())) {
				builder.mapperBuilder().formatMapperFileName(entityPlaceholder(mapper.getFormatMapperFileName()));
			}
			if (StrUtil.isNotBlank(mapper.getFormatXmlFileName())) {
				builder.mapperBuilder().formatXmlFileName(entityPlaceholder(mapper.getFormatXmlFileName()));
			}
		}
		/** ================== Mapper 配置 End ================== */
		
		return builder.build();
	}

	/**
	 * 包配置
	 * 
	 * @param conf
	 * @param module
	 * @return
	 */
	private static final PackageConfig packageConfig(GeneratorConfig conf, GeneratorModule module) {
		Map<OutputFile, String> pathInfo = new HashMap<>();
		pathInfo.put(OutputFile.xml, mapperOutputDir + SLASH + module.name + SLASH);
		PackageConfig.Builder builder = new PackageConfig.Builder()
				.parent(module.pkg)
				.moduleName(module.name)
				.pathInfo(pathInfo);
		/** ================== 自定义配置项 Start ================== */
		if (conf.getConfig() != null && conf.getConfig().getPackageConfig() != null) {
			_PackageConfig packageConfig = conf.getConfig().getPackageConfig();
			if (StrUtil.isNotBlank(packageConfig.getController())) {
				builder.controller(packageConfig.getController());
			}
			if (StrUtil.isNotBlank(packageConfig.getServiceImpl())) {
				builder.serviceImpl(packageConfig.getServiceImpl());
			}
			if (StrUtil.isNotBlank(packageConfig.getService())) {
				builder.service(packageConfig.getService());
			}
			if (StrUtil.isNotBlank(packageConfig.getEntity())) {
				builder.entity(packageConfig.getEntity());
			}
			if (StrUtil.isNotBlank(packageConfig.getMapper())) {
				builder.mapper(packageConfig.getMapper());
			}
			if (StrUtil.isNotBlank(packageConfig.getXml())) {
				builder.xml(packageConfig.getXml());
				pathInfo.put(OutputFile.xml, ConstVal.resourcesDir + SLASH + packageConfig.getXml() + SLASH + module.name + SLASH);
			}
		}
		/** ================== 自定义配置项 End ================== */
		return builder.build();
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

		private final Map<Integer, IColumnType> typeMap = new HashMap<>();

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

		/**
		 * 获取自定义类型转换
		 * 
		 * @param metaInfo
		 * @return
		 */
		public IColumnType getColumnType(TableField.MetaInfo metaInfo) {
			if (metaInfo == null || metaInfo.getJdbcType() == null) {
				return null;
			}
			if (MapUtil.isEmpty(typeMap) && this.config != null && this.config.getTypeConvert() != null) {
				List<_JavaSqlType> javaSqlTypes = this.config.getTypeConvert().getJavaSqlTypes();
				if (CollUtil.isNotEmpty(javaSqlTypes)) {
					javaSqlTypes.forEach(type -> {
						if (StrUtil.isNotBlank(type.getName()) && StrUtil.isNotBlank(type.getType())) {
							Field field = ReflectUtil.getField(Types.class, type.getName());
							int javaSqlType = Convert.toInt(ReflectUtil.getStaticFieldValue(field));
							typeMap.put(javaSqlType, new IColumnType() {

								@Override
								public String getType() {
									return type.getType();
								}

								@Override
								public String getPkg() {
									return type.getPkg();
								}

							});
						}
					});
				}
			}
			int typeCode = metaInfo.getJdbcType().TYPE_CODE;
			return this.typeMap.get(typeCode);
		}

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
