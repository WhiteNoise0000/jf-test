package jp.whitenoise.common.auth;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;

import com.azure.spring.data.cosmos.core.mapping.Container;
import com.azure.spring.data.cosmos.core.mapping.CosmosUniqueKey;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/**
 * ユーザ情報.
 */
@Data
@NoArgsConstructor
@Container(containerName = "users")
@CosmosUniqueKey(paths = "/username")
public class User {

    /** ユーザ名. */
    @Id
    @NonNull
    private String username;
    /** パスワード. */
    @NonNull
    private String password;
    /** メールアドレス. */
    private Optional<String> emailAddr;
    /** 権限. */
    private final List<String> roles = new ArrayList<String>();
    /** 有効フラグ. */
    private boolean enabled = true;
    /** ロックなしフラグ. */
    private boolean accountNonLocked = true;
    /** パスワード誤りカウント. */
    private int failureCount = 0;

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

    /**
     * コンストラクタ.
     * 
     * @param username ユーザ名
     * @param password パスワード
     * @param roles ユーザ権限
     */
    User(String username, String password, EUserRole... roles) {
        this.username = username;
        this.password = password;
        this.roles.addAll(Arrays.stream(roles).map(EUserRole::name).toList());
    }
}
