package jp.whitenoise.common.util;

import java.util.stream.Stream;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasValidation;
import com.vaadin.flow.component.HasValue;

/**
 * UI部品ユーティリティ.
 */
public class UiUtil {

    /**
     * 入力有無検証.<BR>
     * 指定したUI部品がすべて入力済みか検証する.
     * 
     * @param targets 検証対象(複数)
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
     * 入力内容検証.<BR>
     * 指定したUI部品がすべて検証正常かチェックする.
     * 
     * @param targets 検証対象(複数)
     * @return true:すべて正常、false:異常あり
     */
    public static boolean isValid(HasValidation... targets) {
        return isValid(Stream.of(targets));
    }

    /**
     * 入力内容検証.<BR>
     * 指定したUI部品がすべて検証正常かチェックする.
     * 
     * @param targets 検証対象(複数)
     * @return true:すべて正常、false:異常あり
     */
    public static boolean isValid(Stream<HasValidation> targets) {
        return targets.allMatch(c -> !c.isInvalid());
    }

    /**
     * 入力内容検証(親コンポーネント指定).<BR>
     * 指定した親コンポーネント配下のUI部品が、すべて検証正常かチェックする.
     * 
     * @param parent 検証対象(親コンポーネント)
     * @return true:すべて正常、false:異常あり
     */
    public static boolean childIsValid(Component parent) {
        return isValid(parent.getChildren().filter(c -> c instanceof HasValidation)
                .map(c -> (HasValidation) c));
    }

    /** 
     * コンストラクタ.
     */
    private UiUtil() {
    }
}
