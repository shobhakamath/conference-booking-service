FROM maven:3.8.4-openjdk-17-slim
ENV TZ=Asia/Dubai
WORKDIR /app
COPY . /app/
RUN mvn clean package -DskipTests
EXPOSE 8080
CMD ["mvn", "spring-boot:run","-Dspring.profiles.active=${SPRING_PROFILES_ACTIVE}"]