package jp.whitenoise.jftest.ui;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.OptionalParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;

import jp.whitenoise.common.ui.ErrorNotification;
import jp.whitenoise.jftest.model.入港予定;
import jp.whitenoise.jftest.model.入港予定明細;
import jp.whitenoise.jftest.model.漁船;
import jp.whitenoise.jftest.service.AddService;

@Route("add")
@PageTitle("漁獲予定登録・編集")
public class AddPage extends VerticalLayout implements HasUrlParameter<Integer> {

    private final AddService service;
    @NotNull
    private Optional<入港予定> entity = Optional.empty();

    private final DatePicker dt入港予定日;
    private final ComboBox<漁船> cmb入港漁船;
    private final VerticalLayout vl明細;
    private final List<Item> list入港予定明細 = new ArrayList<>();

    private final List<String> list魚種;

    public AddPage(@Autowired AddService service) {
        this.service = service;
        setAlignItems(Alignment.CENTER);

        VerticalLayout vlMain = new VerticalLayout();
        vlMain.setMargin(false);
        vlMain.setPadding(false);
        vlMain.setMaxWidth(1024, Unit.PIXELS);
        add(vlMain);
        vlMain.add(new H2("漁獲予定登録・編集"));

        // 入港予定日
        dt入港予定日 = new DatePicker("入港予定日", LocalDate.now());
        dt入港予定日.setMin(LocalDate.now());
        dt入港予定日.setRequired(true);
        vlMain.add(dt入港予定日);

        // 対象漁船
        cmb入港漁船 = new ComboBox<>("入港漁船");
        cmb入港漁船.setRequired(true);
        cmb入港漁船.setItemLabelGenerator(漁船::get漁船名);
        cmb入港漁船.setItems(service.select漁船());
        vlMain.add(cmb入港漁船);

        // 明細
        vl明細 = new VerticalLayout();
        vl明細.setMargin(false);
        vl明細.setPadding(false);
        vlMain.add(vl明細);
        list魚種 = service.select魚種();
        addItem(new Item().setLabelVisible(true));
        addItem(new Item());
        addItem(new Item());

        // 1行追加・保存ボタン
        Button btn追加 = new Button("1行追加", e -> addItem(new Item()));
        Button btn保存 = new Button("保存", e -> save());
        btn保存.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        vlMain.add(new HorizontalLayout(btn追加, btn保存));

        // 戻るリンク
        vlMain.add(new RouterLink("戻る", TopPage.class));
    }

    /**
     * パラメータ初期化処理.
     */
    @Override
    public void setParameter(BeforeEvent event, @OptionalParameter Integer id) {
        // 新規登録時は何もしない
        if (id == null) {
            return;
        }
        // 編集の場合、入港予定取得
        entity = service.select入港予定(id);
        if (entity.isPresent()) {
            // 取得成功時、画面に表示
            入港予定 entity = this.entity.get();
            cmb入港漁船.setValue(entity.get入港漁船());
            dt入港予定日.setValue(entity.get入港予定日());
            list入港予定明細.clear();
            vl明細.removeAll();
            entity.get明細().forEach(item -> addItem(new Item(item)));
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
        if (list入港予定明細.size() == 1) {
            item.setLabelVisible(true);
        }
    }

    private void removeItem(Item item) {
        // 1行目削除の場合、ラベル表示再設定
        if (item.equals(list入港予定明細.get(0)) && list入港予定明細.size() >= 2) {
            list入港予定明細.get(1).setLabelVisible(true);
        }
        list入港予定明細.remove(item);
        vl明細.remove(item);
    }

    private void save() {

        // TODO 入力チェック（バインダー使ってない。。）

        入港予定 entity = this.entity.orElse(new 入港予定());
        entity.set入港予定日(dt入港予定日.getValue());
        entity.set入港漁船(cmb入港漁船.getValue());
        entity.get明細().clear();
        // 全項目入力の行だけ保存
        list入港予定明細.forEach(item -> {
            if (item.hasValue()) {
                entity.get明細().add(item.get明細());
            }
        });
        service.save入港予定(entity);

        // 保存成功
        getUI().ifPresent((ui) -> ui.navigate(TopPage.class));
        Notification success = new Notification("保存しました.", 5000, Position.TOP_CENTER);
        success.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        success.open();
    }

    /**
     * 明細アイテム.
     */
    private class Item extends HorizontalLayout {

        private final ComboBox<String> cmb魚種;
        private final IntegerField int数量;
        private final DatePicker dt出荷予定日;
        private final Button btn削除;

        Item() {
            setAlignItems(Alignment.END);
            cmb魚種 = new ComboBox<>();
            cmb魚種.setItems(list魚種);
            cmb魚種.setRequired(true);
            add(cmb魚種);
            int数量 = new IntegerField();
            int数量.setMin(1);
            int数量.setMax(99);
            int数量.setStepButtonsVisible(true);
            int数量.setRequiredIndicatorVisible(true);
            add(int数量);
            dt出荷予定日 = new DatePicker();
            dt出荷予定日.setMin(LocalDate.now());
            dt出荷予定日.setRequired(true);
            add(dt出荷予定日);
            Icon closeIcon = VaadinIcon.CLOSE_BIG.create();
            closeIcon.setColor("red");
            btn削除 = new Button(closeIcon, e -> removeItem(this));
            add(btn削除);
        }

        Item(入港予定明細 entity) {
            this();
            cmb魚種.setValue(entity.get魚種());
            int数量.setValue(Integer.valueOf(entity.get数量()));
            dt出荷予定日.setValue(entity.get出荷予定日());
        }

        boolean hasError() {
            if(hasValue()) {
                return cmb魚種.isEmpty() || int数量.isEmpty() || dt出荷予定日.isEmpty();
            }
            return false;
        }

        boolean hasValue() {
            if(cmb魚種.isEmpty() || int数量.isEmpty() || dt出荷予定日.isEmpty()) {
                return false;
            }
            return true;
        }
        入港予定明細 get明細() {
            入港予定明細 ret = new 入港予定明細();
            ret.set魚種(cmb魚種.getValue());
            ret.set数量(int数量.getValue().shortValue());
            ret.set出荷予定日(dt出荷予定日.getValue());
            return ret;
        }

        Item setLabelVisible(boolean visible) {
            cmb魚種.setLabel("魚種");
            int数量.setLabel("数量");
            dt出荷予定日.setLabel("出荷予定日");
            return this;
        }
    }
}
