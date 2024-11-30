package jp.whitenoise.common.ui;

import com.vaadin.flow.component.Text;
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

    public SuccessNotification(String errMsg) {
        addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        setPosition(Position.TOP_CENTER);
        setDuration(15000); // 最大15秒表示

        Div text = new Div(new Text(errMsg));
        Button btnClose = new Button(LumoIcon.CROSS.create());
        btnClose.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
        btnClose.getElement().setAttribute("aria-label", "閉じる");
        btnClose.addClickListener(e -> close());

        HorizontalLayout layout = new HorizontalLayout(text, btnClose);
        layout.setAlignItems(Alignment.CENTER);

        add(layout);
    }
}
