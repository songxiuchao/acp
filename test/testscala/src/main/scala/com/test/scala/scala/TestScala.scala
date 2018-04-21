package com.test.scala.scala

/**
  * Create by zhangbin on 2017-9-28 16:39
  */
class TestScala {

  /**
    * test1 测试
    *
    * @param count int
    */
  def testForFunc(count: Int): Unit = {
    var a = 0
    for (i <- 0 until count) {
      a += i
    }
  }

  /**
    * test2
    *
    * @param count int
    */
  def testWhileFunc(count: Int): Unit = {
    var i, a = 0
    while (i < count) {
      a += i
      i += 1
    }
  }

}
