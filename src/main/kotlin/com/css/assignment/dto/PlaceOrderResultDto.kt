package com.css.assignment.dto

import com.css.assignment.domain.orders.OrderInProgress

data class PlaceOrderResultDto(
    var orderInProgressCount: Int,
    val orderInProgress: MutableList<OrderInProgress>
)
