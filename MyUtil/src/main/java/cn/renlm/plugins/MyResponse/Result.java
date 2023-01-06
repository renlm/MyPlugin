package cn.renlm.plugins.MyResponse;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 响应结果
 *
 * @author Renlm
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class Result<T> implements Serializable {

	private static final long serialVersionUID = 1L;

	private int statusCode;

	private String message;

	private T data;

	public static <R> Result<R> of(StatusCode status) {
		return new Result<R>(status.getValue(), status.getReasonPhrase(), null);
	}

	public static <R> Result<R> of(StatusCode status, String message) {
		Result<R> result = Result.of(status);
		return result.setMessage(message);
	}

	public static <R> Result<R> success() {
		return Result.of(StatusCode.OK);
	}

	public static <R> Result<R> success(R data) {
		Result<R> result = success();
		return result.setData(data);
	}

	public static <R> Result<R> error() {
		return Result.of(StatusCode.INTERNAL_SERVER_ERROR);
	}

	public static <R> Result<R> error(String message) {
		return Result.of(StatusCode.INTERNAL_SERVER_ERROR, message);
	}

	public boolean isSuccess() {
		return this.statusCode == StatusCode.OK.getValue();
	}

}
