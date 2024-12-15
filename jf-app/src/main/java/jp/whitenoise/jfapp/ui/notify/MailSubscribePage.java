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

@Route(value = "mail", layout = MainLayout.class)
@PageTitle("メール配信 - " + JfApplication.APP_NAME)
@AnonymousAllowed
public class MailSubscribePage extends VerticalLayout {

    /** メール配信サービス. */
    private final MailService serivce;

    /**
     * コンストラクタ.
     */
    public MailSubscribePage(MailService serivce) {
        this.serivce = serivce;
        initSbscribe();
        initUnSbscribe();
    }

    /**
     * 配信登録セクション初期化.
     */
    private void initSbscribe() {
        add(new H4("メール配信仮登録"));
        add(new Div("登録したメールアドレス宛に、翌日出荷予定をメールで配信します。"));
        add(new Div("アドレス仮登録後、5分以内にメールアドレス内のリンクから本登録を行ってください。"));
        add(new Div("なお認証メールが届かない場合は、アドレスが既に仮登録／本登録済みです。"));

        EmailField txtアドレス = new EmailField("メールアドレス");
        txtアドレス.setRequired(true);
        txtアドレス.setValueChangeMode(ValueChangeMode.EAGER);
        Button btn登録 = new Button("仮登録");
        btn登録.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        btn登録.setEnabled(false);

        // メールアドレス有効時のみ登録可
        txtアドレス.addValueChangeListener(event -> btn登録.setEnabled(!txtアドレス.isInvalid()));
        btn登録.addClickListener(event -> {
            if (!txtアドレス.isInvalid()) {
                // 既存アドレス有無等に関わらず、常に登録成功で返す（チェック結果を返すとメアド有無を類推される）
                serivce.regist(txtアドレス.getValue());
                new SuccessNotification("メールアドレスを登録しました。\n仮登録メールを受信した場合は、5分以内に本登録してください。").open();
                txtアドレス.clear();
            }
        });
        HorizontalLayout hl = new HorizontalLayout();
        hl.setPadding(false);
        hl.setAlignItems(Alignment.BASELINE);
        hl.add(txtアドレス, btn登録);
        add(hl);
    }

    /**
     * 配信解除セクション初期化.
     */
    private void initUnSbscribe() {
        add(new H4("メール配信解除"));
        add(new Div("配信を解除するアドレスを入力してください。"));

        EmailField txt解除アドレス = new EmailField("メールアドレス");
        txt解除アドレス.setRequired(true);
        txt解除アドレス.setValueChangeMode(ValueChangeMode.EAGER);
        Button btn解除 = new Button("配信解除");
        btn解除.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        btn解除.setEnabled(false);

        // メールアドレス有効時のみ登録可
        txt解除アドレス.addValueChangeListener(event -> btn解除.setEnabled(!txt解除アドレス.isInvalid()));
        btn解除.addClickListener(event -> {
            if (!txt解除アドレス.isInvalid()) {
                // 既存アドレス有無等に関わらず、常に解除成功で返す（チェック結果を返すとメアド有無を類推される）
                serivce.delete(txt解除アドレス.getValue());
                new SuccessNotification("メール配信を解除しました。").open();
                txt解除アドレス.clear();
            }
        });
        HorizontalLayout hl = new HorizontalLayout();
        hl.setPadding(false);
        hl.setAlignItems(Alignment.BASELINE);
        hl.add(txt解除アドレス, btn解除);
        add(hl);
    }
}
