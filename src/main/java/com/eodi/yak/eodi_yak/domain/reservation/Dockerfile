# 빌드 스테이지
FROM openjdk:17-jdk-slim AS builder


COPY . .
# 불필요한 도메인 삭제
RUN rm -rf src/main/java/com/eodi/yak/eodi_yak/domain/medicine
RUN rm -rf src/main/java/com/eodi/yak/eodi_yak/domain/member
RUN rm -rf src/main/java/com/eodi/yak/eodi_yak/domain/pharmacy

RUN chmod +x ./gradlew
RUN ./gradlew bootJar	# gradlew를 통해 실행 가능한 jar파일 생성

# 런타임 스테이지
FROM openjdk:17
COPY --from=builder build/libs/*.jar app.jar

# 애플리케이션 포트 열기
EXPOSE 8080

# 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "/app.jar", "--spring.profiles.active=test"]
