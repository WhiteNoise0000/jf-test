package jp.whitenoise.jftest.ui;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import jakarta.annotation.security.PermitAll;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.OptionalParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;

import jp.whitenoise.JfTestApplication;
import jp.whitenoise.common.ui.DatePickerI18nJpn;
import jp.whitenoise.common.ui.ErrorNotification;
import jp.whitenoise.common.util.UiUtil;
import jp.whitenoise.jftest.model.入港予定;
import jp.whitenoise.jftest.model.入港予定明細;
import jp.whitenoise.jftest.model.漁船;
import jp.whitenoise.jftest.service.EditService;

@Route(value = "edit", layout = MainLayout.class)
@PageTitle("出荷予定登録・編集 - " + JfTestApplication.APP_NAME)
@PermitAll
public class EditPage extends VerticalLayout implements HasUrlParameter<String> {

    private final EditService service;
    private final Binder<入港予定> binder = new Binder<入港予定>();

    private final DatePicker dt入港予定日;
    private final ComboBox<漁船> cmb入港漁船;
    private final VerticalLayout vl明細;
    private final List<Item> list入港予定明細 = new ArrayList<>();

    private final Set<String> list魚種;

    public EditPage(@Autowired EditService service) {
        this.service = service;
        add(new H4("出荷予定登録・編集"));
        setSizeFull();
        binder.setBean(new 入港予定());

        // 入港予定日
        dt入港予定日 = new DatePicker("入港予定日");
        dt入港予定日.setI18n(new DatePickerI18nJpn());
        dt入港予定日.setMin(LocalDate.now());
        dt入港予定日.setMax(LocalDate.now().plusDays(30));
        dt入港予定日.setRequired(true);
        binder.bind(dt入港予定日, 入港予定::get入港予定日, 入港予定::set入港予定日);
        add(dt入港予定日);

        // 入港漁船
        cmb入港漁船 = new ComboBox<>("入港漁船");
        cmb入港漁船.setRequired(true);
        cmb入港漁船.setItemLabelGenerator(漁船::get漁船名);
        cmb入港漁船.setItems(service.select漁船());
        cmb入港漁船.setErrorMessage("入港漁船が不正です。");
        binder.bind(cmb入港漁船, 入港予定::get入港漁船, 入港予定::set入港漁船);
        add(cmb入港漁船);

        // 明細(デフォルト3行)
        vl明細 = new VerticalLayout();
        vl明細.setMargin(false);
        vl明細.setPadding(false);
        add(vl明細);

        list魚種 = service.select魚種();
        addItem(new Item().setLabelVisible());
        addItem(new Item());
        addItem(new Item());

        // 1行追加・保存ボタン
        Button btn追加 = new Button("1行追加", e -> addItem(new Item()));
        Button btn保存 = new Button("保存", e -> save());
        btn保存.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        add(new HorizontalLayout(btn追加, btn保存));

        // 戻るリンク
        add(new RouterLink("戻る", TopPage.class));
    }

    /**
     * パラメータ初期化処理.
     */
    @Override
    public void setParameter(BeforeEvent event, @OptionalParameter String id) {
        // 新規登録時は何もしない
        if (id == null) {
            return;
        }
        // 編集の場合、入港予定取得
        Optional<入港予定> entity = service.select入港予定(id);
        if (entity.isPresent()) {
            // 取得成功時、画面に表示
            binder.setBean(entity.get());
            list入港予定明細.clear();
            vl明細.removeAll();
            entity.get().get明細().forEach(item -> addItem(new Item(item)));
        }
        // 取得失敗（不正なパラメータ）
        else {
            // エラーメッセージ表示
            new ErrorNotification("エラー：入港予定が見つかりません").open();
        }
    }

    private void addItem(Item item) {
        list入港予定明細.add(item);
        vl明細.add(item);
        // 1行目削除の場合、ラベル表示再設定
        if (list入港予定明細.size() == 1) {
            item.setLabelVisible();
        }
    }

