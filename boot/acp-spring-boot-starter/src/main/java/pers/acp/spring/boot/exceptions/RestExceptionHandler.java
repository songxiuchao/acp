package pers.acp.spring.boot.exceptions;

import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import pers.acp.spring.boot.vo.ErrorVO;
import pers.acp.spring.boot.enums.ResponseCode;
import pers.acp.spring.boot.tools.PackageTools;
import pers.acp.core.exceptions.EnumValueUndefinedException;
import pers.acp.core.log.LogFactory;

import javax.validation.ConstraintViolationException;
import java.util.List;

/**
 * Create by zhangbin on 2017-8-10 16:26
 */
@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    private final LogFactory log = LogFactory.getInstance(this.getClass());

    /**
     * 处理自定义异常
     *
     * @param ex 异常类
     * @return 响应对象
     */
    @ExceptionHandler({ServerException.class, ConstraintViolationException.class})
    protected ResponseEntity<Object> handleServerException(Exception ex) {
        log.error(ex.getMessage(), ex);
        ResponseCode responseCode;
        try {
            if (ex instanceof ServerException) {
                responseCode = ResponseCode.getEnum(((ServerException) ex).getCode());
            } else if (ex instanceof ConstraintViolationException || ex instanceof MethodArgumentNotValidException) {
                responseCode = ResponseCode.invalidParameter;
            } else {
                responseCode = ResponseCode.otherError;
            }
        } catch (EnumValueUndefinedException e) {
            responseCode = ResponseCode.otherError;
        }
        ErrorVO errorVO = PackageTools.buildErrorResponsePackage(responseCode, ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).header("Content-Type", MediaType.APPLICATION_JSON_UTF8_VALUE).body(errorVO);
    }

    /**
     * 处理 MethodArgumentNotValidException 异常，参数校验不通过
     *
     * @param ex      MethodArgumentNotValidException
     * @param headers 请求头
     * @param status  请求状态
     * @param request 请求对象
     * @return 响应对象
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        log.error(ex.getMessage(), ex);
        List<ObjectError> errors = ex.getBindingResult().getAllErrors();
        StringBuilder errorMsg = new StringBuilder();
        errors.forEach(error -> errorMsg.append(error.getDefaultMessage()).append(";"));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).header("Content-Type", MediaType.APPLICATION_JSON_UTF8_VALUE).body(PackageTools.buildErrorResponsePackage(ResponseCode.invalidParameter, errorMsg.toString()));
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
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).header("Content-Type", MediaType.APPLICATION_JSON_UTF8_VALUE).body(PackageTools.buildErrorResponsePackage(ResponseCode.invalidParameter, ex.getMessage()));
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
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).header("Content-Type", MediaType.APPLICATION_JSON_UTF8_VALUE).body(PackageTools.buildErrorResponsePackage(ResponseCode.invalidParameter, ex.getMessage()));
    }

    /**
     * 处理通用异常
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
        ErrorVO errorVO = PackageTools.buildErrorResponsePackage(ResponseCode.otherError, ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).header("Content-Type", MediaType.APPLICATION_JSON_UTF8_VALUE).body(errorVO);
    }

}