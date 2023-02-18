package cn.renlm.plugins.Common;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;

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
	@TableField(value = "created_at", fill = FieldFill.INSERT)
	private Date createdAt;

	/**
	 * 更新时间
	 */
	@TableField(value = "updated_at", fill = FieldFill.UPDATE)
	private Date updatedAt;

	/**
	 * 是否删除
	 */
	@TableLogic(value = "false", delval = "true")
	private Boolean deleted;

}
