//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.3.1 
// See <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
//


package com.example.application.regonApi.wsdl;

import jakarta.xml.bind.annotation.*;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="GetValueResult" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "getValueResult"
})
@XmlRootElement(name = "GetValueResponse", namespace = "http://CIS/BIR/2014/07")
public class GetValueResponse {

    @XmlElement(name = "GetValueResult", namespace = "http://CIS/BIR/2014/07")
    protected String getValueResult;

    /**
     * Gets the value of the getValueResult property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGetValueResult() {
        return getValueResult;
    }

    /**
     * Sets the value of the getValueResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGetValueResult(String value) {
        this.getValueResult = value;
    }

}
