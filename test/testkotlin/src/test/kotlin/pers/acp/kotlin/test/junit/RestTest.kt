package pers.acp.kotlin.test.junit

import com.test.kotlin.entity.TableOne
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus

/**
 * @author zhangbin by 28/04/2018 17:18
 * @since JDK 11
 */
class RestTest : BaseTest() {

    @Test
    fun testQuery200() {
        val name = "abc"
        val result = testRestTemplate.getForEntity("/query?name={1}", TableOne::class.java, name)
        Assertions.assertEquals(HttpStatus.OK, result.statusCode)
        Assertions.assertEquals(result.body!!.name, name)
    }

    @Test
    fun testAdd201() {
        val name = "fsdafa"
        val tableOne = TableOne().apply {
            this.name = name
            this.value = 101.0
        }
        val result = testRestTemplate.postForEntity("/add", tableOne, TableOne::class.java)
        Assertions.assertEquals(HttpStatus.CREATED, result.statusCode)
        Assertions.assertEquals(result.body!!.name, name)
    }

}