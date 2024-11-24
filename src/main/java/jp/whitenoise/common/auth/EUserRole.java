package jp.whitenoise.common.auth;

import java.util.List;
import java.util.stream.Stream;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import lombok.Getter;

/**
 * ユーザ権限.
 */
@Getter
public enum EUserRole {

    /** 管理者権限. */
    ADMIN("管理者"),
    /** ユーザ権限. */
    USER("ユーザ");

    /** 管理者権限. */
    public static final String ROLE_ADMIN = "ADMIN";
    /** ユーザ権限. */
    public static final String ROLE_USER = "USER";
    /** 権限名. **/
    private String nameJpn;
    /** 権限オブジェクト. */
    private GrantedAuthority authObj;

    /**
     * コンストラクタ.
     * 
     * @param 権限名(和名)
     */
    private EUserRole(String nameJpn) {
        this.nameJpn = nameJpn;
        // ROLE_を先頭に自動付与必要
        this.authObj = new SimpleGrantedAuthority("ROLE_" + name());
    }

    /**
     * 権限リスト変換.
     * 
     * @param roles 対象権限
     * @return 変換結果
     */
    public List<String> valueOf(EUserRole... roles) {
        return Stream.of(roles).map(EUserRole::name).toList();
    }

    /**
     * 権限リスト変換.
     * 
     * @param roles 対象権限
     * @return 変換結果
     */
    public List<EUserRole> valueOf(String... authorities) {
        return Stream.of(authorities).map(EUserRole::valueOf).toList();
    }
}
