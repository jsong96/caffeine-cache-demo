package study.caffeinecachedemo.service

import reactor.core.publisher.Mono

interface OrderService {
    fun requestOrderFirstWay(id: String): Mono<String>
}
