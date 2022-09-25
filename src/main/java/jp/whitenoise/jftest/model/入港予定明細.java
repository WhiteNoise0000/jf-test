package jp.whitenoise.jftest.model;

import java.time.LocalDate;

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
public class 入港予定明細 {
    
    @Id
    @GeneratedValue
    private Integer 明細ID;
    private Integer 予定ID;
    @Column(length = 20, nullable = false)
    private String 魚種;
    private short 数量;
    private LocalDate 出荷予定日;
}
