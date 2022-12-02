FROM maven:3.8.6-openjdk-11 as MavenBuilder

COPY pom.xml /root/pom.xml
COPY dependency-reduced-pom.xml /root/dependency-reduced-pom.xml
COPY Distance.csv /root/Distance.csv
COPY src /root/src

WORKDIR /root
RUN mvn clean package

#COPY --chown=maven:maven build.gradle /home/gradle/MqttWriter/build.gradle
#COPY --chown=gradle:gradle gradle.properties /home/gradle/MqttWriter/gradle.properties
#COPY --chown=gradle:gradle settings.gradle /home/gradle/MqttWriter/settings.gradle

#COPY --chown=gradle:gradle src /home/gradle/MqttWriter/src

#WORKDIR /home/gradle/MqttWriter
#RUN ls -R
#ENV GRADLE_USER_HOME=/home/gradle/MqttWriter
#RUN gradle --no-daemon installDist --info --stacktrace

#======
FROM openjdk:11-jre

ENV APP_NAME MqttWriter
ENV APP_HOME /opt/$APP_NAME


COPY --from=MavenBuilder /root/target $APP_HOME
COPY Distance.csv $APP_HOME
COPY entrypoint.sh $APP_HOME

WORKDIR $APP_HOME
