package io.github.itfinally.mybatis.jpa.exception;

public class DuplicatePrimaryKeyException extends RuntimeException {
  public DuplicatePrimaryKeyException() {
  }

  public DuplicatePrimaryKeyException( String message ) {
    super( message );
  }

  public DuplicatePrimaryKeyException( String message, Throwable cause ) {
    super( message, cause );
  }

  public DuplicatePrimaryKeyException( Throwable cause ) {
    super( cause );
  }

  public DuplicatePrimaryKeyException( String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace ) {
    super( message, cause, enableSuppression, writableStackTrace );
  }
}
