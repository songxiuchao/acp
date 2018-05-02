package com.test.scala.entity

import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.Parameter
import javax.persistence._

/**
  * @author zhangbin by 02/05/2018 17:46
  * @since JDK1.8
  */
@Entity
@Table(name = "table1")
class TableOne {

  @GenericGenerator(name = "testSequenceGenerator",
    strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
    parameters = Array(
      new Parameter(name = "sequence_name", value = "test_seq"),
      new Parameter(name = "initial_value", value = "1"),
      new Parameter(name = "increment_size", value = "1")))
  @GeneratedValue(generator = "testSequenceGenerator")
  @Id
  @Column(updatable = false)
  val id = 0

  val name: String = ""

  val value: Double = 0.00

}
