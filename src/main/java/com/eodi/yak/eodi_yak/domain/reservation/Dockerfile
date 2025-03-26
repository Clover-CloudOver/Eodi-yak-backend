# 빌드 스테이지
FROM openjdk:17-jdk-slim AS builder

WORKDIR /app

# 전체 소스 코드 복사
COPY . .

RUN chmod +x ./gradlew
RUN ./gradlew dependencies

# 불필요한 도메인 삭제
RUN rm -rf src/main/java/com/eodi/yak/eodi_yak/domain/medicine
RUN rm -rf src/main/java/com/eodi/yak/eodi_yak/domain/member
RUN rm -rf src/main/java/com/eodi/yak/eodi_yak/domain/pharmacy

# gRPC 관련 코드 생성 (프로토콜 파일 컴파일)
RUN ./gradlew generateProto -i
RUN chmod 777 build/generated/source/proto/main/grpc/medicine
RUN chmod 777 build/generated/source/proto/main/grpc/member

#RUN jar tf build/libs/*.jar | grep application-dev.yaml
RUN ls -l src/main/resources/application-dev.yaml
RUN cat src/main/resources/application-dev.yaml

RUN ./gradlew build -Dspring.profiles.active=dev -x test

# 런타임 스테이지
FROM openjdk:17
COPY --from=builder app/build/libs/*.jar app.jar

# 애플리케이션 포트 열기
EXPOSE 8080

# 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "/app.jar", "--spring.profiles.active=dev"]