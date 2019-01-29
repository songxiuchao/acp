package pers.acp.test.application.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pers.acp.test.application.entity.primary.TableOne;
import pers.acp.test.application.repo.primary.TableRepo;
import pers.acp.core.CommonTools;
import pers.acp.core.log.LogFactory;
import springfox.documentation.annotations.ApiIgnore;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

/**
 * Created by zhangbin on 2017/4/26.
 */
@RestController
@RequestMapping("/boot")
@Api("测试接口")
public class TestController {

    private final LogFactory log = LogFactory.getInstance(this.getClass());

    private final TableRepo tableRepo;

    @Autowired
    public TestController(TableRepo tableRepo) {
        this.tableRepo = tableRepo;
    }

    @ApiOperation(value = "测试 hello", notes = "返回项目绝对路径")
    @RequestMapping(value = "hello", method = RequestMethod.GET)
    public ResponseEntity<Object> home() throws UnknownHostException {
        return ResponseEntity.ok("home，" + CommonTools.getProjectAbsPath() + "，" + InetAddress.getLocalHost().getHostAddress());
    }

    @ApiOperation(value = "测试 rest 接口1", notes = "返回数据库中记录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "name", value = "名称", required = true, paramType = "path", dataType = "String"),
            @ApiImplicitParam(name = "pwd", value = "密码", required = true, paramType = "query", dataType = "String")
    })
    @RequestMapping(value = "/rest1/{name}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<TableOne>> post1(@PathVariable String name, @RequestParam String pwd) {
        log.info("name1:" + name + ",pwd1:" + pwd);
        log.info(CommonTools.getProjectAbsPath());
        List<TableOne> tableOneList = tableRepo.findAll();
        return ResponseEntity.ok(tableOneList);
    }

    @ApiIgnore
    @RequestMapping(value = "/rest2/{name}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Object> post2(@PathVariable String name, @RequestParam String pwd) {
        log.info("name2:" + name + ",pwd2:" + pwd);
        log.info(CommonTools.getProjectAbsPath());
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode objectNode = mapper.createObjectNode();
        objectNode.put("result2", name + pwd);
        return ResponseEntity.ok(objectNode.toString());
    }

    @ApiOperation(value = "测试 rest 接口2", notes = "返回字符串")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "str", value = "字符串", required = true, paramType = "body", dataType = "String")
    })
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
