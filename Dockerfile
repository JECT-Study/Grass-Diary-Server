# dockerfile에서 빌드를 하지는 않고 실행만(빌드는 github actions)
FROM openjdk:17-jdk

WORKDIR /app

ENV TZ Asia/Seoul
EXPOSE 8080

COPY build/libs/grassdiary-0.0.1-SNAPSHOT.jar /app

CMD ["java", "-jar", "grassdiary-0.0.1-SNAPSHOT.jar"]