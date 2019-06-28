package pers.acp.spring.boot.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 错误信息实体
 *
 * @author zhang by 27/12/2018 13:07
 * @since JDK 11
 */
@ApiModel(value = "错误信息")
public class ErrorVO {

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getErrorDescription() {
        return errorDescription;
    }

    public void setErrorDescription(String errorDescription) {
        this.errorDescription = errorDescription;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    @ApiModelProperty(value = "错误编码")
    private int code;

    @ApiModelProperty(value = "信息")
    private String error;

    @ApiModelProperty(value = "描述")
    private String errorDescription;

}
