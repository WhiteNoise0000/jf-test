package jp.whitenoise.jfnotify;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.azure.cosmos.CosmosClient;
import com.azure.cosmos.CosmosClientBuilder;
import com.azure.cosmos.CosmosDatabase;
import com.azure.cosmos.models.CosmosQueryRequestOptions;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * 通知処理Dao.
 */
public class NotifyDao implements Closeable {

    private Logger logger = LoggerFactory.getLogger(NotifyDao.class);
    private final CosmosClient cosmosClient;
    private final CosmosDatabase db;

    /** デフォルトテンプレート. */
    private static final String DEFAULUT_TEMPLATE = """
            システムに登録済みの出荷予定をお知らせします。
            対象日：${date}

            ${content}
            """.trim();

    /**
     * コンストラクタ.
     * 
     * @param endpoint CosmosDBサービスエンドポイント
     * @param key CosmosDB接続キー
     * @param dbName CosmosDB名
     */
    public NotifyDao(String endpoint, String key, String dbName) {
        cosmosClient = new CosmosClientBuilder().endpoint(endpoint).key(key).buildClient();
        db = cosmosClient.getDatabase(dbName);
    }

    /**
     * 通知メッセージ取得.<BR>
     * 
     * @return 通知メッセージ
     */
    public Optional<String> createSummaryMessage(String date) {
        logger.info("取得対象日：" + date);

        String sql = """
                SELECT
                    d.魚種 as 魚種,
                    SUM(d.数量) as 数量
                FROM 入港予定 c
                JOIN d IN c.明細
                WHERE d.出荷予定日 = "%s"
                GROUP BY d.魚種
                """.formatted(date);
        if (logger.isDebugEnabled()) {
            logger.debug("発行SQL：" + sql);
        }

        StringBuilder sb = new StringBuilder();
        CosmosQueryRequestOptions option = new CosmosQueryRequestOptions();
        for (NotifyItem item : db.getContainer("入港予定").queryItems(sql, option, NotifyItem.class)) {
            sb.append(item.get魚種()).append('：').append(item.get数量()).append('\n');
        }
        if (sb.isEmpty()) {
            // 出荷予定なし
            return Optional.empty();
        }

        // TODO テンプレート可変化
        String template = DEFAULUT_TEMPLATE;
        return Optional.of(template.replace("${date}", date).replace("${content}", sb.toString()));
    }

    /**
     * 検証済みアドレスリスト取得.
     * 
     * @return アドレスリスト
     */
    public List<String> selectVerifiedAddress() {
        String sql = "SELECT c.アドレス FROM c WHERE c.検証済み = true";
        CosmosQueryRequestOptions option = new CosmosQueryRequestOptions();
        return db.getContainer("メール配信").queryItems(sql, option, JsonNode.class).stream()
                .map(jsonNode -> jsonNode.get("アドレス").asText()).toList();
    }

    @Override
    public void close() throws IOException {
        cosmosClient.close();
    }
}
