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
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for GenreType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GenreType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="genreId" type="{http://www.w3.org/2001/XMLSchema}long"/&gt;
 *         &lt;element name="genreName" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="genreDescription" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GenreType", propOrder = {
    "genreId",
    "genreName",
    "genreDescription"
})
public class GenreType {

    protected long genreId;
    @XmlElement(required = true)
    protected String genreName;
    @XmlElement(required = true)
    protected String genreDescription;

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
     * Gets the value of the genreName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGenreName() {
        return genreName;
    }

    /**
     * Sets the value of the genreName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGenreName(String value) {
        this.genreName = value;
    }

    /**
     * Gets the value of the genreDescription property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGenreDescription() {
        return genreDescription;
    }

    /**
     * Sets the value of the genreDescription property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGenreDescription(String value) {
        this.genreDescription = value;
    }

}
