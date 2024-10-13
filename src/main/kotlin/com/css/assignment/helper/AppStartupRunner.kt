package com.css.assignment.helper

import com.css.assignment.service.DeliveryService
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.SpringApplication
import org.springframework.context.ApplicationContext
import org.springframework.stereotype.Component

@Component
class AppStartupRunner(
    private val deliveryService: DeliveryService,
    private val applicationContext: ApplicationContext,
) : ApplicationRunner {
    override fun run(args: ApplicationArguments) {
        deliveryService.run()
        SpringApplication.exit(applicationContext, { 0 })
    }
}