package com.css.assignment.domain.orders

import com.css.assignment.enums.OrderStatus
import java.time.LocalDateTime

data class OrderInProgress(
    override val id: String,
    override val name: String,
    override val prepTime: Long,
    override var orderStatus: OrderStatus = OrderStatus.IN_PREP,
    val orderReadyAt: LocalDateTime = LocalDateTime.now().plusSeconds(prepTime)
) : OrderBase {
    companion object {
        fun of(deliveryOrder: DeliveryOrder) =
            OrderInProgress(
                id = deliveryOrder.id,
                name = deliveryOrder.name,
                prepTime = deliveryOrder.prepTime,
            )
    }
}
