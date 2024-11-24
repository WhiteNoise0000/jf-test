package jp.whitenoise.common.auth;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import lombok.Getter;

/**
 * ユーザ権限.
 */
@Getter
public enum EUserRole {

    /** 管理者権限. */
    ADMIN,
    /** ユーザ権限. */
    USER;

    /** 権限オブジェクト. */
    private GrantedAuthority authObj;

    /**
     * コンストラクタ.
     */
    private EUserRole() {
        // ROLE_を先頭に自動付与必要
        authObj = new SimpleGrantedAuthority("ROLE_" + name());
    }
}
