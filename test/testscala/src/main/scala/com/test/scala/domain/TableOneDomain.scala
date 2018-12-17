package com.test.scala.domain

import java.util
import java.util.Optional

import com.test.scala.entity.TableOne
import com.test.scala.repository.TableOneRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
  * @author zhangbin by 02/05/2018 17:43
  * @since JDK 11
  */
@Service
@Transactional
class TableOneDomain @Autowired()(private val tableOneRepository: TableOneRepository) {

  @Transactional
  def create(tableOne: TableOne): TableOne = tableOneRepository.save(tableOne)

  def query(name: String): Optional[TableOne] = tableOneRepository.findByName(name)

  def all(specification: Specification[TableOne]): util.List[TableOne] = tableOneRepository.findAll(specification)

}
