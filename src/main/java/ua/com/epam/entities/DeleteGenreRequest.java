//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.3.0 
// See <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2020.02.23 at 02:19:17 AM EET 
//


package ua.com.epam.entities;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


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
 *         &lt;element name="genreId" type="{http://www.w3.org/2001/XMLSchema}long"/&gt;
 *         &lt;element name="options" type="{libraryService}DeleteParams"/&gt;
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
    "genreId",
    "options"
})
@XmlRootElement(name = "deleteGenreRequest")
public class DeleteGenreRequest {

    protected long genreId;
    @XmlElement(required = true)
    protected DeleteParams options;

    /**
     * Gets the value of the genreId property.
     * 
     */
    public long getGenreId() {
        return genreId;
    }

    /**
     * Sets the value of the genreId property.
     * 
     */
    public void setGenreId(long value) {
        this.genreId = value;
    }

    /**
     * Gets the value of the options property.
     * 
     * @return
     *     possible object is
     *     {@link DeleteParams }
     *     
     */
    public DeleteParams getOptions() {
        return options;
    }

    /**
     * Sets the value of the options property.
     * 
     * @param value
     *     allowed object is
     *     {@link DeleteParams }
     *     
     */
    public void setOptions(DeleteParams value) {
        this.options = value;
    }

}