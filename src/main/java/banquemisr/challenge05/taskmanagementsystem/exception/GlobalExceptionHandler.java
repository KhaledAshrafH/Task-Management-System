package banquemisr.challenge05.taskmanagementsystem.exception;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.NoSuchElementException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<String> handleInvalidTokenException(InvalidTokenException ex) {
        log.warn("Invalid Token: {}", ex.getMessage());
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(UsernameAlreadyExistsException.class)
    public ResponseEntity<ProblemDetail> handleUsernameAlreadyExists(UsernameAlreadyExistsException ex) {
        log.warn("Registration failed: {}", ex.getMessage());
        return createProblemDetailResponse(HttpStatus.CONFLICT, ex.getMessage(), "Registration failed: Username already exists.");
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ProblemDetail> handleEmailAlreadyExists(EmailAlreadyExistsException ex) {
        log.warn("Registration failed : {}", ex.getMessage());
        return createProblemDetailResponse(HttpStatus.CONFLICT, ex.getMessage(), "Registration failed: Email already exists.");
    }

    @ExceptionHandler(InvalidInputException.class)
    public ResponseEntity<ProblemDetail> handleInvalidInput(InvalidInputException ex) {
        log.warn("Invalid input: {}", ex.getMessage());
        return createProblemDetailResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), "Invalid input provided.");
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ProblemDetail> handleMissingParameterException(MissingServletRequestParameterException ex) {
        log.warn("Missing request parameter: {}", ex.getMessage());
        return createProblemDetailResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), "Required request parameter is missing.");
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ProblemDetail> handleBadCredentialsException(BadCredentialsException ex) {
        log.warn("Authentication failed: {}", ex.getMessage());
        return createProblemDetailResponse(HttpStatus.UNAUTHORIZED, ex.getMessage(), "The username or password is incorrect.");
    }

    @ExceptionHandler(AccountStatusException.class)
    public ResponseEntity<ProblemDetail> handleAccountStatusException(AccountStatusException ex) {
        log.warn("Account status issue: {}", ex.getMessage());
        return createProblemDetailResponse(HttpStatus.FORBIDDEN, ex.getMessage(), "The account is locked.");
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ProblemDetail> handleAccessDeniedException(AccessDeniedException ex) {
        log.warn("Access denied: {}", ex.getMessage());
        return createProblemDetailResponse(HttpStatus.FORBIDDEN, ex.getMessage(), "You are not authorized to access this resource.");
    }

    @ExceptionHandler(SignatureException.class)
    public ResponseEntity<ProblemDetail> handleSignatureException(SignatureException ex) {
        log.warn("Invalid JWT signature: {}", ex.getMessage());
        return createProblemDetailResponse(HttpStatus.FORBIDDEN, ex.getMessage(), "The JWT signature is invalid.");
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<ProblemDetail> handleExpiredJwtException(ExpiredJwtException ex) {
        log.warn("Expired JWT token: {}", ex.getMessage());
        return createProblemDetailResponse(HttpStatus.FORBIDDEN, ex.getMessage(), "The JWT token has expired.");
    }

    @ExceptionHandler(UnauthorizedAccessException.class)
    public ResponseEntity<ProblemDetail> handleUnauthorizedAccess(UnauthorizedAccessException ex) {
        log.warn("Unauthorized Access: {}", ex.getMessage());
        return createProblemDetailResponse(HttpStatus.FORBIDDEN, ex.getMessage(), "Unauthorized Access!");
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ProblemDetail> handleUnauthorizedAccess(NoSuchElementException ex) {
        log.warn("This Element Not found!: {}", ex.getMessage());
        return createProblemDetailResponse(HttpStatus.NOT_FOUND, ex.getMessage(), "This Element Not found!");
    }

    @ExceptionHandler(TaskNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleTaskNotFound(TaskNotFoundException ex) {
        log.warn("This Task Not found!: {}", ex.getMessage());
        return createProblemDetailResponse(HttpStatus.NOT_FOUND, ex.getMessage(), "This Task Not found!");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleGenericException(Exception ex) {
        log.error("An unexpected error occurred: {}", ex.getMessage(), ex);
        return createProblemDetailResponse(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred.", "Unknown internal server error.");
    }

    private ResponseEntity<ProblemDetail> createProblemDetailResponse(HttpStatus status, String message, String description) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, message);
        problemDetail.setProperty("description", description);
        return ResponseEntity.status(status).body(problemDetail);
    }
}