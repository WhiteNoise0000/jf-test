package jp.whitenoise.jfnotify;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * 入港予定明細コンテナの集計結果.
 */
@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class NotifyItem {

    @NonNull
    private String 魚種;
    @JsonDeserialize(using = DoubleToInteger.class)
    private Integer 数量;
}
