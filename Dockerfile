# assets
FROM alpine/git:2.43.0 AS assets
WORKDIR /assets
RUN git clone --depth=1 https://github.com/Owen1212055/mc-assets.git .
RUN mkdir -p /out && cp -r item-assets/* /out/

# our runtime
FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=assets /out /app/items
COPY build/libs/DFHelpBot-all.jar app.jar

CMD ["java", "-jar", "app.jar"]