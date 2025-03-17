# 빌드 스테이지
FROM openjdk:17-jdk-slim AS builder

WORKDIR /app

# 전체 소스 코드 복사
COPY . .

RUN chmod +x ./gradlew
RUN ./gradlew dependencies

RUN apt-get update && apt-get install -y protobuf-compiler wget unzip

RUN if ! command -v gradle &> /dev/null; then \
    wget https://services.gradle.org/distributions/gradle-8.12.1-bin.zip -P /tmp && \
    unzip /tmp/gradle-8.12.1-bin.zip -d /opt && \
    ln -s /opt/gradle-8.12.1/bin/gradle /usr/bin/gradle; \
    fi


# 불필요한 도메인 삭제
RUN rm -rf src/main/java/com/eodi/yak/eodi_yak/domain/reservation
RUN rm -rf src/main/java/com/eodi/yak/eodi_yak/domain/member
RUN rm -rf src/main/java/com/eodi/yak/eodi_yak/domain/pharmacy

# gRPC Java 플러그인 다운로드
RUN wget https://repo1.maven.org/maven2/io/grpc/protoc-gen-grpc-java/1.45.1/protoc-gen-grpc-java-1.45.1-linux-x86_64.exe -O /usr/local/bin/protoc-gen-grpc-java

# 실행 권한 부여
RUN chmod +x /usr/local/bin/protoc-gen-grpc-java

# gRPC 관련 코드 생성 (프로토콜 파일 컴파일)
RUN protoc --proto_path=src/main/proto --java_out=./src/main/java --grpc-java_out=./src/main/java medicine.proto

RUN ls -R .src/main/java/pharmacy

# gradlew를 통해 실행 가능한 jar파일 생성
RUN ./gradlew bootJar

# 런타임 스테이지
FROM openjdk:17
COPY --from=builder build/libs/*.jar app.jar

# 애플리케이션 포트 열기 (& gRPC 서버)
EXPOSE 8080 9090

# 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "/app.jar", "--spring.profiles.active=test"]
