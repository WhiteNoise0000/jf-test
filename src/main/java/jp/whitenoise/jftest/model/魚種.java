package jp.whitenoise.jftest.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

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
