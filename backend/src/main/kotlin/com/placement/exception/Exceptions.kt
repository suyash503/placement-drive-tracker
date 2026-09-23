package com.placement.exception

// Thrown when something with the given id doesn't exist -> HTTP 404
class NotFoundException(message: String) : RuntimeException(message)

// Thrown when the request breaks a business rule -> HTTP 400
class BadRequestException(message: String) : RuntimeException(message)
