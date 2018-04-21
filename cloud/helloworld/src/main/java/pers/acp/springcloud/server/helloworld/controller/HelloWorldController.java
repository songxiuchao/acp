package pers.acp.springcloud.server.helloworld.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pers.acp.springcloud.server.helloworld.feign.HelloServer;
import pers.acp.springcloud.server.helloworld.feign.WorldServer;

/**
 * @author zhangbin by 2018-3-6 15:34
 * @since JDK1.8
 */
@RestController
public class HelloWorldController {

    private final HelloServer helloServer;

    private final WorldServer worldServer;

    @Autowired
    public HelloWorldController(HelloServer helloServer, WorldServer worldServer) {
        this.helloServer = helloServer;
        this.worldServer = worldServer;
    }

    @GetMapping(value = "/helloworld", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<Object> helloWorld(@RequestParam String name) {
        return ResponseEntity.ok(helloServer.fromClient(name) + ";" + worldServer.fromClient(name));
    }

}
