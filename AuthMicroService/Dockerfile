#OS+JVM
FROM amazoncorretto:19
#Jar Dosyası:
COPY build/libs/AuthMicroService-v0.1.jar app.jar
#Çift Tıklama:
ENTRYPOINT ["java","-jar","/app.jar"]
#Dockerfile dosyası ile image oluşturmak için gerekli kod:
# docker build -t barisde/java12authmicroservice:v001 .
#  cd authmicroservice (change directory)