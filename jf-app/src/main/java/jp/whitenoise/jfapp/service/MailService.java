package jp.whitenoise.jfapp.service;

import java.nio.charset.Charset;
import java.util.Optional;
import java.util.concurrent.ExecutorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriUtils;

import com.azure.communication.email.EmailAsyncClient;
import com.azure.communication.email.EmailClientBuilder;
import com.azure.communication.email.models.EmailAddress;
import com.azure.communication.email.models.EmailMessage;
import com.azure.communication.email.models.EmailSendResult;
import com.azure.communication.email.models.EmailSendStatus;
import com.azure.core.models.ResponseError;
import com.azure.core.util.polling.PollerFlux;
import com.vaadin.flow.router.RouteConfiguration;

import jp.whitenoise.jfapp.dao.メール配信Dao;
import jp.whitenoise.jfapp.model.メール配信;
import jp.whitenoise.jfapp.ui.notify.MailVerifyPage;

/**
 * メール配信サービス.
 */
@Component
public class MailService {

    /** ロガー. */
    private final Logger logger = LoggerFactory.getLogger(MailService.class);
    /** Azure Email Communication Serviceクライアント. */
    private final EmailAsyncClient mailClient;
    /** メール配信Dao. */
    private final メール配信Dao dao;
    /** メール非同期送信用サービス. */
    private final ExecutorService sendService;
    /** パスワードエンコーダ. */
    private final PasswordEncoder encoder;
    /** メール送信元アドレス. */
    private final String fromAddress;

    /**
     * コンストラクタ.
     * 
     * @param dao メール配信Dao
     * @param sendSerice メール非同期送信用サービス
     * @param conStr メールサービス接続文字列
     * @param fromAddress メール送信元     * @param encoder パスワードエンコーダ
    アドレス
     */
    public MailService(メール配信Dao dao, ExecutorService sendService, PasswordEncoder encoder,
            @Value("${mail.conStr}") String conStr,
            @Value("${mail.fromAddress}") String fromAddress) {
        this.dao = dao;
        this.sendService = sendService;
        this.encoder = encoder;
        this.mailClient = new EmailClientBuilder().connectionString(conStr).buildAsyncClient();
        this.fromAddress = fromAddress;
    }

    /**
     * メールアドレス登録.
     * 
     * @param address アドレス
     */
    public void regist(String address) {

        // 既に登録済みの場合、何もしない
        if (dao.findByアドレス(address) != null) {
            return;
        }

        // メールアドレス仮登録
        メール配信 entity = dao.save(new メール配信(address));
        // ID隠ぺいのためBCrypt暗号化
        entity.setEncryptedId(encoder.encode(entity.getId()));
        dao.save(entity);
        // 未登録の場合、検証用メールを送信
        sendVerifyMail(address, entity.getEncryptedId());
    }

    /**
     * メールアドレス検証.
     * 
     * @param address アドレス
     */
    public boolean verify(String id) {

        // 登録されていない場合、有効期限切れ
        Optional<メール配信> ret = dao.findByEncryptedId(id);
        if (ret.isEmpty()) {
            return false;
        }

        // メールアドレスを本登録に変更
        メール配信 entity = ret.get();
        entity.setEncryptedId(null); // 隠ぺい用キーは無効化
        entity.set検証済み(true);
        entity.setTtl(-1);
        dao.save(entity);
        return true;
    }

    /**
     * 検証メール送信.
     * 
     * @param address アドレス
     * @param id 主キー
     */
    private void sendVerifyMail(String address, String id) {

        EmailMessage send = new EmailMessage();

        // 送信タイトル
        String subject = "メールアドレス仮登録（出荷予定配信）";
        send.setSubject(subject);
        // 送信元アドレス
        send.setSenderAddress(fromAddress);
        // TOで送信
        send.setToRecipients(new EmailAddress(address));
        // 本文
        send.setBodyPlainText("""
                ※このメールは、【出荷予定配信】に仮登録いただいたメールアドレス宛に自動的に送信しています。

                この度は、出荷予定配信にご登録頂きありがとうございます。
                5分以内に、下記リンクよりメールアドレスの本登録を完了してください。

                本登録URL：%s/%s

                【ご注意】
                アドレス登録の覚えがない場合は、リンクは押下せず本メールは破棄してください。
                5分経過後は仮登録を自動解除するため、有効期限切れの場合は改めて仮登録してください。
                """.formatted(ServletUriComponentsBuilder.fromCurrentContextPath().toUriString(),
                RouteConfiguration.forSessionScope().getUrl(MailVerifyPage.class,
                        UriUtils.encode(id, Charset.defaultCharset()))));

        // メール非同期送信開始
        PollerFlux<EmailSendResult, EmailSendResult> poller = mailClient.beginSend(send);
        sendService.execute(() -> {
            poller.subscribe(
                    // 送信完了
                    res -> {
                        EmailSendResult result = res.getValue();
                        // 送信結果出力
                        logger.info("status:" + result.getStatus());
                        if (result.getStatus() != EmailSendStatus.SUCCEEDED) {
                            ResponseError e = result.getError();
                            logger.error(e.getCode() + ":" + e.getMessage());
                        }
                    },
                    // 内部エラー
                    error -> {
                        logger.error(error.getMessage(), error.getCause());
                    });
        });
    }

    /**
     * メールアドレス削除.
     * 
     * @param address アドレス
     */
    public void delete(String address) {
        dao.deleteByアドレス(address);
    }
}
