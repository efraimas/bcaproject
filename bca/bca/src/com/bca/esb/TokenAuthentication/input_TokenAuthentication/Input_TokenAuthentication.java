/**
 * Input_TokenAuthentication.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.bca.esb.TokenAuthentication.input_TokenAuthentication;

@SuppressWarnings({ "unused", "rawtypes", "serial" })
public class Input_TokenAuthentication  implements java.io.Serializable {
    private java.lang.String clientID;

    private java.lang.String request_trans_type;

    private java.lang.String user_id;

    private java.lang.String random;

    private java.lang.String challenge;

    private java.lang.String counter;

    private java.lang.String clock;

    private java.lang.String random1;

    private java.lang.String random2;

    private java.lang.String akt;

    private java.lang.String challenge_type;

    private java.lang.String type;

    public Input_TokenAuthentication() {
    }

    public Input_TokenAuthentication(
           java.lang.String clientID,
           java.lang.String request_trans_type,
           java.lang.String user_id,
           java.lang.String random,
           java.lang.String challenge,
           java.lang.String counter,
           java.lang.String clock,
           java.lang.String random1,
           java.lang.String random2,
           java.lang.String akt,
           java.lang.String challenge_type,
           java.lang.String type) {
           this.clientID = clientID;
           this.request_trans_type = request_trans_type;
           this.user_id = user_id;
           this.random = random;
           this.challenge = challenge;
           this.counter = counter;
           this.clock = clock;
           this.random1 = random1;
           this.random2 = random2;
           this.akt = akt;
           this.challenge_type = challenge_type;
           this.type = type;
    }


    /**
     * Gets the clientID value for this Input_TokenAuthentication.
     * 
     * @return clientID
     */
    public java.lang.String getClientID() {
        return clientID;
    }


    /**
     * Sets the clientID value for this Input_TokenAuthentication.
     * 
     * @param clientID
     */
    public void setClientID(java.lang.String clientID) {
        this.clientID = clientID;
    }


    /**
     * Gets the request_trans_type value for this Input_TokenAuthentication.
     * 
     * @return request_trans_type
     */
    public java.lang.String getRequest_trans_type() {
        return request_trans_type;
    }


    /**
     * Sets the request_trans_type value for this Input_TokenAuthentication.
     * 
     * @param request_trans_type
     */
    public void setRequest_trans_type(java.lang.String request_trans_type) {
        this.request_trans_type = request_trans_type;
    }


    /**
     * Gets the user_id value for this Input_TokenAuthentication.
     * 
     * @return user_id
     */
    public java.lang.String getUser_id() {
        return user_id;
    }


    /**
     * Sets the user_id value for this Input_TokenAuthentication.
     * 
     * @param user_id
     */
    public void setUser_id(java.lang.String user_id) {
        this.user_id = user_id;
    }


    /**
     * Gets the random value for this Input_TokenAuthentication.
     * 
     * @return random
     */
    public java.lang.String getRandom() {
        return random;
    }


    /**
     * Sets the random value for this Input_TokenAuthentication.
     * 
     * @param random
     */
    public void setRandom(java.lang.String random) {
        this.random = random;
    }


    /**
     * Gets the challenge value for this Input_TokenAuthentication.
     * 
     * @return challenge
     */
    public java.lang.String getChallenge() {
        return challenge;
    }


    /**
     * Sets the challenge value for this Input_TokenAuthentication.
     * 
     * @param challenge
     */
    public void setChallenge(java.lang.String challenge) {
        this.challenge = challenge;
    }


    /**
     * Gets the counter value for this Input_TokenAuthentication.
     * 
     * @return counter
     */
    public java.lang.String getCounter() {
        return counter;
    }


    /**
     * Sets the counter value for this Input_TokenAuthentication.
     * 
     * @param counter
     */
    public void setCounter(java.lang.String counter) {
        this.counter = counter;
    }


    /**
     * Gets the clock value for this Input_TokenAuthentication.
     * 
     * @return clock
     */
    public java.lang.String getClock() {
        return clock;
    }


    /**
     * Sets the clock value for this Input_TokenAuthentication.
     * 
     * @param clock
     */
    public void setClock(java.lang.String clock) {
        this.clock = clock;
    }


    /**
     * Gets the random1 value for this Input_TokenAuthentication.
     * 
     * @return random1
     */
    public java.lang.String getRandom1() {
        return random1;
    }


    /**
     * Sets the random1 value for this Input_TokenAuthentication.
     * 
     * @param random1
     */
    public void setRandom1(java.lang.String random1) {
        this.random1 = random1;
    }


    /**
     * Gets the random2 value for this Input_TokenAuthentication.
     * 
     * @return random2
     */
    public java.lang.String getRandom2() {
        return random2;
    }


    /**
     * Sets the random2 value for this Input_TokenAuthentication.
     * 
     * @param random2
     */
    public void setRandom2(java.lang.String random2) {
        this.random2 = random2;
    }


    /**
     * Gets the akt value for this Input_TokenAuthentication.
     * 
     * @return akt
     */
    public java.lang.String getAkt() {
        return akt;
    }


    /**
     * Sets the akt value for this Input_TokenAuthentication.
     * 
     * @param akt
     */
    public void setAkt(java.lang.String akt) {
        this.akt = akt;
    }


    /**
     * Gets the challenge_type value for this Input_TokenAuthentication.
     * 
     * @return challenge_type
     */
    public java.lang.String getChallenge_type() {
        return challenge_type;
    }


    /**
     * Sets the challenge_type value for this Input_TokenAuthentication.
     * 
     * @param challenge_type
     */
    public void setChallenge_type(java.lang.String challenge_type) {
        this.challenge_type = challenge_type;
    }


    /**
     * Gets the type value for this Input_TokenAuthentication.
     * 
     * @return type
     */
    public java.lang.String getType() {
        return type;
    }


    /**
     * Sets the type value for this Input_TokenAuthentication.
     * 
     * @param type
     */
    public void setType(java.lang.String type) {
        this.type = type;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof Input_TokenAuthentication)) return false;
        Input_TokenAuthentication other = (Input_TokenAuthentication) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.clientID==null && other.getClientID()==null) || 
             (this.clientID!=null &&
              this.clientID.equals(other.getClientID()))) &&
            ((this.request_trans_type==null && other.getRequest_trans_type()==null) || 
             (this.request_trans_type!=null &&
              this.request_trans_type.equals(other.getRequest_trans_type()))) &&
            ((this.user_id==null && other.getUser_id()==null) || 
             (this.user_id!=null &&
              this.user_id.equals(other.getUser_id()))) &&
            ((this.random==null && other.getRandom()==null) || 
             (this.random!=null &&
              this.random.equals(other.getRandom()))) &&
            ((this.challenge==null && other.getChallenge()==null) || 
             (this.challenge!=null &&
              this.challenge.equals(other.getChallenge()))) &&
            ((this.counter==null && other.getCounter()==null) || 
             (this.counter!=null &&
              this.counter.equals(other.getCounter()))) &&
            ((this.clock==null && other.getClock()==null) || 
             (this.clock!=null &&
              this.clock.equals(other.getClock()))) &&
            ((this.random1==null && other.getRandom1()==null) || 
             (this.random1!=null &&
              this.random1.equals(other.getRandom1()))) &&
            ((this.random2==null && other.getRandom2()==null) || 
             (this.random2!=null &&
              this.random2.equals(other.getRandom2()))) &&
            ((this.akt==null && other.getAkt()==null) || 
             (this.akt!=null &&
              this.akt.equals(other.getAkt()))) &&
            ((this.challenge_type==null && other.getChallenge_type()==null) || 
             (this.challenge_type!=null &&
              this.challenge_type.equals(other.getChallenge_type()))) &&
            ((this.type==null && other.getType()==null) || 
             (this.type!=null &&
              this.type.equals(other.getType())));
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        if (getClientID() != null) {
            _hashCode += getClientID().hashCode();
        }
        if (getRequest_trans_type() != null) {
            _hashCode += getRequest_trans_type().hashCode();
        }
        if (getUser_id() != null) {
            _hashCode += getUser_id().hashCode();
        }
        if (getRandom() != null) {
            _hashCode += getRandom().hashCode();
        }
        if (getChallenge() != null) {
            _hashCode += getChallenge().hashCode();
        }
        if (getCounter() != null) {
            _hashCode += getCounter().hashCode();
        }
        if (getClock() != null) {
            _hashCode += getClock().hashCode();
        }
        if (getRandom1() != null) {
            _hashCode += getRandom1().hashCode();
        }
        if (getRandom2() != null) {
            _hashCode += getRandom2().hashCode();
        }
        if (getAkt() != null) {
            _hashCode += getAkt().hashCode();
        }
        if (getChallenge_type() != null) {
            _hashCode += getChallenge_type().hashCode();
        }
        if (getType() != null) {
            _hashCode += getType().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(Input_TokenAuthentication.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://esb.bca.com/TokenAuthentication/input_TokenAuthentication", "input_TokenAuthentication"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("clientID");
        elemField.setXmlName(new javax.xml.namespace.QName("", "ClientID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("request_trans_type");
        elemField.setXmlName(new javax.xml.namespace.QName("", "request_trans_type"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("user_id");
        elemField.setXmlName(new javax.xml.namespace.QName("", "user_id"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("random");
        elemField.setXmlName(new javax.xml.namespace.QName("", "random"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("challenge");
        elemField.setXmlName(new javax.xml.namespace.QName("", "challenge"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("counter");
        elemField.setXmlName(new javax.xml.namespace.QName("", "counter"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("clock");
        elemField.setXmlName(new javax.xml.namespace.QName("", "clock"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("random1");
        elemField.setXmlName(new javax.xml.namespace.QName("", "random1"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("random2");
        elemField.setXmlName(new javax.xml.namespace.QName("", "random2"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("akt");
        elemField.setXmlName(new javax.xml.namespace.QName("", "akt"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("challenge_type");
        elemField.setXmlName(new javax.xml.namespace.QName("", "challenge_type"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("type");
        elemField.setXmlName(new javax.xml.namespace.QName("", "type"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
    }

    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

    /**
     * Get Custom Serializer
     */
    public static org.apache.axis.encoding.Serializer getSerializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanSerializer(
            _javaType, _xmlType, typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanDeserializer(
            _javaType, _xmlType, typeDesc);
    }

}
