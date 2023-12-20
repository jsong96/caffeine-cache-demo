package study.caffeinecachedemo.controller

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/products")
class ProductController {

    @GetMapping("/{id}")
    fun requestProduct(@PathVariable id: String) =
        if (id == WRONG_PRODUCT_ID_MATCH) {
            Mono.error(ResponseStatusException(HttpStatus.NOT_FOUND, "Product is not found"))
        } else {
            Mono.just("Product $id")
        }

    @GetMapping
    fun getProducts(): Mono<Map<String, String>> {
        val productMap = mutableMapOf<String, String>()
        (1..5).map {
            productMap.put("$it", "Product $it")
        }
        logger.info { "received request" }
        return Mono.just(productMap)
    }

    companion object {
        const val WRONG_PRODUCT_ID_MATCH = "6"
        private val logger = KotlinLogging.logger { }
    }
}
