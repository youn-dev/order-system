package com.css.assignment.domain.courier

import com.css.assignment.enums.CourierStatus
import java.time.LocalDateTime

data class Courier(
    var status: CourierStatus,
    val arrivalAt: LocalDateTime,
)
