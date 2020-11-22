package fi.nuortimo.typetalkbot.config

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler


@ControllerAdvice
class ExceptionHandler : ResponseEntityExceptionHandler() {

    private val errorLogger: Logger = LogManager.getLogger(ExceptionHandler::class.java)

    @ExceptionHandler(value = [Exception::class])
    protected fun handleConflict(ex: RuntimeException, request: WebRequest): ResponseEntity<Nothing?> {
        errorLogger.info("Exception handling a request: ${ex.message}")
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
    }
}