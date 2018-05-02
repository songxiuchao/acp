package pers.acp.test.application.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pers.acp.test.application.task.Task1;
import pers.acp.core.CommonTools;
import pers.acp.core.log.LogFactory;

/**
 * Created by zhangbin on 2017/4/26.
 */
@RestController
@RequestMapping("/boot")
public class TestController {

    private final LogFactory log = LogFactory.getInstance(this.getClass());

    private final Task1 task1;

    @Autowired
    public TestController(Task1 task1) {
        this.task1 = task1;
    }

    @RequestMapping(value = "hello", method = RequestMethod.GET)
    public ResponseEntity<Object> home() {
        return ResponseEntity.ok("home，" + CommonTools.getProjectAbsPath());
    }

    @RequestMapping(value = "/rest1/{name}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Object> post1(@PathVariable String name, @RequestParam String pwd) {
        log.info("name1:" + name + ",pwd1:" + pwd);
        log.info(CommonTools.getProjectAbsPath());
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode objectNode = mapper.createObjectNode();
        objectNode.put("result1", name + pwd);
        return ResponseEntity.ok(objectNode.toString());
    }

    @RequestMapping(value = "/rest2/{name}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Object> post2(@PathVariable String name, @RequestParam String pwd) {
        log.info("name2:" + name + ",pwd2:" + pwd);
        log.info(CommonTools.getProjectAbsPath());
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode objectNode = mapper.createObjectNode();
        objectNode.put("result2", name + pwd);
        return ResponseEntity.ok(objectNode.toString());
    }

    @RequestMapping(value = "/rest2", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Object> post2(@RequestBody String str) {
        log.info("str:" + str);
        log.info(CommonTools.getProjectAbsPath());
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode objectNode = mapper.createObjectNode();
        objectNode.put("result2", str);
        return ResponseEntity.ok(objectNode.toString());
    }

}
