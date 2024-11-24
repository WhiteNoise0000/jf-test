package jp.whitenoise.common.auth;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.azure.spring.data.cosmos.core.mapping.Container;
import com.azure.spring.data.cosmos.core.mapping.CosmosUniqueKey;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
public class User implements UserDetails {

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
    private final Set<String> roles = new HashSet<>();
    /** 有効フラグ. */
    private boolean enabled = true;
    /** ロックなしフラグ. */
    private boolean accountNonLocked = true;
    /** パスワード誤りカウント. */
    private int failureCount = 0;
    /** ユーザ別設定. */
    private final Map<String, String> userSettings = new HashMap<>();

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

    @JsonIgnore
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream().map(role -> EUserRole.valueOf(role).getAuthObj()).toList();
    }
}
