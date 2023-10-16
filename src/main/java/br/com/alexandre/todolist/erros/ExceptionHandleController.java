package br.com.alexandre.todolist.erros;

import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionHandleController {

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<String> handle(HttpMessageNotReadableException exception) {
    return ResponseEntity.badRequest().body(exception.getMostSpecificCause().getMessage());
  }
}
