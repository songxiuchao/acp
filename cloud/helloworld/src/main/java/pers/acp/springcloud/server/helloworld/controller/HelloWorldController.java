package pers.acp.springcloud.server.helloworld.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pers.acp.springboot.core.handle.HttpServletRequestAcp;
import pers.acp.springboot.core.tools.IpTools;
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
        String respon = helloServer.fromClient(content) + ";" + worldServer.fromClient(content);
        logInstance.info(respon);
        return ResponseEntity.ok(respon);
    }

    @GetMapping(value = "/helloworld", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<Object> helloWorldGet(@RequestParam String name) {
        String respon = helloServer.fromClient(name) + ";" + worldServer.fromClient(name);
        logInstance.info(respon);
        return ResponseEntity.ok(respon);
    }

    @GetMapping(value = "/open/ips", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<Object> ips(HttpServletRequestAcp requestAcp) {
        String ip = IpTools.getRemoteIP(requestAcp);
        return ResponseEntity.ok(ip);
    }

}
