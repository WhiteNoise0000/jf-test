package jp.whitenoise.common.auth.ui;

import java.util.Optional;

import jakarta.annotation.security.PermitAll;

import org.springframework.dao.OptimisticLockingFailureException;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.OptionalParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import jp.whitenoise.JfApplication;
import jp.whitenoise.common.auth.AuthService;
import jp.whitenoise.common.auth.User;
import jp.whitenoise.common.ui.ErrorNotification;
import jp.whitenoise.common.ui.SuccessNotification;
import jp.whitenoise.common.ui.WarnNotification;
import jp.whitenoise.common.util.UiUtil;
import jp.whitenoise.jfapp.ui.MainLayout;

@Route(value = "users/edit", layout = MainLayout.class)
@PageTitle("ユーザ編集 - " + JfApplication.APP_NAME)
@PermitAll
public class UserEditPage extends VerticalLayout implements HasUrlParameter<String> {

    /** 認証・認可サービス. */
    protected final AuthService service;
    /** ユーザ名. */
    protected final TextField txtユーザ名;
    /** 新パスワード. */
    protected final PasswordField txtパスワード;
    /** 新パスワード(確認). */
    protected final PasswordField txtパスワード確認;
    /** 管理ページ側追加先レイアウト. */
    protected final VerticalLayout vl管理属性レイアウト;
    /** メールアドレス. */
    protected final EmailField txtメールアドレス;
    /** 編集対象ユーザ. */
    protected Optional<User> targetUser = Optional.empty();

    /**
     * コンストラクタ.
     * 
     * @param userDao ユーザ情報DAO
     */
    public UserEditPage(AuthService service) {
        this.service = service;

        add(new H4("ユーザ編集"));
        setSpacing(false);
        setSizeFull();

        // ユーザ名(読み取り専用)
        txtユーザ名 = new TextField("ユーザ名");
        txtユーザ名.setReadOnly(true);
        add(txtユーザ名);

        // 新パスワード
        txtパスワード = new PasswordField("パスワード");
        txtパスワード.setMinLength(8);
        txtパスワード.setErrorMessage("パスワードは8文字以上で入力してください。");
        add(txtパスワード);

        // 新パスワード(確認)
        txtパスワード確認 = new PasswordField("パスワード(確認)");
        txtパスワード確認.setErrorMessage("パスワードが一致しません。");
        txtパスワード確認.setManualValidation(true);
        txtパスワード確認.addValueChangeListener(event -> {
            // パスワード入力の一致をチェック
            boolean isInvalid = !txtパスワード.getValue().equals(txtパスワード確認.getValue());
            txtパスワード確認.setInvalid(isInvalid);
        });
        add(txtパスワード確認);

        // メールアドレス
        txtメールアドレス = new EmailField("メールアドレス(任意)");
        txtメールアドレス.setTooltipText("各種通知をする場合は入力");
        txtメールアドレス.setErrorMessage("メールアドレスが不正です。");
        add(txtメールアドレス);

        // 管理ページでユーザ権限・有効フラグ追加先
        vl管理属性レイアウト = new VerticalLayout();
        vl管理属性レイアウト.setPadding(false);
        add(vl管理属性レイアウト);

        // 戻る・保存ボタン
        Button btn保存 = new Button("保存", e -> save());
        btn保存.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        Button btn戻る = new Button("戻る", click -> UI.getCurrent().getPage().getHistory().back());
        add(new HorizontalLayout(btn戻る, btn保存));
    }

    /**
     * パラメータ初期化処理.<BR>
     * ユーザページでは自分自身の情報を設定.
     */
    @Override
    public void setParameter(BeforeEvent event, @OptionalParameter String username) {

        // 自身のユーザ情報を画面表示
        String loginUser = service.getAuthUsername().get();
        targetUser = service.findByUsername(loginUser);
        targetUser.ifPresentOrElse(user -> {
            // 取得成功
            txtユーザ名.setValue(user.getUsername());
            user.getEmailAddr().ifPresent(v -> txtメールアドレス.setValue(v));
        }, () -> {
            // 取得失敗（別ユーザが削除等）
            UI.getCurrent().getPage().getHistory().back();
            new ErrorNotification("エラー：対象ユーザが見つかりません").open();
        });
    }

    /**
     * 保存ボタン押下.
     */
    protected void save() {

        // 入力チェックエラー
        if (!UiUtil.childIsValid(this)) {
            new WarnNotification("入力内容が不正です。").open();
            return;
        }

        try {
            // ユーザ情報更新
            service.save(targetUser, Optional.ofNullable(txtパスワード.getValue()),
                    Optional.ofNullable(txtメールアドレス.getValue()));

            // 保存成功
            getUI().ifPresent((ui) -> ui.refreshCurrentRoute(true));
            new SuccessNotification("保存しました。").open();

        } catch (OptimisticLockingFailureException e) {
            // 排他エラー
            new ErrorNotification("保存失敗：ほかのユーザに更新されました").open();
        }
    }
}
