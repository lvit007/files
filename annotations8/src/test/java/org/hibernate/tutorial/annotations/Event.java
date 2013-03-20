
package org.hibernate.tutorial.annotations;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Proxy;

@Entity
@Table( name = "EVENTS" )
@ToString(exclude = "person1")  @EqualsAndHashCode //@Proxy(lazy = true) //@Proxy(lazy = false)
public class Event implements Serializable{
    static final long serialVersionUID = 446768348102072453L;
    @Id
	@GeneratedValue(generator="increment")
	@GenericGenerator(name="increment", strategy = "increment")
    private Long id;
    private String title;
    @Temporal(TemporalType.TIMESTAMP)
	@Column(name = "EVENT_DATE")
    private Date date;
    @Getter @Setter
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "personId",referencedColumnName = "personId",nullable = false)
    private Person person1;

	public Event() {
	}

	public Event(String title, Date date) {
		this.title = title;
		this.date = date;
	}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}