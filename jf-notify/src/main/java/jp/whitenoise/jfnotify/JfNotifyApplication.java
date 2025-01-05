package jp.whitenoise.jfnotify;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 定時通知バッチ.
 */
public class JfNotifyApplication {

    public static void main(String[] args) throws Exception {

        // 必須パラメータ
        // CosmosDB
        String endpoint = Objects.requireNonNull(System.getenv("APPSETTING_spring_cloud_azure_cosmos_endpoint"));
        String key = Objects.requireNonNull(System.getenv("APPSETTING_spring_cloud_azure_cosmos_key"));
        String dbName = Objects.requireNonNull(System.getenv("APPSETTING_spring_cloud_azure_cosmos_database"));
        // LINE Messaging API
        String lineToken = Objects.requireNonNull(System.getenv("APPSETTING_line_token"));
        // Azure Communication Service(mail)
        String mailConstr = Objects.requireNonNull(System.getenv("APPSETTING_mail_conStr"));
        String mailFromAddress = Objects.requireNonNull(System.getenv("APPSETTING_mail_fromAddress"));

        // 翌日分のデータを取得しLINE/メール通知
        String targetDate = LocalDate.now().plusDays(1).toString();
        try (NotifyDao reader = new NotifyDao(endpoint, key, dbName)) {
            Optional<String> msg = reader.createSummaryMessage(targetDate);
            if (msg.isEmpty()) {
                // 通知対象がない場合は送信スキップ
                return;
            }
            List<String> address = reader.selectVerifiedAddress();
            new NotifyLINE(lineToken).broadcast(msg.get());
            new NotifyEmail(mailConstr, mailFromAddress).broadcast(msg.get(), address);
        }
    }
}
