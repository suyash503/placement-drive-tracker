package com.placement

import io.micronaut.runtime.Micronaut.run
import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.info.Info
import org.slf4j.bridge.SLF4JBridgeHandler

// Shows up at the top of the Swagger UI page (http://localhost:8080/swagger-ui)
@OpenAPIDefinition(
    info = Info(
        title = "Placement Drive Tracker API",
        version = "1.0",
        description = "Backend for a college Training and Placement cell"
    )
)
object Api

fun main(args: Array<String>) {
    SLF4JBridgeHandler.removeHandlersForRootLogger()
    SLF4JBridgeHandler.install()
    run(*args)
}
