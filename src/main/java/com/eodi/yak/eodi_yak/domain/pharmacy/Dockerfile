# 빌드 스테이지
FROM openjdk:17-jdk-slim AS builder

# 필요한 패키지 설치 (protobuf 컴파일러 및 wget)
RUN apt-get update && apt-get install -y protobuf-compiler wget

# 전체 소스 코드 복사
COPY . .
# 불필요한 도메인 삭제
RUN rm -rf src/main/java/com/eodi/yak/eodi_yak/domain/medicine
RUN rm -rf src/main/java/com/eodi/yak/eodi_yak/domain/member
RUN rm -rf src/main/java/com/eodi/yak/eodi_yak/domain/reservation

# gRPC Java 플러그인 다운로드
RUN wget https://repo1.maven.org/maven2/io/grpc/protoc-gen-grpc-java/1.45.1/protoc-gen-grpc-java-1.45.1-linux-x86_64.exe -O /usr/local/bin/protoc-gen-grpc-java && \
    chmod +x /usr/local/bin/protoc-gen-grpc-java \

# pharmacy.proto 컴파일
RUN protoc --proto_path=src/main/proto --java_out=./src/main/java --grpc-java_out=./src/main/java pharmacy.proto

# gradlew를 통해 실행 가능한 jar파일 생성
RUN chmod +x ./gradlew
RUN ./gradlew bootJar

# 런타임 스테이지
FROM openjdk:17
COPY --from=builder build/libs/*.jar app.jar

# 애플리케이션 포트 열기
EXPOSE 8080

# 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "/app.jar", "--spring.profiles.active=test"]
