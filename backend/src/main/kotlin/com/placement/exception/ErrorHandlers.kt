package com.placement.exception

import com.placement.dto.ErrorResponse
import io.micronaut.context.annotation.Requires
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.annotation.Produces
import io.micronaut.http.server.exceptions.ExceptionHandler
import jakarta.inject.Singleton

// These classes turn our exceptions into clean JSON like {"status": 404, "message": "..."}
// so the frontend can simply show error.message to the user.

@Produces
@Singleton
@Requires(classes = [NotFoundException::class, ExceptionHandler::class])
class NotFoundHandler : ExceptionHandler<NotFoundException, HttpResponse<ErrorResponse>> {

    override fun handle(request: HttpRequest<*>, exception: NotFoundException): HttpResponse<ErrorResponse> {
        val body = ErrorResponse(404, exception.message ?: "Not found")
        return HttpResponse.status<ErrorResponse>(HttpStatus.NOT_FOUND).body(body)
    }
}

@Produces
@Singleton
@Requires(classes = [BadRequestException::class, ExceptionHandler::class])
class BadRequestHandler : ExceptionHandler<BadRequestException, HttpResponse<ErrorResponse>> {

    override fun handle(request: HttpRequest<*>, exception: BadRequestException): HttpResponse<ErrorResponse> {
        val body = ErrorResponse(400, exception.message ?: "Bad request")
        return HttpResponse.badRequest(body)
    }
}
