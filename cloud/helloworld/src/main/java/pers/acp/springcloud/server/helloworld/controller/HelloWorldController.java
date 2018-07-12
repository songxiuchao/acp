package pers.acp.springcloud.server.helloworld.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pers.acp.springcloud.common.log.LogInstance;
import pers.acp.springcloud.server.helloworld.feign.HelloServer;
import pers.acp.springcloud.server.helloworld.feign.WorldServer;

/**
 * @author zhangbin by 2018-3-6 15:34
 * @since JDK1.8
 */
@RestController
public class HelloWorldController {

    private final LogInstance logInstance;

    private final HelloServer helloServer;

    private final WorldServer worldServer;

    @Autowired
    public HelloWorldController(HelloServer helloServer, WorldServer worldServer, LogInstance logInstance) {
        this.helloServer = helloServer;
        this.worldServer = worldServer;
        this.logInstance = logInstance;
    }

    @PostMapping(value = "/helloworld", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<Object> helloWorld(@RequestBody String content) {
        logInstance.info(helloServer.fromClient(content) + ";" + worldServer.fromClient(content));
        return ResponseEntity.ok(helloServer.fromClient(content) + ";" + worldServer.fromClient(content));
    }

    @GetMapping(value = "/helloworld", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<Object> helloWorldGet(@RequestParam String name) {
        logInstance.info(helloServer.fromClient(name) + ";" + worldServer.fromClient(name));
        return ResponseEntity.ok(helloServer.fromClient(name) + ";" + worldServer.fromClient(name));
    }

}
