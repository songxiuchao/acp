package com.test.scala.base

import org.springframework.data.jpa.repository.{JpaRepository, JpaSpecificationExecutor}
import org.springframework.data.repository.NoRepositoryBean

/**
  * @author zhangbin by 02/05/2018 17:36
  * @since JDK 11
  */
@NoRepositoryBean
trait BaseRepository[T, ID] extends JpaSpecificationExecutor[T] with JpaRepository[T, ID]