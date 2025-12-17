FROM eclipse-temurin:17-jdk

RUN apt-get update && apt-get install -y libjemalloc-dev

ARG LIBOPEN_CV_VERSION=4.6.0
ARG TARGETPLATFORM
RUN case ${TARGETPLATFORM} in \
         "linux/amd64")  DOWNLOAD_ARCH=x86-64  ;; \
         "linux/arm64")  DOWNLOAD_ARCH=aarch64  ;; \
    esac \
 && apt-get update && apt-get install -y wget && rm -rf /var/lib/apt/lists/* && wget -O /usr/lib/libopencv_java.so "https://github.com/nroduit/mvn-repo/blob/master/org/weasis/thirdparty/org/opencv/libopencv_java/$LIBOPEN_CV_VERSION-dcm/libopencv_java-$LIBOPEN_CV_VERSION-dcm-linux-$DOWNLOAD_ARCH.so?raw=true"


# Set the working directory inside the container
WORKDIR /app

# Copy the JAR file into the container
COPY target/user-api-1.0.0.jar /app/app.jar

# Expose the application port (change if needed)
EXPOSE 8080

# Run the Spring Boot application
CMD ["java", "-jar", "/app/app.jar"]