/**
 * Ws_TokenAuthentication_ServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.bca.esb.TokenAuthentication;

@SuppressWarnings({ "rawtypes", "unchecked", "serial" })
public class Ws_TokenAuthentication_ServiceLocator extends org.apache.axis.client.Service implements com.bca.esb.TokenAuthentication.Ws_TokenAuthentication_Service {

    public Ws_TokenAuthentication_ServiceLocator() {
    }


    public Ws_TokenAuthentication_ServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public Ws_TokenAuthentication_ServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for TokenAuthenticationPortTypeBndPort  
    //private java.lang.String TokenAuthenticationPortTypeBndPort_address = "http://10.20.200.140:9403/TokenAuthentication/TokenAuthenticationPortTypeBndPort"; // dev
    //private java.lang.String TokenAuthenticationPortTypeBndPort_address = "http://10.20.200.142:9303/TokenAuthentication/TokenAuthenticationPortTypeBndPort";  // uat
    private java.lang.String TokenAuthenticationPortTypeBndPort_address = "http://10.16.50.55:9303/TokenAuthentication/TokenAuthenticationPortTypeBndPort"; //Production
    
    public java.lang.String getTokenAuthenticationPortTypeBndPortAddress() {
        return TokenAuthenticationPortTypeBndPort_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String TokenAuthenticationPortTypeBndPortWSDDServiceName = "TokenAuthenticationPortTypeBndPort";

    public java.lang.String getTokenAuthenticationPortTypeBndPortWSDDServiceName() {
        return TokenAuthenticationPortTypeBndPortWSDDServiceName;
    }

    public void setTokenAuthenticationPortTypeBndPortWSDDServiceName(java.lang.String name) {
        TokenAuthenticationPortTypeBndPortWSDDServiceName = name;
    }

    public com.bca.esb.TokenAuthentication.TokenAuthenticationPortType getTokenAuthenticationPortTypeBndPort() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(TokenAuthenticationPortTypeBndPort_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getTokenAuthenticationPortTypeBndPort(endpoint);
    }

    public com.bca.esb.TokenAuthentication.TokenAuthenticationPortType getTokenAuthenticationPortTypeBndPort(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            com.bca.esb.TokenAuthentication.TokenAuthenticationPortTypeBndStub _stub = new com.bca.esb.TokenAuthentication.TokenAuthenticationPortTypeBndStub(portAddress, this);
            _stub.setPortName(getTokenAuthenticationPortTypeBndPortWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setTokenAuthenticationPortTypeBndPortEndpointAddress(java.lang.String address) {
        TokenAuthenticationPortTypeBndPort_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (com.bca.esb.TokenAuthentication.TokenAuthenticationPortType.class.isAssignableFrom(serviceEndpointInterface)) {
                com.bca.esb.TokenAuthentication.TokenAuthenticationPortTypeBndStub _stub = new com.bca.esb.TokenAuthentication.TokenAuthenticationPortTypeBndStub(new java.net.URL(TokenAuthenticationPortTypeBndPort_address), this);
                _stub.setPortName(getTokenAuthenticationPortTypeBndPortWSDDServiceName());
                return _stub;
            }
        }
        catch (java.lang.Throwable t) {
            throw new javax.xml.rpc.ServiceException(t);
        }
        throw new javax.xml.rpc.ServiceException("There is no stub implementation for the interface:  " + (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName()));
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        if (portName == null) {
            return getPort(serviceEndpointInterface);
        }
        java.lang.String inputPortName = portName.getLocalPart();
        if ("TokenAuthenticationPortTypeBndPort".equals(inputPortName)) {
            return getTokenAuthenticationPortTypeBndPort();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://esb.bca.com/TokenAuthentication", "ws_TokenAuthentication_Service");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://esb.bca.com/TokenAuthentication", "TokenAuthenticationPortTypeBndPort"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("TokenAuthenticationPortTypeBndPort".equals(portName)) {
            setTokenAuthenticationPortTypeBndPortEndpointAddress(address);
        }
        else 
{ // Unknown Port Name
            throw new javax.xml.rpc.ServiceException(" Cannot set Endpoint Address for Unknown Port" + portName);
        }
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(javax.xml.namespace.QName portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        setEndpointAddress(portName.getLocalPart(), address);
    }

}
