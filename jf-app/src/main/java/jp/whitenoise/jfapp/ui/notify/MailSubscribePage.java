package jp.whitenoise.jfapp.ui.notify;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

import jp.whitenoise.JfApplication;
import jp.whitenoise.common.ui.SuccessNotification;
import jp.whitenoise.jfapp.service.MailService;
import jp.whitenoise.jfapp.ui.MainLayout;

@Route(value = "mail/subscribe", layout = MainLayout.class)
@PageTitle("メール登録 - " + JfApplication.APP_NAME)
@AnonymousAllowed
public class MailSubscribePage extends VerticalLayout {

    /**
     * コンストラクタ.
     */
    public MailSubscribePage(MailService serivce) {

        add(new H4("メール配信登録"));
        add(new Div("登録したメールアドレス宛に、翌日出荷予定をメールで配信します。"));
        add(new Div("アドレス登録後、5分以内にメールアドレス内のリンクから認証を行ってください。"));

        EmailField txtアドレス = new EmailField("メールアドレス");
        txtアドレス.setRequired(true);
        txtアドレス.setValueChangeMode(ValueChangeMode.EAGER);
        Button btn登録 = new Button("登録");
        btn登録.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        btn登録.setEnabled(false);

        // メールアドレス有効時のみ登録可
        txtアドレス.addValueChangeListener(event -> btn登録.setEnabled(!txtアドレス.isInvalid()));
        btn登録.addClickListener(event -> {
            if (!txtアドレス.isInvalid()) {
                // 既存アドレス有無等に関わらず、常に登録成功で返す（チェック結果を返すとメアド有無を類推される）
                serivce.regist(txtアドレス.getValue());
                new SuccessNotification("メールアドレスを登録しました。").open();
                txtアドレス.clear();
            }
        });
        HorizontalLayout hl = new HorizontalLayout();
        hl.setPadding(false);
        hl.setAlignItems(Alignment.BASELINE);
        hl.add(txtアドレス, btn登録);
        add(hl);
    }
}
