FROM eclipse-temurin:17-jdk

WORKDIR /app

# GitHub Actions에서 미리 빌드된 JAR만 복사
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar

EXPOSE 8080

#컨테이너가 시작될 때 실행될 명령어 정의
ENTRYPOINT ["java", "-jar", "/app/app.jar"]