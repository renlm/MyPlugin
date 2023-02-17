package cn.renlm.plugins;

import java.io.Serializable;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import com.thoughtworks.xstream.converters.extended.ToAttributedValueConverter;

import cn.hutool.core.util.StrUtil;
import lombok.Data;

/**
 * 代码生成配置
 * 
 * @author RenLiMing(任黎明)
 *
 */
@Data
@XStreamAlias("config")
public class MyGeneratorConf {

	private static final String ENTITY_PLACEHOLDER = "{entityName}";

	@XStreamAlias("template")
	private _TemplateConfig templateConfig;

	@XStreamAlias("package")
	private _PackageConfig packageConfig;

	@XStreamAlias("strategy")
	private _StrategyConfig strategyConfig;

	/**
	 * 实体类名称占位符转换
	 * 
	 * @param format
	 * @return
	 */
	public static final String entityPlaceholder(String format) {
		return StrUtil.replace(format, ENTITY_PLACEHOLDER, "%s");
	}

	/**
	 * 模板配置
	 */
	@Data
	public static final class _TemplateConfig implements Serializable {

		private static final long serialVersionUID = 1L;

		/**
		 * 是否生成Controller（默认否）
		 */
		@XStreamAlias("controller")
		private boolean enableController;

	}

	/**
	 * 包配置
	 */
	@Data
	public static final class _PackageConfig implements Serializable {

		private static final long serialVersionUID = 1L;

		/**
		 * Controller包名
		 */
		private String controller = "controller";

		/**
		 * Service Impl包名
		 */
		private String serviceImpl = "service.impl";

		/**
		 * Service包名
		 */
		private String service = "service";

		/**
		 * Entity包名
		 */
		private String entity = "entity";

		/**
		 * Mapper包名
		 */
		private String mapper = "mapper";

		/**
		 * Mapper XML包名
		 */
		private String xml = "mapper";

	}

	/**
	 * 策略配置
	 */
	@Data
	public static final class _StrategyConfig implements Serializable {

		private static final long serialVersionUID = 1L;

		private _Entity entity;

		private _Controller controller;

		private _Service service;

		private _Mapper mapper;

	}

	/**
	 * Entity 配置
	 */
	@Data
	public static final class _Entity implements Serializable {

		private static final long serialVersionUID = 1L;

		/**
		 * Entity 格式化名称
		 */
		private String formatFileName = "{entityName}";

		/**
		 * 父类（全路径名称）
		 */
		private String superClass;

		/**
		 * 禁用生成serialVersionUID
		 */
		private boolean disableSerialVersionUID;

		/**
		 * 父类字段集
		 */
		private _SuperEntityColumns superEntityColumns;

	}

	@Data
	public static final class _SuperEntityColumns implements Serializable {

		private static final long serialVersionUID = 1L;

		@XStreamImplicit(itemFieldName = "column")
		private List<_Text> superEntityColumns;

	}

	@Data
	@XStreamConverter(value = ToAttributedValueConverter.class, strings = { "text" })
	public class _Text implements Serializable {

		private static final long serialVersionUID = 1L;

		private String text;

	}

	/**
	 * Controller 配置
	 */
	@Data
	public static final class _Controller implements Serializable {

		private static final long serialVersionUID = 1L;

		/**
		 * Controller 格式化名称
		 */
		private String formatFileName = "{entityName}Controller";

	}

	/**
	 * Service 配置
	 */
	@Data
	public static final class _Service implements Serializable {

		private static final long serialVersionUID = 1L;

		/**
		 * Service 格式化名称
		 */
		private String formatServiceFileName = "I{entityName}Service";

		/**
		 * Service Impl 格式化名称
		 */
		private String formatServiceImplFileName = "{entityName}ServiceImpl";

	}

	/**
	 * Mapper 配置
	 */
	@Data
	public static final class _Mapper implements Serializable {

		private static final long serialVersionUID = 1L;

		/**
		 * Mapper 格式化名称
		 */
		private String formatMapperFileName = "{entityName}Mapper";

		/**
		 * Mapper XML 格式化名称
		 */
		private String formatXmlFileName = "{entityName}Mapper";

	}

}
