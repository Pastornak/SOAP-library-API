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
 *         &lt;element name="search" type="{libraryService}SearchParamsWithPagination"/&gt;
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
    "search"
})
@XmlRootElement(name = "getBooksByGenreRequest")
public class GetBooksByGenreRequest {

    protected long genreId;
    @XmlElement(required = true)
    protected SearchParamsWithPagination search;

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
     * Gets the value of the search property.
     * 
     * @return
     *     possible object is
     *     {@link SearchParamsWithPagination }
     *     
     */
    public SearchParamsWithPagination getSearch() {
        return search;
    }

    /**
     * Sets the value of the search property.
     * 
     * @param value
     *     allowed object is
     *     {@link SearchParamsWithPagination }
     *     
     */
    public void setSearch(SearchParamsWithPagination value) {
        this.search = value;
    }

}
