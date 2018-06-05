package pers.acp.springcloud.gateway.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import pers.acp.springcloud.gateway.vo.ErrorVO;

/**
 * @author zhangbin by 26/04/2018 21:30
 * @since JDK1.8
 */
@RestController
public class CustomerHystrixErrorController {

    /**
     * 服务断路 Hystrix 响应
     *
     * @return ResponseEntity
     */
    @GetMapping(value = "/hystrixhandle", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Object> hystrixHandle() {
        ErrorVO errorVO = new ErrorVO();
        errorVO.setError("invalid service");
        errorVO.setErrorDescription("GateWay error, the service is invalid");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorVO);
    }

}
