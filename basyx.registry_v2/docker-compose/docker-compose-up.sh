#!/bin/bash
docker-compose up -d --build --force-recreate
read -p "Done. Press any key to continue... " -n1 -s