package fr.emse.tb3pwme.project.persistence;

import fr.emse.tb3pwme.project.domain.Address;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class AddressEmbeddable {

    @Column(nullable = false)
    private String street;

    @Column(nullable = false)
    private String zipCode;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String country;

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public static AddressEmbeddable fromDomain(Address address) {
        AddressEmbeddable embeddable = new AddressEmbeddable();
        embeddable.setStreet(address.getStreet());
        embeddable.setCity(address.getCity());
        embeddable.setZipCode(address.getZipCode());
        embeddable.setCountry(address.getCountry());
        return embeddable;
    }

    public Address toDomain() {
        return new Address(street, city, zipCode, country);
    }

}
