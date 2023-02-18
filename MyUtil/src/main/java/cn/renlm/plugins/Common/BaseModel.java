package cn.renlm.plugins.Common;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

/**
 * 实体公共父类
 * 
 * @author RenLiMing(任黎明)
 *
 */
@Data
public class BaseModel implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 创建时间
	 */
	private Date createdAt;

	/**
	 * 更新时间
	 */
	private Date updatedAt;

	/**
	 * 是否删除
	 */
	private Boolean deleted;

}
