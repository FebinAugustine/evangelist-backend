package com.febin.evangelist

import io.github.cdimascio.dotenv.dotenv
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class EvangelistApplication

fun main(args: Array<String>) {
    // Load environment variables from .env file
    dotenv {
        ignoreIfMissing = true
        // Make the loaded variables available to the system
        systemProperties = true
    }
    runApplication<EvangelistApplication>(*args)
}
