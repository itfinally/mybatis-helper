package io.github.itfinally.mybatis.generator.exception;

public class UnknownNameMappingException extends RuntimeException {
  public UnknownNameMappingException() {
  }

  public UnknownNameMappingException( String message ) {
    super( message );
  }

  public UnknownNameMappingException( String message, Throwable cause ) {
    super( message, cause );
  }

  public UnknownNameMappingException( Throwable cause ) {
    super( cause );
  }

  public UnknownNameMappingException( String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace ) {
    super( message, cause, enableSuppression, writableStackTrace );
  }
}
