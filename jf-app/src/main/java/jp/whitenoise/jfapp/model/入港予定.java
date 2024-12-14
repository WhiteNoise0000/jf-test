package jp.whitenoise.jfapp.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;

import com.azure.spring.data.cosmos.core.mapping.CompositeIndex;
import com.azure.spring.data.cosmos.core.mapping.CompositeIndexPath;
import com.azure.spring.data.cosmos.core.mapping.Container;
import com.azure.spring.data.cosmos.core.mapping.CosmosIndexingPolicy;
import com.azure.spring.data.cosmos.core.mapping.GeneratedValue;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@NoArgsConstructor
@Container
@CosmosIndexingPolicy(compositeIndexes = @CompositeIndex(paths = {
        // 複合インデックスは手動定義必要
        @CompositeIndexPath(path = "/入港予定日"),
        @CompositeIndexPath(path = "/入港漁船/漁船名") }))
public class 入港予定 {

    @Id
    @GeneratedValue
    private String id;
    @NonNull
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate 入港予定日 = LocalDate.now();
    @NonNull
    private 漁船 入港漁船;
    private List<入港予定明細> 明細 = new ArrayList<>();

    @Version
    private String _etag;
    @CreatedDate
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    private LocalDateTime createDate;
    @CreatedBy
    private String createdBy;
    @LastModifiedDate
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    private LocalDateTime lastModifyedDate;
    @LastModifiedBy
    private String lastModifiedBy;
}
