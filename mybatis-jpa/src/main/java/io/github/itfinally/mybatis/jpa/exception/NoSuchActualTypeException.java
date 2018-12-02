package io.github.itfinally.mybatis.jpa.exception;

public class NoSuchActualTypeException extends RuntimeException {
  public NoSuchActualTypeException() {
  }

  public NoSuchActualTypeException( String message ) {
    super( message );
  }

  public NoSuchActualTypeException( String message, Throwable cause ) {
    super( message, cause );
  }

  public NoSuchActualTypeException( Throwable cause ) {
    super( cause );
  }

  public NoSuchActualTypeException( String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace ) {
    super( message, cause, enableSuppression, writableStackTrace );
  }
}
