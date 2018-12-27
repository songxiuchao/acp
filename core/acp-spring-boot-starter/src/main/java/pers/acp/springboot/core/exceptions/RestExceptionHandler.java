package pers.acp.springboot.core.exceptions;

import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import pers.acp.springboot.core.enums.ResponseCode;
import pers.acp.springboot.core.tools.PackageTools;
import pers.acp.core.exceptions.EnumValueUndefinedException;
import pers.acp.core.log.LogFactory;
import pers.acp.springboot.core.vo.ErrorVO;

/**
 * Create by zhangbin on 2017-8-10 16:26
 */
@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    private final LogFactory log = LogFactory.getInstance(this.getClass());

    /**
     * 处理异常错误请求
     *
     * @param ex 异常类
     * @return 响应对象
     */
    @ExceptionHandler(value = ServerException.class)
    protected ResponseEntity<Object> handleServerException(ServerException ex) {
        log.error(ex.getMessage(), ex);
        ResponseCode responseCode;
        try {
            responseCode = ResponseCode.getEnum(ex.getCode());
        } catch (EnumValueUndefinedException e) {
            responseCode = ResponseCode.otherError;
        }
        ErrorVO errorVO = PackageTools.buildErrorResponsePackage(responseCode, ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).header("Content-Type", MediaType.APPLICATION_JSON_UTF8_VALUE).body(errorVO);
    }

    /**
     * 处理异常错误请求
     *
     * @param ex      异常类
     * @param body    协议体
     * @param headers 请求头
     * @param status  请求状态
     * @param request 请求对象
     * @return 响应对象
     */
    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers, HttpStatus status, WebRequest request) {
        log.error(ex.getMessage(), ex);
        ErrorVO errorVO;
        if (ex instanceof ServerException) {
            ResponseCode responseCode;
            try {
                responseCode = ResponseCode.getEnum(((ServerException) ex).getCode());
            } catch (EnumValueUndefinedException e) {
                responseCode = ResponseCode.otherError;
            }
            errorVO = PackageTools.buildErrorResponsePackage(responseCode, ex.getMessage());
        } else {
            errorVO = PackageTools.buildErrorResponsePackage(ResponseCode.otherError, ex.getMessage());
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).header("Content-Type", MediaType.APPLICATION_JSON_UTF8_VALUE).body(errorVO);
    }

    /**
     * 处理@RequestParam错误, 即参数不足
     *
     * @param ex      异常类
     * @param headers 请求头
     * @param status  请求状态
     * @param request 请求对象
     * @return 响应对象
     */
    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        log.error(ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).header("Content-Type", MediaType.APPLICATION_JSON_UTF8_VALUE).body(PackageTools.buildErrorResponsePackage(ResponseCode.invalidParameter, ex.getMessage()).toString());
    }

    /**
     * 处理参数类型转换失败
     *
     * @param ex      异常类
     * @param headers 请求头
     * @param status  请求状态
     * @param request 请求对象
     * @return 响应对象
     */
    @Override
    protected ResponseEntity<Object> handleTypeMismatch(TypeMismatchException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        log.error(ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).header("Content-Type", MediaType.APPLICATION_JSON_UTF8_VALUE).body(PackageTools.buildErrorResponsePackage(ResponseCode.invalidParameter, ex.getMessage()).toString());
    }

}
