package com.css.assignment.domain.orders

import com.css.assignment.enums.OrderStatus

data class DeliveryOrder(
     override val id: String,
     override val name: String,
     override val prepTime: Long,
     override val orderStatus: OrderStatus = OrderStatus.WAITING,
): OrderBase
