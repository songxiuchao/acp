package com.test.kotlin.domain

import com.test.kotlin.entity.TableOne
import com.test.kotlin.repository.TableOneRepository
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

/**
 * @author zhangbin by 28/04/2018 13:07
 * @since JDK 11
 */
@Service
@Transactional
class TableOneDomain(private val tableOneRepository: TableOneRepository) {

    @Transactional
    fun create(tableOne: TableOne): TableOne = tableOneRepository.save(tableOne)

    fun query(name: String): Optional<TableOne> = tableOneRepository.findByName(name)

    fun all(specification: Specification<TableOne>?): MutableList<TableOne> = tableOneRepository.findAll(specification)
}