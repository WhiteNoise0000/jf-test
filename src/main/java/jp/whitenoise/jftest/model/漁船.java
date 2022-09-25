package jp.whitenoise.jftest.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class 漁船 {
    
    @Id
    @Column(length = 6)
    private String 漁船ID;
    @Column(nullable = false, length = 20, unique = true)
    private String 漁船名;
}
