package cn.renlm.plugins.MyUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import cn.hutool.core.io.FileUtil;
import freemarker.cache.FileTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

/**
 * 模板工具类
 * 
 * @author RenLiMing(任黎明)
 *
 */
@UtilityClass
public class MyFreemarkerUtil {

	private static final Configuration cfg = new Configuration(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS);

	static {
		cfg.setClassicCompatible(true);
		cfg.setClassForTemplateLoading(MyFreemarkerUtil.class, File.separator);
		cfg.setClassLoaderForTemplateLoading(MyFreemarkerUtil.class.getClassLoader(), File.separator);
	}
	
	/**
	 * 解析模板
	 * 
	 * @param filePath
	 * @param value
	 * @return
	 */
	@SneakyThrows
	public final static String readFromFile(String filePath, Object value) {
		File templateFile = new File(filePath);
		FileTemplateLoader templateLoader = new FileTemplateLoader(templateFile.getParentFile());
		Configuration configuration = new Configuration(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS);
		configuration.setTemplateLoader(templateLoader);
		Template template = configuration.getTemplate(FileUtil.normalize(FileUtil.getName(templateFile)));
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		template.process(value, new OutputStreamWriter(out));
		return new String(out.toByteArray(), StandardCharsets.UTF_8.name());
	}

	/**
	 * 解析模板
	 * 
	 * @param path
	 * @param value
	 * @return
	 */
	@SneakyThrows
	public final static String read(String path, Object value) {
		Template template = cfg.getTemplate(FileUtil.normalize(path));
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		template.process(value, new OutputStreamWriter(out));
		return new String(out.toByteArray(), StandardCharsets.UTF_8.name());
	}

	/**
	 * 解析模板
	 * 
	 * @param path
	 * @param key
	 * @param value
	 * @return
	 */
	@SneakyThrows
	public final static String read(String path, String key, Object value) {
		Map<String, Object> dataModel = new HashMap<>();
		dataModel.put(key, value);
		Template template = cfg.getTemplate(FileUtil.normalize(path));
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		template.process(dataModel, new OutputStreamWriter(out));
		return new String(out.toByteArray(), StandardCharsets.UTF_8.name());
	}

}
