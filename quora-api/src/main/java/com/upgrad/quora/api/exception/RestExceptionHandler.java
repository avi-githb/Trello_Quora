package com.upgrad.quora.api.exception;

import com.upgrad.quora.api.model.ErrorResponse;
import com.upgrad.quora.service.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

/**
 * All the Exceptions are called from this Rest Exception Handler class, and the required Status code is generated.
 */

@ControllerAdvice
public class RestExceptionHandler {

    /**
     * SignUpRestrictedException - HttpStatus.CONFLICT
     *
     * @param exc
     * @param request
     * @return
     */

    @ExceptionHandler(SignUpRestrictedException.class)
    public ResponseEntity<ErrorResponse> SignUpRestrictedException(SignUpRestrictedException exc, WebRequest request) {
        return new ResponseEntity<ErrorResponse>(new ErrorResponse().code(exc.getCode()).message(exc.getErrorMessage()), HttpStatus.CONFLICT);
    }

    /**
     * AuthenticationFailedException - HttpStatus.UNAUTHORIZED
     *
     * @param afe
     * @param request
     * @return
     */

    @ExceptionHandler(AuthenticationFailedException.class)
    public ResponseEntity<ErrorResponse> AuthenticationFailedException(AuthenticationFailedException afe, WebRequest request) {
        return new ResponseEntity<ErrorResponse>(new ErrorResponse().code(afe.getCode()).message(afe.getErrorMessage()), HttpStatus.UNAUTHORIZED);
    }

    /**
     * SignOutRestrictedException - HttpStatus.NOT_FOUND
     *
     * @param sre
     * @param request
     * @return
     */

    @ExceptionHandler(SignOutRestrictedException.class)
    public ResponseEntity<ErrorResponse> SignOutRestrictedException(SignOutRestrictedException sre, WebRequest request) {
        return new ResponseEntity<ErrorResponse>(new ErrorResponse().code(sre.getCode()).message(sre.getErrorMessage()), HttpStatus.UNAUTHORIZED);
    }

    /**
     * AuthorizationFailedException - HttpStatus.FORBIDDEN
     *
     * @param authfe
     * @param request
     * @return
     */

    @ExceptionHandler(AuthorizationFailedException.class)
    public ResponseEntity<ErrorResponse> AuthorizationFailedException(AuthorizationFailedException authfe, WebRequest request) {
        return new ResponseEntity<ErrorResponse>(new ErrorResponse().code(authfe.getCode()).message(authfe.getErrorMessage()), HttpStatus.FORBIDDEN);
    }

    /**
     * InvalidQuestionException - HttpStatus.NOT_FOUND
     *
     * @param iqe
     * @param request
     * @return
     */

    @ExceptionHandler(InvalidQuestionException.class)
    public ResponseEntity<ErrorResponse> InvalidQuestionException(InvalidQuestionException iqe, WebRequest request) {
        return new ResponseEntity<ErrorResponse>(new ErrorResponse().code(iqe.getCode()).message(iqe.getErrorMessage()), HttpStatus.NOT_FOUND);
    }

    /**
     * UserNotFoundException - HttpStatus.NOT_FOUND
     *
     * @param unf
     * @param request
     * @return
     */

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> UserNotFoundException(UserNotFoundException unf, WebRequest request) {
        return new ResponseEntity<ErrorResponse>(new ErrorResponse().code(unf.getCode()).message(unf.getErrorMessage()), HttpStatus.NOT_FOUND);
    }
}
