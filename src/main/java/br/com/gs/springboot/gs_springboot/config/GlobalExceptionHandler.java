package br.com.gs.springboot.gs_springboot.config;

import br.com.gs.springboot.gs_springboot.dto.errors.ErrorResponse;
import br.com.gs.springboot.gs_springboot.dto.errors.ErrorValidation;
import br.com.gs.springboot.gs_springboot.dto.errors.FieldError;
import br.com.gs.springboot.gs_springboot.exception.*;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handlerRecursoNaoEncontrado(ResourceNotFoundException ex){
        return buildError(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorValidation> handlerArgumentoInvalido(MethodArgumentNotValidException ex){

        List<FieldError> camposErros = ex.getBindingResult().getFieldErrors().stream()
                .map(campoErro -> new FieldError(campoErro.getField(), campoErro.getDefaultMessage()))
                .toList();

        List<FieldError> errosGlobais = ex.getBindingResult().getGlobalErrors().stream()
                .map(erroGlobal -> new FieldError(erroGlobal.getObjectName(), erroGlobal.getDefaultMessage()))
                .toList();

        List<FieldError> erros = Stream.concat(camposErros.stream(), errosGlobais.stream()).toList();

        ErrorValidation bodyErros = new ErrorValidation(
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                HttpStatus.BAD_REQUEST.value(),
                erros,
                LocalDateTime.now()
        );

        return ResponseEntity.badRequest().body(bodyErros);
    }

    @ExceptionHandler(InvalidStatusTransitionException.class)
    public ResponseEntity<ErrorResponse> handlerTransicaoInvalidaStatus(InvalidStatusTransitionException ex){
        return buildError(HttpStatus.UNPROCESSABLE_CONTENT, ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handlerParametroInvalido(MethodArgumentTypeMismatchException ex){
        return buildError(HttpStatus.BAD_REQUEST, "Parâmetro: (" +  ex.getName() + ") inválido!");
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handlerConstraintValidation(ConstraintViolationException ex){
        String mensagem = ex.getConstraintViolations().stream()
                .map(valor -> valor.getPropertyPath() + ": " + valor.getMessage())
                .collect(Collectors.joining(";"));
        return buildError(HttpStatus.BAD_REQUEST, mensagem);
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ErrorValidation> handlerValidation(HandlerMethodValidationException ex){
        List<FieldError> errosGlobais = ex.getParameterValidationResults().getFirst().getResolvableErrors()
                .stream()
                .map(erroGlobal -> new FieldError(
                        "Validação",
                        erroGlobal.getDefaultMessage()
                ))
                .toList();

        ErrorValidation bodyErros = new ErrorValidation(
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                HttpStatus.BAD_REQUEST.value(),
                errosGlobais,
                LocalDateTime.now()
        );

        return ResponseEntity.badRequest().body(bodyErros);
    }

    @ExceptionHandler(org.springframework.http.converter.HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handlerJsonInvalido(Exception ex) {
        return buildError(HttpStatus.BAD_REQUEST, "JSON inválido ou campo com tipo incorreto.");
    }

    @ExceptionHandler(org.springframework.web.bind.MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handlerParametroAusente(Exception ex) {
        return buildError(HttpStatus.BAD_REQUEST, "Parâmetro obrigatório ausente.");
    }

    @ExceptionHandler(org.springframework.dao.DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handlerConflito(Exception ex) {
        return buildError(HttpStatus.CONFLICT, "Registro duplicado ou vínculo impede a operação.");
    }

    @ExceptionHandler(org.springframework.web.server.ResponseStatusException.class)
    public ResponseEntity<ErrorResponse> handlerStatus(org.springframework.web.server.ResponseStatusException ex) {
        return buildError(HttpStatus.valueOf(ex.getStatusCode().value()), ex.getReason());
    }

    @ExceptionHandler(EmailRegistradoException.class)
    public ResponseEntity<ErrorResponse> handlerEmailRegistrado(EmailRegistradoException ex) {
        return buildError(HttpStatus.CONFLICT, ex.getMessage());
    }

    // Senha errada ou e-mail inexistente: 401. Sem este handler, o handler genérico responderia 500.
    @ExceptionHandler(org.springframework.security.core.AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handlerAutenticacao(Exception ex) {
        return buildError(HttpStatus.UNAUTHORIZED, "Credenciais inválidas ou acesso não autorizado.");
    }

    @ExceptionHandler(CepNaoEncontradoException.class)
    public ResponseEntity<ErrorResponse> handlerCepNaoEncontrado(CepNaoEncontradoException ex) {
        return buildError(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(GoogleMapsException.class)
    public ResponseEntity<ErrorResponse> handlerGoogleMapsErro(GoogleMapsException ex) {
        return buildError(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handlerGenerico(Exception ex){
        return buildError(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno inesperado.");
    }

    private ResponseEntity<ErrorResponse> buildError(HttpStatus statusCode, String mensagem){
        return ResponseEntity.status(statusCode)
                .body(new ErrorResponse(mensagem, statusCode.getReasonPhrase(), statusCode.value(), LocalDateTime.now()));
    }
}
