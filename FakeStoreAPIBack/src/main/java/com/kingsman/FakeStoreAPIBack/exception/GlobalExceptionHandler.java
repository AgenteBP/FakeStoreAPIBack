package com.kingsman.FakeStoreAPIBack.exception;

import java.time.LocalDateTime;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import jakarta.servlet.http.HttpServletRequest;

// Atrapa las excepciones que salen de cualquier controller y las convierte en un ErrorResponseDTO
// con el código HTTP correcto. Los services solo lanzan la excepción; acá se decide el status.
@RestControllerAdvice
public class GlobalExceptionHandler {

    // ---- Excepciones propias, lanzadas desde los services ----

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleNotFound(NotFoundException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.NOT_FOUND, getMessage(ex), request);
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ErrorResponseDTO> handleConflict(ConflictException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.CONFLICT, getMessage(ex), request);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponseDTO> handleBadRequest(BadRequestException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.BAD_REQUEST, getMessage(ex), request);
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ErrorResponseDTO> handleForbidden(ForbiddenException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.FORBIDDEN, getMessage(ex), request);
    }

    // ---- Seguridad: login y permisos ----

    // Email o contraseña incorrectos en el login. Mismo mensaje para los dos casos,
    // así no se revela si el email existe o no.
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponseDTO> handleBadCredentials(BadCredentialsException ex,
            HttpServletRequest request) {
        return buildResponse(HttpStatus.UNAUTHORIZED, "Email o contraseña incorrectos.", request);
    }

    // Login de un usuario dado de baja por un ADMIN (baja lógica)
    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<ErrorResponseDTO> handleDisabled(DisabledException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.FORBIDDEN, "Tu cuenta está dada de baja.", request);
    }

    // Endpoint que pide login y el request no trae token, o el token venció
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponseDTO> handleAuthentication(AuthenticationException ex,
            HttpServletRequest request) {
        return buildResponse(HttpStatus.UNAUTHORIZED, "Tenés que iniciar sesión para acceder a este recurso.",
                request);
    }

    // Logueado pero sin el rol necesario (ej. un CLIENT intentando crear un producto)
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponseDTO> handleAccessDenied(AccessDeniedException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.FORBIDDEN, "No tenés permisos para realizar esta acción.", request);
    }

    // ---- Errores de la base de datos ----

    // Respaldo por si la base rechaza algo que el service no validó antes.
    // MySQL usa "Duplicate entry" cuando se repite un valor único (email, sku);
    // cualquier otro caso es una FK: el registro está referenciado o apunta a algo que no existe.
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponseDTO> handleDataIntegrityViolation(DataIntegrityViolationException ex,
            HttpServletRequest request) {
        String cause = ex.getMostSpecificCause().getMessage();
        String message = cause != null && cause.contains("Duplicate entry")
                ? "Ya existe un registro con esos datos únicos."
                : "La operación no se puede realizar porque hay datos relacionados.";

        return buildResponse(HttpStatus.CONFLICT, message, request);
    }

    // ---- Errores de la solicitud, los lanza Spring antes de llegar al controller ----

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponseDTO> handleMissingParameter(MissingServletRequestParameterException ex,
            HttpServletRequest request) {
        String message = "El parametro '" + ex.getParameterName() + "' es obligatorio.";
        return buildResponse(HttpStatus.BAD_REQUEST, message, request);
    }

    // Un parámetro con el tipo equivocado (ej. /api/users/abc)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponseDTO> handleTypeMismatch(MethodArgumentTypeMismatchException ex,
            HttpServletRequest request) {
        String message = "El parametro '" + ex.getName() + "' tiene un valor invalido.";
        return buildResponse(HttpStatus.BAD_REQUEST, message, request);
    }

    // JSON mal formado o un valor de enum que no existe (ej. "status": "ENVIADO")
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponseDTO> handleUnreadableMessage(HttpMessageNotReadableException ex,
            HttpServletRequest request) {
        return buildResponse(HttpStatus.BAD_REQUEST, "El cuerpo de la solicitud es invalido.", request);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponseDTO> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex,
            HttpServletRequest request) {
        return buildResponse(HttpStatus.METHOD_NOT_ALLOWED, "Metodo HTTP no permitido para este endpoint.", request);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleNoResourceFound(NoResourceFoundException ex,
            HttpServletRequest request) {
        return buildResponse(HttpStatus.NOT_FOUND, "Endpoint no encontrado.", request);
    }

    // ---- Cualquier otro error ----

    // Si la excepción tiene @ResponseStatus se respeta ese código; si no, es un error inesperado (500)
    // y no se muestra el mensaje interno al cliente.
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleGenericException(Exception ex, HttpServletRequest request) {
        HttpStatus status = getResponseStatus(ex);
        String message = status == HttpStatus.INTERNAL_SERVER_ERROR
                ? "Ocurrio un error interno en el servidor."
                : getMessage(ex);

        return buildResponse(status, message, request);
    }

    private ResponseEntity<ErrorResponseDTO> buildResponse(HttpStatus status, String message,
            HttpServletRequest request) {
        ErrorResponseDTO error = new ErrorResponseDTO(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                request.getRequestURI());

        return ResponseEntity.status(status).body(error);
    }

    private String getMessage(Exception ex) {
        return ex.getMessage() == null || ex.getMessage().isBlank()
                ? "Error inesperado."
                : ex.getMessage();
    }

    private HttpStatus getResponseStatus(Exception ex) {
        ResponseStatus responseStatus = ex.getClass().getAnnotation(ResponseStatus.class);

        if (responseStatus == null) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }

        return responseStatus.value();
    }
}
