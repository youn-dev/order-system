package com.css.assignment.domain.orders

import com.css.assignment.enums.OrderStatus

interface OrderBase {
    val id: String
    val name: String
    val prepTime: Long
    val orderStatus: OrderStatus
}
