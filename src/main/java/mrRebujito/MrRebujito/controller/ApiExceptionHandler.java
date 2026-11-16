package mrRebujito.MrRebujito.controller;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;
@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String,Object>> validation(MethodArgumentNotValidException ex){
        return error(HttpStatus.BAD_REQUEST,ex.getBindingResult().getFieldErrors().stream()
            .map(e->e.getField()+": "+e.getDefaultMessage()).collect(Collectors.joining("; ")));
    }
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Map<String,Object>> authentication(AuthenticationException ex){
        return error(HttpStatus.UNAUTHORIZED,"Credenciales incorrectas");
    }
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String,Object>> integrity(DataIntegrityViolationException ex){
        return error(HttpStatus.CONFLICT,"Operación incompatible con datos relacionados o repetidos");
    }
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String,Object>> conflict(IllegalStateException ex){
        return error(HttpStatus.CONFLICT,ex.getMessage());
    }
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String,Object>> status(ResponseStatusException ex){
        Map<String,Object> body=new LinkedHashMap<>();
        body.put("timestamp",Instant.now().toString());
        body.put("status",ex.getStatusCode().value());
        body.put("message",ex.getReason());
        return ResponseEntity.status(ex.getStatusCode()).body(body);
    }
    private ResponseEntity<Map<String,Object>> error(HttpStatus status,String message){
        Map<String,Object> body=new LinkedHashMap<>();
        body.put("timestamp",Instant.now().toString());body.put("status",status.value());body.put("message",message);
        return ResponseEntity.status(status).body(body);
    }
}
