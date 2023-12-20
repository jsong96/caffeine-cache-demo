package study.caffeinecachedemo.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import study.caffeinecachedemo.service.OrderService

@RestController
@RequestMapping("/order-service")
class OrderController(
    private val orderService: OrderService,
) {

    @GetMapping("/order/first-way/{id}")
    fun requestOrderFirstWay(
        @PathVariable id: String,
    ): Mono<String> = orderService.requestOrderFirstWay(id)
}
