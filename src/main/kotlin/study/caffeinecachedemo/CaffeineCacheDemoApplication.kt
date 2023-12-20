package study.caffeinecachedemo

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class CaffeineCacheDemoApplication

fun main(args: Array<String>) {
    runApplication<CaffeineCacheDemoApplication>(*args)
}
