package jp.whitenoise.jftest.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class 入港予定 {
    
    @Id
    @GeneratedValue
    private Integer 予定ID;
    @NotNull
    private LocalDate 入港予定日;
    @ManyToOne
    @JoinColumn(name = "漁船ID")
    private 漁船 入港漁船;
    @OneToMany(mappedBy = "予定ID", orphanRemoval = true, fetch = FetchType.EAGER)
    private List<入港予定明細> 明細 = new ArrayList<>();
}
