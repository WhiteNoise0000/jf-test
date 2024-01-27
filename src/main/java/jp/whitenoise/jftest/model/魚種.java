package jp.whitenoise.jftest.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class 魚種 {
    
    @Id
    @GeneratedValue
    private Integer 魚種ID;
    @Column(length = 20, nullable = false)
    private String 名称;
}
