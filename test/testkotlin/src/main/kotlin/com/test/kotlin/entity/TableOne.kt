package com.test.kotlin.entity

import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.Parameter
import javax.persistence.*

/**
 * @author zhangbin by 28/04/2018 12:57
 * @since JDK1.8
 */
@Entity
@Table(name = "table1")
data class TableOne(
        @GenericGenerator(name = "tableSequenceGenerator",
                strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
                parameters = [
                    (Parameter(name = "sequence_name", value = "table_seq")),
                    (Parameter(name = "initial_value", value = "1")),
                    (Parameter(name = "increment_size", value = "1"))])
        @Id
        @GeneratedValue(generator = "tableSequenceGenerator")
        @Column(updatable = false)
        val id: Long = 0,

        val name: String = "",

        val value: Double = 0.00
)