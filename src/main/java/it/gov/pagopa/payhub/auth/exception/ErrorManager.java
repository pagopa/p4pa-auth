//package it.gov.pagopa.payhub.auth.exception;
//
//import it.gov.pagopa.payhub.auth.exception.dto.ErrorDTO;
//import jakarta.servlet.http.HttpServletRequest;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.lang.Nullable;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//import org.springframework.web.bind.annotation.RestControllerAdvice;
//
//import java.util.Map;
//import java.util.Optional;
//
//@RestControllerAdvice
//@Slf4j
//public class ErrorManager {
//  private final ErrorDTO defaultErrorDTO;
//  private final Map<Class<? extends ServiceException>, HttpStatus> transcodeMap;
//
//
//  public ErrorManager(@Nullable ErrorDTO defaultErrorDTO, Map<Class<? extends ServiceException>, HttpStatus> transcodeMap) {
//    this.defaultErrorDTO = Optional.ofNullable(defaultErrorDTO)
//            .orElse(new ErrorDTO("Error", "Something gone wrong"));
//    this.transcodeMap = transcodeMap;
//  }
//  @ExceptionHandler(RuntimeException.class)
//  protected ResponseEntity<ErrorDTO> handleException(RuntimeException error, HttpServletRequest request) {
//    logException(error, request);
//
//    HttpStatus httpStatus;
//    ErrorDTO errorDTO;
//
//    if (error instanceof ServiceException serviceException) {
//      httpStatus = transcodeMap.get(error.getClass());
//      if (httpStatus == null) {
//        log.warn("Unhandled exception: {}", error.getClass().getName());
//        httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
//        errorDTO = defaultErrorDTO;
//      }else {
//        errorDTO = new ErrorDTO(serviceException.getCode(), error.getMessage());
//      }
//    } else {
//      httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
//      errorDTO = defaultErrorDTO;
//    }
//
//    return ResponseEntity.status(httpStatus)
//            .contentType(MediaType.APPLICATION_JSON)
//            .body(errorDTO);
//  }
//
//  public static void logException(RuntimeException error, HttpServletRequest request) {
//    if(!(error instanceof ServiceException serviceException) || serviceException.isPrintStackTrace() || error.getCause() != null){
//      log.error("Something went wrong handling request {}", getRequestDetails(request), error);
//    } else {
//      log.info("A {} occurred handling request {} at {}",
//              error.getClass().getSimpleName() ,
//              getRequestDetails(request),
//              error.getStackTrace().length > 0 ? error.getStackTrace()[0] : "UNKNOWN");
//    }
//  }
//
//  public static String getRequestDetails(HttpServletRequest request) {
//    return "%s %s".formatted(request.getMethod(), request.getRequestURI());
//  }
//
//}
