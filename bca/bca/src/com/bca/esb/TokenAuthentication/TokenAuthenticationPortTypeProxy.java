package com.bca.esb.TokenAuthentication;

public class TokenAuthenticationPortTypeProxy implements com.bca.esb.TokenAuthentication.TokenAuthenticationPortType {
  private String _endpoint = null;
  private com.bca.esb.TokenAuthentication.TokenAuthenticationPortType tokenAuthenticationPortType = null;
  
  public TokenAuthenticationPortTypeProxy() {
    _initTokenAuthenticationPortTypeProxy();
  }
  
  public TokenAuthenticationPortTypeProxy(String endpoint) {
    _endpoint = endpoint;
    _initTokenAuthenticationPortTypeProxy();
  }
  
  private void _initTokenAuthenticationPortTypeProxy() {
    try {
      tokenAuthenticationPortType = (new com.bca.esb.TokenAuthentication.Ws_TokenAuthentication_ServiceLocator()).getTokenAuthenticationPortTypeBndPort();
      if (tokenAuthenticationPortType != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)tokenAuthenticationPortType)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)tokenAuthenticationPortType)._getProperty("javax.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (tokenAuthenticationPortType != null)
      ((javax.xml.rpc.Stub)tokenAuthenticationPortType)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public com.bca.esb.TokenAuthentication.TokenAuthenticationPortType getTokenAuthenticationPortType() {
    if (tokenAuthenticationPortType == null)
      _initTokenAuthenticationPortTypeProxy();
    return tokenAuthenticationPortType;
  }
  
  public com.bca.esb.TokenAuthentication.output_TokenAuthentication.Output_TokenAuthentication getTokenAuthentication(com.bca.esb.TokenAuthentication.input_TokenAuthentication.Input_TokenAuthentication input) throws java.rmi.RemoteException{
    if (tokenAuthenticationPortType == null)
      _initTokenAuthenticationPortTypeProxy();
    return tokenAuthenticationPortType.getTokenAuthentication(input);
  }
  
  
}