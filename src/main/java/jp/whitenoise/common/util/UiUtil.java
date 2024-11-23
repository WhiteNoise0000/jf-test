package jp.whitenoise.common.util;

import com.vaadin.flow.component.HasValue;

/**
 * UI部品ユーティリティ.
 */
public class UiUtil {

    /**
     * 入力チェック.<BR>
     * 指定したUI部品がすべて入力済みかチェックする.
     * 
     * @param targets チェック対象(複数)
     * @return true:すべて入力、false:未入力あり
     */
    public static boolean hasValues(HasValue<?, ?>... targets) {
        for (HasValue<?, ?> target : targets) {
            if (target.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    /** 
     * コンストラクタ.
     */
    private UiUtil() {
    }
}
