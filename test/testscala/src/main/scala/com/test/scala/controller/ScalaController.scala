package com.test.scala.controller

import com.test.scala.domain.TableOneDomain
import com.test.scala.entity.TableOne
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.{MediaType, ResponseEntity}
import org.springframework.web.bind.annotation._

/**
  * @author zhangbin by 02/05/2018 21:50
  * @since JDK 11
  */
@RestController
class ScalaController @Autowired()(private val tableOneDomain: TableOneDomain) {

  @GetMapping(value = Array("/query"), produces = Array(MediaType.APPLICATION_JSON_UTF8_VALUE))
  def doQuery(@RequestParam name: String): ResponseEntity[TableOne] = {
    ResponseEntity.ok(tableOneDomain.query(name).get())
  }

  @PostMapping(value = Array("/add"), produces = Array(MediaType.APPLICATION_JSON_UTF8_VALUE))
  def doAdd(@RequestBody tableOne: TableOne): ResponseEntity[TableOne] = {
    ResponseEntity.ok(tableOneDomain.create(tableOne))
  }

}
