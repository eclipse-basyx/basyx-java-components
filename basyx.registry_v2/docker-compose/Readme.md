# Docker Compose

The folder content is an example on how to include the aas-registry docker images in a docker compose setup.

Look at the docker-compose file to see how additional application properties can be applied.

Start your docker daemon and 'cd' into this folder. 

Run *build-images.sh* to create the referenced docker images with a specific docker name prefix *aas-registry-test* and call *docker-compose-up.sh* to start the docker stack and *docker-compose-down-sh* to tear it down again.