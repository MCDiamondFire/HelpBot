FROM eclipse-temurin:21-jre

WORKDIR /app

COPY build/libs/*.jar bot.jar

CMD ["java", "-jar", "bot.jar"]