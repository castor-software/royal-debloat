FROM maven:3-jdk-8

COPY target/jdbl-pom-experiments-0.1.0-SNAPSHOT-jar-with-dependencies.jar /app/jdbl.jar

ENV MAVEN_HOME=/usr/share/maven
ENV M2_HOME=/usr/share/maven

CMD ["java", "-jar", "-Xmx8g", "-XX:MaxRAM=8g", "/app/jdbl.jar", "/xperiments/artifacts", "/xperiments/dependencies/", "/xperiments/results/", "/xperiments/artifact/",  "r"]

# 1. Build the docker container from the Dockerfile:
# docker build -t cesarsotovalero/jdbl-pom-exp .

# 2. Run the container, rename it, and link the results in /xperiments to a local folder:
# docker run -d --name jdbl-pom -v /home/cesarsv/Documents/papers/2019_papers/royal-debloat/jdbl-pom/jdbl-pom-experiments/experiment:/xperiments cesarsotovalero/jdbl-pom-exp

# 3. Visualize the logs from the CLI:
# docker logs -f jdbl-pom

# 4. Stop the execution:
# docker kill jdbl-pom

# 5. Push the docker image to DockerHub:
# docker login
# docker push cesarsotovalero/jdbl-pom-exp

#-----------------------------------------------------

# Optionally run the batch of the image:
# docker run --name jdbl-pom -v /home/cesarsv/Documents/papers/2019_papers/royal-debloat/jdbl-pom/jdbl-pom-experiments/experiment:/xperiments cesarsotovalero/jdbl-pom-exp -it /bin/bash

# Optionally remove the local docker image locally:
# docker rm -f jdbl-pom

# Optionally remove the docker image remote:
# docker rmi cesarsotovalero/jdbl-exp