    private void removeItem(Item item) {
        // 1行目削除の場合、ラベル表示再設定
        if (item.equals(list入港予定明細.get(0)) && list入港予定明細.size() >= 2) {
            list入港予定明細.get(1).setLabelVisible();
        }
        list入港予定明細.remove(item);
        vl明細.remove(item);
    }

    private void save() {

        // 入力チェックエラー
        if (binder.validate().hasErrors() || list入港予定明細.stream().anyMatch(item -> item.hasErrors())) {
            Notification warn = new Notification("入力エラー.", 5000, Position.BOTTOM_CENTER);
            warn.addThemeVariants(NotificationVariant.LUMO_WARNING);
            warn.open();
            return;
        }

        // 全項目入力の行だけ保存
        入港予定 entity = binder.getBean();
        entity.get明細().clear();
        list入港予定明細.forEach(t -> {
            if (t.hasValue()) {
                entity.get明細().add(t.get明細());
            }
        });

        try {
            service.save入港予定(entity);

            // 保存成功
            getUI().ifPresent((ui) -> ui.refreshCurrentRoute(true));
            Notification success = new Notification("保存しました.", 5000, Position.BOTTOM_CENTER);
            success.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            success.open();
        } catch (OptimisticLockingFailureException e) {
            // 排他エラー
            new ErrorNotification("保存失敗：ほかのユーザに更新されました").open();
        }
    }

    /**
     * 明細アイテム.
     */
    private class Item extends HorizontalLayout {

        private final Binder<入港予定明細> binder = new Binder<>();
        private final ComboBox<String> cmb魚種;
        private final IntegerField int数量;
        private final DatePicker dt出荷予定日;
        private final Button btn削除;

        Item() {
            this(new 入港予定明細());
        }

        Item(入港予定明細 entity) {
            setAlignItems(Alignment.BASELINE);

            cmb魚種 = new ComboBox<>();
            cmb魚種.setItems(list魚種);
            cmb魚種.setRequired(true);
            cmb魚種.setWidth("120px");
            cmb魚種.setErrorMessage("魚種が不正です。");
            binder.bind(cmb魚種, 入港予定明細::get魚種, 入港予定明細::set魚種);

            int数量 = new IntegerField();
            int数量.setMin(1);
            int数量.setMax(99);
            int数量.setStepButtonsVisible(true);
            int数量.setRequiredIndicatorVisible(true);
            int数量.setErrorMessage("数量が不正です。");
            int数量.setWidth("100px");
            binder.bind(int数量, 入港予定明細::get数量, 入港予定明細::set数量);

            dt出荷予定日 = new DatePicker();
            dt出荷予定日.setI18n(new DatePickerI18nJpn());
            dt出荷予定日.setMin(LocalDate.now());
            dt出荷予定日.setMax(LocalDate.now().plusDays(30));
            dt出荷予定日.setRequired(true);
            dt出荷予定日.setWidth("150px");
            binder.bind(dt出荷予定日, 入港予定明細::get出荷予定日, 入港予定明細::set出荷予定日);

            Icon delIcon = VaadinIcon.CLOSE_BIG.create();
            delIcon.setColor("red");
            btn削除 = new Button(delIcon, e -> removeItem(this));

            add(cmb魚種, int数量, dt出荷予定日, btn削除);
            binder.setBean(entity);
        }

        boolean hasValue() {
            return UiUtil.hasValues(cmb魚種, int数量, dt出荷予定日);
        }

        boolean hasErrors() {
            return binder.validate().hasErrors();
        }

        入港予定明細 get明細() {
            if (binder.validate().hasErrors()) {
                return null;
            }
            return binder.getBean();
        }

        Item setLabelVisible() {
            cmb魚種.setLabel("魚種");
            int数量.setLabel("数量");
            dt出荷予定日.setLabel("出荷予定日");
            return this;
        }
    }
}
