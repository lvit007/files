/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * Copyright (c) 2010, Red Hat Inc. or third-party contributors as
 * indicated by the @author tags or express copyright attribution
 * statements applied by the authors.  All third-party contributions are
 * distributed under license by Red Hat Inc.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 */
package org.hibernate.tutorial.annotations;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.*;
import org.hibernate.annotations.Cache;

@Entity
@Table( name = "EVENTS" )
@ToString(exclude = "person1") @EqualsAndHashCode
//@Cacheable
//@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Event implements Serializable {
    static final long serialVersionUID = 446768348102072453L;
    @Id
	@GeneratedValue(generator="increment")
	@GenericGenerator(name="increment", strategy = "increment")
    private Long id;
    private String title;
    private Date date;
    @Getter @Setter
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "personId",referencedColumnName = "personId",nullable = false)
    private Person person1;

	public Event() {
		// this form used by Hibernate
	}

	public Event(String title, Date date) {
		// for application use, to create new events
		this.title = title;
		this.date = date;
	}


    public Long getId() {
		return id;
    }

    private void setId(Long id) {
		this.id = id;
    }

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "EVENT_DATE")
    public Date getDate() {
		return date;
    }

    public void setDate(Date date) {
		this.date = date;
    }

    public String getTitle() {
		return title;
    }

    public void setTitle(String title) {
		this.title = title;
    }

}