/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package alexandre.letteridentification.model;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 *
 * @author alexa
 */

@Entity
public class Statistics implements Serializable
{
    @Id
    String id;
    
    Character letter;
    
    private Integer number_first;
    private Integer number_second;
    private Integer number_third;
    private Integer number_more;

    public Statistics() {
    }

    public Statistics(String id, Character letter, Integer number_first, Integer number_second, Integer number_third, Integer number_more) {
        this.id = id;
        this.letter = letter;
        this.number_first = number_first;
        this.number_second = number_second;
        this.number_third = number_third;
        this.number_more = number_more;
    }

    public Statistics(String id, Character letter) {
        this.id = id;
        this.letter = letter;
    }

    public String getId() {
        return id;
    }

    public Character getLetter() {
        return letter;
    }

    public Integer getNumber_first() {
        return number_first;
    }

    public Integer getNumber_second() {
        return number_second;
    }

    public Integer getNumber_third() {
        return number_third;
    }

    public Integer getNumber_more() {
        return number_more;
    }

    public void setNumber_first(Integer number_first) {
        this.number_first = number_first;
    }

    public void setNumber_second(Integer number_second) {
        this.number_second = number_second;
    }

    public void setNumber_third(Integer number_third) {
        this.number_third = number_third;
    }

    public void setNumber_more(Integer number_more) {
        this.number_more = number_more;
    }

    @Override
    public int hashCode() {
        Integer hash = 7;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Statistics other = (Statistics) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Statistics{" + "id=" + id + ", letter=" + letter + ", number_first=" + number_first + ", number_second=" + number_second + ", number_third=" + number_third + ", number_more=" + number_more + '}';
    }
}
