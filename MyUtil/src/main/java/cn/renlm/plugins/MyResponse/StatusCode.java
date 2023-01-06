package cn.renlm.plugins.MyResponse;

import lombok.Getter;

/**
 * 响应状态码
 * 
 * @author Renlm
 *
 */
public enum StatusCode {

	// 200
	OK(200, "OK"),
	// 400
	BAD_REQUEST(400, "Bad Request"),
	// 401
	UNAUTHORIZED(401, "Unauthorized"),
	// 500
	INTERNAL_SERVER_ERROR(500, "Internal Server Error");

	@Getter
	private final int value;

	@Getter
	private final String reasonPhrase;

	StatusCode(int value, String reasonPhrase) {
		this.value = value;
		this.reasonPhrase = reasonPhrase;
	}

}
