FROM docker.io/eclipse-temurin:21-jre
LABEL LABEL org.label-schema.name="jf-app"
EXPOSE 8080
COPY build/libs/jf-app.jar jf-app.jar
RUN groupadd -r app && useradd -r -g app app

# ロケールの設定
RUN locale-gen ja_JP.UTF-8
ENV LC_ALL ja_JP.UTF-8
ENV LANG ja_JP.UTF-8
ENV LANGUAGE ja_JP:UTF-8

USER app
ENTRYPOINT ["java","-jar","/jf-app.jar"]
