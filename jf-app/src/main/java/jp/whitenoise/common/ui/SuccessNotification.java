package jp.whitenoise.common.ui;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.theme.lumo.LumoIcon;

/**
 * 成功通知バルーン.
 */
public class SuccessNotification extends Notification {

    /**
     * コンストラクタ.
     * 
     * @param successMsg 通知メッセージ
     */
    public SuccessNotification(String successMsg) {
        addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        setPosition(Position.TOP_CENTER);
        setDuration(15000); // 最大15秒表示

        Div text = new Div();
        for (String line : successMsg.split("\n")) {
            text.add(new Div(line));
        }
        Button btnClose = new Button(LumoIcon.CROSS.create());
        btnClose.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
        btnClose.getElement().setAttribute("aria-label", "閉じる");
        btnClose.addClickListener(e -> close());

        HorizontalLayout layout = new HorizontalLayout(text, btnClose);
        layout.setAlignItems(Alignment.BASELINE);

        add(layout);
    }
}
