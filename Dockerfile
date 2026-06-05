# Використовуємо актуальний образ Java 21 від Eclipse Temurin
FROM eclipse-temurin:21-jdk

# Встановлюємо робочу директорію всередині контейнера
WORKDIR /app

# Копіюємо зібраний JAR-файл з папки target в контейнер
COPY target/FishMarket-0.0.1-SNAPSHOT.jar app.jar

# Відкриваємо порт, на якому працює додаток
EXPOSE 8080

# Команда для запуску додатку
ENTRYPOINT ["java", "-jar", "app.jar"]