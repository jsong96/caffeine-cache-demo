package study.caffeinecachedemo.service

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import com.github.benmanes.caffeine.cache.RemovalListener
import com.github.benmanes.caffeine.cache.Scheduler
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.server.ResponseStatusException
import reactor.core.publisher.Mono
import java.time.Duration

@Service
class OrderServiceImpl(
    private val webClient: WebClient,
) : OrderService {
    val productCache: Cache<String, String> = Caffeine.newBuilder()
        .expireAfterAccess(Duration.ofSeconds(1L))
        .maximumSize(10L)
        .evictionListener(RemovalListener<String, String> { key, value, cause ->
            logger.info { "${this.javaClass.name}: key $key was evicted" }
        })
        .removalListener(RemovalListener<String, String> { key, value, cause ->
            logger.info { "${this.javaClass.name}: key $key was removed" }
        })
        .scheduler(Scheduler.systemScheduler())
        .build()

    override fun requestOrderFirstWay(id: String): Mono<String> {
        val productName = productCache.getIfPresent(id)

        return Mono.justOrEmpty(productName?.let { "Order requested on $productName" })
            .switchIfEmpty(
                webClient.get()
                    .uri("http://localhost:8080/products/$id")
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .exchangeToMono { clientResponse ->
                        if (clientResponse.statusCode().is2xxSuccessful) {
                            clientResponse.bodyToMono(String::class.java)
                                .doOnNext { product -> productCache.put(id, product) }
                                .map { "Order request on $it" }
                        } else if (clientResponse.statusCode().is4xxClientError) {
                            Mono.error(ResponseStatusException(HttpStatus.NOT_FOUND, "Product is not available"))
                        } else {
                            Mono.error(ResponseStatusException(HttpStatus.NOT_FOUND, "Product is not available"))
                        }
                    }
                    .switchIfEmpty(Mono.just("Product is not available"))
            )
    }

    companion object {
        private val logger = KotlinLogging.logger { }
    }
}
