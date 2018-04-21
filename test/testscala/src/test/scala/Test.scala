import com.test.scala.java.TestJava
import com.test.scala.scala.TestScala

/**
  * Create by zhangbin on 2017-9-28 23:26
  */
object Test {

  def main(args: Array[String]): Unit = {

    var yy = for (s <- "yong"; i <- 0 to 1) yield (s + i).toChar
    println(yy)

    val ss = new StringBuilder
    ss += 'a'
    ss ++= "bcdef" + "123"
    println(ss)

    val totalCount = 10000000
    val test = new TestScala
    //    var test = new TestJava

    /*var start = System.currentTimeMillis()
    test.testForFunc(totalCount)
    var end = System.currentTimeMillis()
    println(">>>>>> testForFunc : " + (end - start))

    start = System.currentTimeMillis()
    test.testWhileFunc(totalCount)
    end = System.currentTimeMillis()
    println(">>>>>> testWhileFunc : " + (end - start))*/

    /*var start = System.currentTimeMillis()
    test.testWhileFunc(totalCount)
    var end = System.currentTimeMillis()
    println(">>>>>> testWhileFunc : " + (end - start))

    start = System.currentTimeMillis()
    test.testForFunc(totalCount)
    end = System.currentTimeMillis()
    println(">>>>>> testForFunc : " + (end - start))*/

    val number: Array[Int] = new Array[Int](totalCount)
    for (i <- number.indices) {
      number(1) = i
    }
    var start = System.currentTimeMillis()
    for (i <- number.indices) {

    }
    var end = System.currentTimeMillis()
    println(">>>>>> for 1 : " + (end - start))
    start = System.currentTimeMillis()
    for (x <- number) {

    }
    end = System.currentTimeMillis()
    println(">>>>>> for 2 : " + (end - start))
    var list = List("a","b")
    for(item<-list.indices){
      println(item)
    }
    start = System.currentTimeMillis()
    var i = 0
    while (i < number.length) {
      i += 1
    }
    end = System.currentTimeMillis()
    println(">>>>>> while : " + (end - start))

    val pattern = "Scala".r
    val str = "Scala is Scalable and cool"
    println(pattern findFirstIn str)
  }

}
