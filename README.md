During the dploymen, hardcoded env variables should be replaced by
either kubernetes secrets or docker swarm secrets.


docker-compose down -v
docker-compose up --build
docker exec -it db psql -U postgres
