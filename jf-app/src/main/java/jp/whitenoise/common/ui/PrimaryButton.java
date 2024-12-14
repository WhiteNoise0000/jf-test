package jp.whitenoise.common.ui;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.icon.Icon;

/**
 * プライマリボタン.
 */
public class PrimaryButton extends Button {

    public PrimaryButton() {
        addThemeVariants(ButtonVariant.LUMO_PRIMARY);
    }

    public PrimaryButton(String text) {
        super(text);
        addThemeVariants(ButtonVariant.LUMO_PRIMARY);
    }

    public PrimaryButton(String text, ComponentEventListener<ClickEvent<Button>> clickListener) {
        super(text, clickListener);
        addThemeVariants(ButtonVariant.LUMO_PRIMARY);
    }

    public PrimaryButton(Icon icon, ComponentEventListener<ClickEvent<Button>> clickListener) {
        super(icon, clickListener);
        addThemeVariants(ButtonVariant.LUMO_PRIMARY);
    }

    public PrimaryButton(String text, Icon icon, ComponentEventListener<ClickEvent<Button>> clickListener) {
        super(text, icon, clickListener);
        addThemeVariants(ButtonVariant.LUMO_PRIMARY);
    }
}
