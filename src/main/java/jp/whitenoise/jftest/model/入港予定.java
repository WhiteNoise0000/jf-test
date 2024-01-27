package jp.whitenoise.jftest.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotNull;

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
