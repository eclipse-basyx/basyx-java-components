FROM openjdk:8-jdk-slim-bullseye

# Install dependency for wait-for-it-env.sh
RUN apt update && apt install -y jq && apt clean

# Copy built jar to image using the jar name specified in the pom.xml (JAR_FILE)
ARG JAR_FILE
COPY wait-for-it.sh /
# change EOL of .sh file to LF, so the unix container can find it
RUN ["sed", "-i", "s/\r$//", "/wait-for-it.sh"]
RUN ["chmod", "+x", "/wait-for-it.sh"]
COPY wait-for-it-env.sh /
# change EOL of .sh file to LF, so the unix container can find it
RUN ["sed", "-i", "s/\r$//", "/wait-for-it-env.sh"]
RUN ["chmod", "+x", "/wait-for-it-env.sh"]

COPY target/${JAR_FILE} /usr/share/basyxExecutable.jar
COPY target/lib /usr/share/lib
COPY src/main/resources/aas.properties /usr/share/config/aas.properties
COPY src/main/resources/context.properties /usr/share/config/context.properties
COPY src/test/resources/dockerMongodb.properties /usr/share/config/mongodb.properties

# Expose the appropriate port. In case of Tomcat, this is 8080.
ARG PORT
EXPOSE ${PORT}

# Set the path for the aas configuration file
ARG AAS_CONFIG_KEY
ENV ${AAS_CONFIG_KEY} "/usr/share/config/aas.properties"

# Set the path for the context configuration file
ARG CONTEXT_CONFIG_KEY
ENV ${CONTEXT_CONFIG_KEY} "/usr/share/config/context.properties"

# Set the path for the mongodb configuration file
ARG MONGODB_CONFIG_KEY
ENV ${MONGODB_CONFIG_KEY} "/usr/share/config/mongodb.properties"

# Set the path for the mqtt configuration file
ARG MQTT_CONFIG_KEY
ENV ${MQTT_CONFIG_KEY} "/usr/share/config/mqtt.properties"

# Start the jar
CMD ./wait-for-it-env.sh && java -jar "/usr/share/basyxExecutable.jar"
