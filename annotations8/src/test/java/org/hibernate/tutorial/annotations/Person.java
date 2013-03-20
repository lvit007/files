package org.hibernate.tutorial.annotations;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.annotations.Proxy;

import javax.persistence.*;
import javax.persistence.criteria.Fetch;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "Persons")
@ToString
@EqualsAndHashCode
//@Proxy(lazy = true)//@Proxy(lazy = false)
public class Person implements Serializable {
    static final long serialVersionUID = -7593775012501239455L;
    @Id
    @GeneratedValue()
    @Column(name = "personId")
    private Long id;
    @Column(name = "fName")
    private String firstName;
    @Column(name = "sName")
    private String sureName;
   /* @OneToMany(cascade = CascadeType.ALL
            , fetch = FetchType.EAGER,
            mappedBy = "person1")
    @Getter
    @Setter
    private List<Event> events = new ArrayList<Event>();
    */
    public String getSureName() {
        return sureName;
    }

    public void setSureName(String sureName) {
        this.sureName = sureName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
