package cn.renlm.plugins;

import java.io.Serializable;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import com.thoughtworks.xstream.converters.extended.ToAttributedValueConverter;

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

	@XStreamAlias("template")
	private _TemplateConfig templateConfig;

	@XStreamAlias("package")
	private _PackageConfig packageConfig;

	@XStreamAlias("strategy")
	private _StrategyConfig strategyConfig;

	/**
	 * 模板配置
	 */
	@Data
	public static final class _TemplateConfig implements Serializable {

		private static final long serialVersionUID = 1L;

		/**
		 * 不生成Controller
		 */
		private boolean disableController;

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
		private String controller = "api";

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
		private String mapper = "dao";

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

	}

	/**
	 * 实体配置
	 */
	@Data
	public static final class _Entity implements Serializable {

		private static final long serialVersionUID = 1L;

		/**
		 * 父类
		 */
		private String superClass;

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

}
