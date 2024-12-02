# @see https://spring.pleiades.io/spring-boot/reference/packaging/container-images/dockerfiles.html
# ビルド用コンテナ
FROM docker.io/eclipse-temurin:21-jre AS builder
WORKDIR /builder
ARG APP_NAME=jf-app
COPY build/libs/jf-app.jar jf-app.jar
RUN java -Djarmode=tools -jar jf-app.jar extract --destination extracted

# ランタイムコンテナ
FROM docker.io/eclipse-temurin:21-jre
WORKDIR /jf-app
LABEL org.label-schema.name="jf-app"
EXPOSE 8080
RUN groupadd -r app && useradd -r -g app app

# 日本語ロケールの設定
RUN locale-gen ja_JP.UTF-8
ENV LC_ALL=ja_JP.UTF-8
ENV LANG=ja_JP.UTF-8
ENV LANGUAGE=ja_JP:UTF-8

# 分割したjarをレイヤー毎にコピー
COPY --from=builder /builder/extracted/ ./

# CDS収集
RUN java -Dspring.context.exit=onRefresh -XX:ArchiveClassesAtExit=jf-app.jsa -jar jf-app.jar

USER app
ENTRYPOINT java $JAVA_OPTS -XX:SharedArchiveFile=jf-app.jsa -jar jf-app.jar
