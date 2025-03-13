# 빌드 스테이지
FROM openjdk:17-jdk-slim AS builder

# 전체 소스 코드 복사
COPY . .
# 불필요한 도메인 삭제
RUN rm -rf src/main/java/com/eodi/yak/eodi_yak/domain/medicine
RUN rm -rf src/main/java/com/eodi/yak/eodi_yak/domain/reservation
RUN rm -rf src/main/java/com/eodi/yak/eodi_yak/domain/pharmacy

# gRPC 관련 코드 생성 (프로토콜 파일 컴파일)
RUN apt-get update && apt-get install -y protobuf-compiler

# gRPC Java 플러그인 다운로드
RUN wget https://repo1.maven.org/maven2/io/grpc/protoc-gen-grpc-java/1.45.1/protoc-gen-grpc-java-1.45.1-linux-x86_64.exe -O /usr/local/bin/protoc-gen-grpc-java && \
    chmod +x /usr/local/bin/protoc-gen-grpc-java \

# member.proto 컴파일
RUN protoc --proto_path=src/main/proto --java_out=./src/main/java --grpc-java_out=./src/main/java member.proto

RUN chmod +x ./gradlew
RUN ./gradlew bootJar	# gradlew를 통해 실행 가능한 jar파일 생성

# 런타임 스테이지
FROM openjdk:17
COPY --from=builder build/libs/*.jar app.jar

# 애플리케이션 포트 열기 (& gRPC 서버)
EXPOSE 8080 50051

# 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "/app.jar", "--spring.profiles.active=test"]
