package jp.whitenoise.jftest.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

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
