package cn.renlm.plugins.MyUtil;

import java.util.List;

import javax.sql.DataSource;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.db.ds.DSFactory;
import cn.hutool.db.meta.MetaUtil;
import cn.hutool.db.meta.Table;
import cn.hutool.db.meta.TableType;
import cn.hutool.setting.Setting;
import lombok.experimental.UtilityClass;

/**
 * 数据库操作工具
 * 
 * @author Renlm
 *
 */
@UtilityClass
public class MyDbUtil {

	/**
	 * 获得表的元信息
	 * 
	 * @param jdbcUrl
	 * @param username
	 * @param password
	 * @param types
	 * @return
	 */
	public static final List<Table> getTableMetas(String jdbcUrl, String username, String password,
			TableType... types) {
		Setting setting = new Setting();
		setting.set(DSFactory.KEY_ALIAS_URL[0], jdbcUrl);
		setting.set(DSFactory.KEY_ALIAS_USER[0], username);
		setting.set(DSFactory.KEY_ALIAS_PASSWORD[0], password);
		setting.set(DSFactory.KEY_CONN_PROPS[0], String.valueOf(true));
		setting.set(DSFactory.KEY_CONN_PROPS[1], String.valueOf(true));
		DSFactory dsf = DSFactory.create(setting);
		DataSource ds = dsf.getDataSource();
		List<Table> tables = CollUtil.newArrayList();
		MetaUtil.getTables(dsf.getDataSource(), types).forEach(tableName -> {
			Table table = MetaUtil.getTableMeta(ds, tableName);
			tables.add(table);
		});
		return tables;
	}

	/**
	 * 获得表的元信息
	 * 
	 * @param jdbcUrl
	 * @param schema
	 * @param username
	 * @param password
	 * @param types
	 * @return
	 */
	public static final List<Table> getTableMetas(String jdbcUrl, String schema, String username, String password,
			TableType... types) {
		Setting setting = new Setting();
		setting.set(DSFactory.KEY_ALIAS_URL[0], jdbcUrl);
		setting.set(DSFactory.KEY_ALIAS_USER[0], username);
		setting.set(DSFactory.KEY_ALIAS_PASSWORD[0], password);
		setting.set(DSFactory.KEY_CONN_PROPS[0], String.valueOf(true));
		setting.set(DSFactory.KEY_CONN_PROPS[1], String.valueOf(true));
		DSFactory dsf = DSFactory.create(setting);
		DataSource ds = dsf.getDataSource();
		List<Table> tables = CollUtil.newArrayList();
		MetaUtil.getTables(dsf.getDataSource(), schema, types).forEach(tableName -> {
			Table table = MetaUtil.getTableMeta(ds, null, schema, tableName);
			tables.add(table);
		});
		return tables;
	}

}
