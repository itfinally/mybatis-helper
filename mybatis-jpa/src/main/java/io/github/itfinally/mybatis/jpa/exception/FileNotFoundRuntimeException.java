package io.github.itfinally.mybatis.jpa.exception;

public class FileNotFoundRuntimeException extends RuntimeException {
  public FileNotFoundRuntimeException() {
  }

  public FileNotFoundRuntimeException( String message ) {
    super( message );
  }

  public FileNotFoundRuntimeException( String message, Throwable cause ) {
    super( message, cause );
  }

  public FileNotFoundRuntimeException( Throwable cause ) {
    super( cause );
  }

  public FileNotFoundRuntimeException( String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace ) {
    super( message, cause, enableSuppression, writableStackTrace );
  }
}
