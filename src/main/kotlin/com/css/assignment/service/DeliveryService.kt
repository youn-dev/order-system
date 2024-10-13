package com.css.assignment.service

import com.css.assignment.domain.courier.Courier
import com.css.assignment.domain.orders.DeliveryOrder
import com.css.assignment.domain.orders.OrderInProgress
import com.css.assignment.dto.PlaceOrderResultDto
import com.css.assignment.enums.CourierStatus
import com.css.assignment.enums.OrderStatus
import com.css.assignment.helper.UniformedRandomHelper
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.springframework.stereotype.Service
import java.io.File
import java.nio.file.Paths
import java.time.LocalDateTime

@Service
class DeliveryService(
    private val objectMapper: ObjectMapper,
) {
    fun run() {
        val orders = getOrders() // 132 orders
        var orderCount = orders.size
        var orderInProgressCount = 0
        val couriers = mutableListOf<Courier>()
        var placeOrderResult = PlaceOrderResultDto(
            orderInProgressCount = orderInProgressCount,
            orderInProgress = mutableListOf()
        )

        while (orderCount > 0) {
            Thread.sleep(1000)
            placeOrderResult = placeOrder(
                placeOrderResult = placeOrderResult,
                orders = orders,
                couriers = couriers,
            )
            orderCount = orders.size
            println("remaining orders - $orderCount")
            orderInProgressCount += placeOrderResult.orderInProgressCount
            checkCourierStatus(couriers)
            checkOrderStatus(placeOrderResult)
            deliver(placeOrderResult = placeOrderResult, couriers = couriers)
            removeFinished(placeOrderResult = placeOrderResult, couriers = couriers)
        }
    }

    private fun getOrders(): MutableList<DeliveryOrder> {
        val relativePath = Paths.get("")
        val path = relativePath.toAbsolutePath().toString()
        val orders = File("$path/src/main/kotlin/com/css/assignment/domain/orders/dispatch_orders.json").readText()
        return objectMapper.readValue<MutableList<DeliveryOrder>>(orders)
    }

    private fun placeOrder(
        placeOrderResult: PlaceOrderResultDto,
        orders: MutableList<DeliveryOrder>,
        couriers: MutableList<Courier>,
    ): PlaceOrderResultDto {
        return when (placeOrderResult.orderInProgressCount) {
            0 -> {
                placeOrderResult.orderInProgressCount += 2
                placeOrderResult.orderInProgress.add(OrderInProgress.of(orders.removeFirst()))
                placeOrderResult.orderInProgress.add(OrderInProgress.of(orders.removeFirst()))
                callCourier(orderCount = 2, couriers = couriers)
                println("place two orders - $placeOrderResult")
                println("place two couriers - $couriers")
                placeOrderResult
            }

            1 -> {
                placeOrderResult.orderInProgressCount += 1
                placeOrderResult.orderInProgress.add(OrderInProgress.of(orders.removeFirst()))
                callCourier(orderCount = 1, couriers = couriers)
                println("place an order - $placeOrderResult")
                println("place an courier - $couriers")
                placeOrderResult
            }

            else -> placeOrderResult
        }
    }

    private fun callCourier(orderCount: Int, couriers: MutableList<Courier>): MutableList<Courier>? {
        if (couriers.size >= 2) return null

        for (i in (1..orderCount)) {
            val courier = Courier(
                status = CourierStatus.COMING,
                arrivalAt = LocalDateTime.now().plusSeconds(UniformedRandomHelper.generateDispatchTime())
            )
            couriers.add(courier)
        }
        return couriers
    }

    private fun checkCourierStatus(couriers: MutableList<Courier>) {
        couriers.forEach {
            if (it.arrivalAt <= LocalDateTime.now()) {
                it.status = CourierStatus.WAITING
                println("courier waiting - $it")
            }
        }
    }

    private fun checkOrderStatus(placeOrderResult: PlaceOrderResultDto) {
        placeOrderResult.orderInProgress.forEach {
            if (it.orderReadyAt <= LocalDateTime.now()) {
                it.orderStatus = OrderStatus.READY
                println("order ready - $it")
            }
        }
    }

    private fun deliver(placeOrderResult: PlaceOrderResultDto, couriers: MutableList<Courier>) {
        placeOrderResult.orderInProgress.forEach {
            if (it.orderStatus == OrderStatus.READY &&
                couriers.any { courier -> courier.status == CourierStatus.WAITING }
            ) {
                it.orderStatus = OrderStatus.DELIVERED
                couriers.first { courier -> courier.status == CourierStatus.WAITING }.status = CourierStatus.DISPATCHED
                println("order delivered - $it")
                println("courier status - $couriers")
            }
        }
    }

    private fun removeFinished(placeOrderResult: PlaceOrderResultDto, couriers: MutableList<Courier>) {
        placeOrderResult.orderInProgress.removeAll { order ->
            order.orderStatus == OrderStatus.DELIVERED
        }.also {
            placeOrderResult.orderInProgressCount = placeOrderResult.orderInProgress.size
            couriers.removeAll { it.status == CourierStatus.DISPATCHED }
        }
    }
}
