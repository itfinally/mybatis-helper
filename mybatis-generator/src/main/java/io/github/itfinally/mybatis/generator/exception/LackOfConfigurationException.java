package io.github.itfinally.mybatis.generator.exception;

public class LackOfConfigurationException extends RuntimeException {
  public LackOfConfigurationException() {
  }

  public LackOfConfigurationException( String message ) {
    super( message );
  }

  public LackOfConfigurationException( String message, Throwable cause ) {
    super( message, cause );
  }

  public LackOfConfigurationException( Throwable cause ) {
    super( cause );
  }

  public LackOfConfigurationException( String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace ) {
    super( message, cause, enableSuppression, writableStackTrace );
  }
}
