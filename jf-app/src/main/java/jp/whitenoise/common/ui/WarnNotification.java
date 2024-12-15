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
 * 警告通知バルーン.
 */
public class WarnNotification extends Notification {

    public WarnNotification(String warnMsg) {
        addThemeVariants(NotificationVariant.LUMO_WARNING);
        setPosition(Position.TOP_CENTER);
        setDuration(15000); // 最大15秒表示

        Div text = new Div();
        for (String line : warnMsg.split("\n")) {
            add(new Div(line));
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
