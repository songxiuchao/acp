package com.test.scala.repository

import com.test.scala.base.BaseRepository
import com.test.scala.entity.TableOne
import java.lang.Long
import java.util.Optional

/**
  * @author zhangbin by 02/05/2018 18:03
  * @since JDK 11
  */
trait TableOneRepository extends BaseRepository[TableOne, Long] {
  def findByName(name: String): Optional[TableOne]
}
