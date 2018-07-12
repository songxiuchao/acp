package pers.acp.springcloud.server.hello.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pers.acp.springcloud.common.log.LogInstance;

import javax.servlet.http.HttpServletRequest;

/**
 * @author zhangbin by 2018-3-5 14:00
 * @since JDK1.8
 */
@RestController
public class HelloController {

    private final LogInstance logInstance;

    @Autowired
    public HelloController(LogInstance logInstance) {
        this.logInstance = logInstance;
    }

    @GetMapping(value = "/hellor", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<Object> hello(@RequestParam String name) {
        logInstance.error("hello response: name=" + name);
        return ResponseEntity.ok("hello response: name=" + name);
    }

    @GetMapping(value = "/hello", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<Object> hello(HttpServletRequest request, @RequestParam String name) {
        logInstance.error("hello response: name=" + name);
        return ResponseEntity.ok("hello response: name=" + name);
    }

}
