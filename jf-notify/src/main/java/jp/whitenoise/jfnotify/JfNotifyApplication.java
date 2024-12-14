package jp.whitenoise.jfnotify;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

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
        String mailConstr = Objects.requireNonNull(System.getenv("APPSETTING_mail_constr"));
        String mailFromAddress = Objects.requireNonNull(System.getenv("APPSETTING_mail_fromAddress"));

        // 翌日分のデータを取得しLINE/メール通知
        String targetDate = LocalDate.now().plusDays(1).toString();
        try (NotifyDao reader = new NotifyDao(endpoint, key, dbName)) {
            String msg = reader.createSummaryMessage(targetDate);
            List<String> address = reader.selectVerifiedAddress();
            new NotifyLINE(lineToken).broadcast(msg);
            new NotifyEmail(mailConstr, mailFromAddress).broadcast(msg, address);
        }
    }
}
