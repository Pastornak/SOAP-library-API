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
 *         &lt;element name="books" type="{libraryService}Books"/&gt;
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
    "books"
})
@XmlRootElement(name = "getBooksByGenreResponse")
public class GetBooksByGenreResponse {

    @XmlElement(required = true)
    protected Books books;

    /**
     * Gets the value of the books property.
     * 
     * @return
     *     possible object is
     *     {@link Books }
     *     
     */
    public Books getBooks() {
        return books;
    }

    /**
     * Sets the value of the books property.
     * 
     * @param value
     *     allowed object is
     *     {@link Books }
     *     
     */
    public void setBooks(Books value) {
        this.books = value;
    }

}
