package pers.acp.springcloud.server.hello.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zhangbin by 2018-3-5 14:00
 * @since JDK1.8
 */
@RestController
public class HelloController {

    @GetMapping(value = "/hello", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<Object> hello(@RequestParam String name) {
        return ResponseEntity.ok("hello response: name=" + name);
    }

}
