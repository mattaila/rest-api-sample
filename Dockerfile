FROM eclipse-temurin:21-jdk-noble

# WARファイルをTomcatのwebappsディレクトリへコピー
WORKDIR /opt/app
EXPOSE 8080
COPY  build/libs/*.war /opt/app/*.war
ENTRYPOINT ["java", "-jar", "/opt/app/*.war"]