package com.test.scala

import com.fasterxml.jackson.module.scala.DefaultScalaModule
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean

/**
  * @author zhangbin by 02/05/2018 16:44
  * @since JDK 11
  */
@SpringBootApplication
class TestScalaApplication {

  @Bean def jacksonModuleScala = new DefaultScalaModule

}

object TestScalaApplication {
  def main(args: Array[String]): Unit = {
    SpringApplication.run(classOf[TestScalaApplication], args: _*)
  }
}
