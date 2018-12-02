package io.github.itfinally.mybatis.jpa.exception;

public class MissingPrimaryKeyException extends RuntimeException {
  public MissingPrimaryKeyException() {
  }

  public MissingPrimaryKeyException( String message ) {
    super( message );
  }

  public MissingPrimaryKeyException( String message, Throwable cause ) {
    super( message, cause );
  }

  public MissingPrimaryKeyException( Throwable cause ) {
    super( cause );
  }

  public MissingPrimaryKeyException( String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace ) {
    super( message, cause, enableSuppression, writableStackTrace );
  }
}
