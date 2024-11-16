package banquemisr.challenge05.taskmanagementsystem.exception;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private ProblemDetail createProblemDetail(HttpStatus status, String message, String description) {
        ProblemDetail errorDetail = ProblemDetail.forStatusAndDetail(status, message);
        errorDetail.setProperty("description", description);
        return errorDetail;
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ProblemDetail handleMissingParameterException(MissingServletRequestParameterException exception) {
        return createProblemDetail(HttpStatus.BAD_REQUEST, exception.getMessage(), "Required request parameter is missing.");
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ProblemDetail handleBadCredentialsException(BadCredentialsException exception) {
        return createProblemDetail(HttpStatus.UNAUTHORIZED, exception.getMessage(), "The username or password is incorrect");
    }

    @ExceptionHandler(AccountStatusException.class)
    public ProblemDetail handleAccountStatusException(AccountStatusException exception) {
        return createProblemDetail(HttpStatus.FORBIDDEN, exception.getMessage(), "The account is locked");
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ProblemDetail handleAccessDeniedException(AccessDeniedException exception) {
        return createProblemDetail(HttpStatus.FORBIDDEN, exception.getMessage(), "You are not authorized to access this resource");
    }

    @ExceptionHandler(SignatureException.class)
    public ProblemDetail handleSignatureException(SignatureException exception) {
        return createProblemDetail(HttpStatus.FORBIDDEN, exception.getMessage(), "The JWT signature is invalid");
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public ProblemDetail handleExpiredJwtException(ExpiredJwtException exception) {
        return createProblemDetail(HttpStatus.FORBIDDEN, exception.getMessage(), "The JWT token has expired");
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGenericException(Exception exception) {
        return createProblemDetail(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage(), "Unknown internal server error.");
    }
}