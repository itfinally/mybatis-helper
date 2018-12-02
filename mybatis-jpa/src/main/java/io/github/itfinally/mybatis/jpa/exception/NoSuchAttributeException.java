package io.github.itfinally.mybatis.jpa.exception;

public class NoSuchAttributeException extends RuntimeException {
  public NoSuchAttributeException() {
  }

  public NoSuchAttributeException( String message ) {
    super( message );
  }

  public NoSuchAttributeException( String message, Throwable cause ) {
    super( message, cause );
  }

  public NoSuchAttributeException( Throwable cause ) {
    super( cause );
  }

  public NoSuchAttributeException( String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace ) {
    super( message, cause, enableSuppression, writableStackTrace );
  }
}
