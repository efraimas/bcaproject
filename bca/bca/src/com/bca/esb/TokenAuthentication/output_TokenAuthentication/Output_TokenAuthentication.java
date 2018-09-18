/**
 * Output_TokenAuthentication.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.bca.esb.TokenAuthentication.output_TokenAuthentication;

@SuppressWarnings({ "unused", "rawtypes", "serial" })
public class Output_TokenAuthentication  implements java.io.Serializable {
    private java.lang.String ERROR_MESSAGE;

    private com.bca.esb.TokenAuthentication.output_TokenAuthentication.Output_Success output_Success;

    private com.bca.esb.TokenAuthentication.output_TokenAuthentication.Output_Failed output_Failed;

    public Output_TokenAuthentication() {
    }

    public Output_TokenAuthentication(
           java.lang.String ERROR_MESSAGE,
           com.bca.esb.TokenAuthentication.output_TokenAuthentication.Output_Success output_Success,
           com.bca.esb.TokenAuthentication.output_TokenAuthentication.Output_Failed output_Failed) {
           this.ERROR_MESSAGE = ERROR_MESSAGE;
           this.output_Success = output_Success;
           this.output_Failed = output_Failed;
    }


    /**
     * Gets the ERROR_MESSAGE value for this Output_TokenAuthentication.
     * 
     * @return ERROR_MESSAGE
     */
    public java.lang.String getERROR_MESSAGE() {
        return ERROR_MESSAGE;
    }


    /**
     * Sets the ERROR_MESSAGE value for this Output_TokenAuthentication.
     * 
     * @param ERROR_MESSAGE
     */
    public void setERROR_MESSAGE(java.lang.String ERROR_MESSAGE) {
        this.ERROR_MESSAGE = ERROR_MESSAGE;
    }


    /**
     * Gets the output_Success value for this Output_TokenAuthentication.
     * 
     * @return output_Success
     */
    public com.bca.esb.TokenAuthentication.output_TokenAuthentication.Output_Success getOutput_Success() {
        return output_Success;
    }


    /**
     * Sets the output_Success value for this Output_TokenAuthentication.
     * 
     * @param output_Success
     */
    public void setOutput_Success(com.bca.esb.TokenAuthentication.output_TokenAuthentication.Output_Success output_Success) {
        this.output_Success = output_Success;
    }


    /**
     * Gets the output_Failed value for this Output_TokenAuthentication.
     * 
     * @return output_Failed
     */
    public com.bca.esb.TokenAuthentication.output_TokenAuthentication.Output_Failed getOutput_Failed() {
        return output_Failed;
    }


    /**
     * Sets the output_Failed value for this Output_TokenAuthentication.
     * 
     * @param output_Failed
     */
    public void setOutput_Failed(com.bca.esb.TokenAuthentication.output_TokenAuthentication.Output_Failed output_Failed) {
        this.output_Failed = output_Failed;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof Output_TokenAuthentication)) return false;
        Output_TokenAuthentication other = (Output_TokenAuthentication) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.ERROR_MESSAGE==null && other.getERROR_MESSAGE()==null) || 
             (this.ERROR_MESSAGE!=null &&
              this.ERROR_MESSAGE.equals(other.getERROR_MESSAGE()))) &&
            ((this.output_Success==null && other.getOutput_Success()==null) || 
             (this.output_Success!=null &&
              this.output_Success.equals(other.getOutput_Success()))) &&
            ((this.output_Failed==null && other.getOutput_Failed()==null) || 
             (this.output_Failed!=null &&
              this.output_Failed.equals(other.getOutput_Failed())));
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
        if (getERROR_MESSAGE() != null) {
            _hashCode += getERROR_MESSAGE().hashCode();
        }
        if (getOutput_Success() != null) {
            _hashCode += getOutput_Success().hashCode();
        }
        if (getOutput_Failed() != null) {
            _hashCode += getOutput_Failed().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(Output_TokenAuthentication.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://esb.bca.com/TokenAuthentication/output_TokenAuthentication", "output_TokenAuthentication"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ERROR_MESSAGE");
        elemField.setXmlName(new javax.xml.namespace.QName("", "ERROR_MESSAGE"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("output_Success");
        elemField.setXmlName(new javax.xml.namespace.QName("", "Output_Success"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://esb.bca.com/TokenAuthentication/output_TokenAuthentication", "Output_Success"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("output_Failed");
        elemField.setXmlName(new javax.xml.namespace.QName("", "Output_Failed"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://esb.bca.com/TokenAuthentication/output_TokenAuthentication", "Output_Failed"));
        elemField.setMinOccurs(0);
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
