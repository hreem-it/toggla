build-images:
	cd toggler-web \
	&& npm run build \
	&& docker build -t hreemit/toggler-web:latest .

	cd toggler-service \
	&& ./mvnw clean package -Dquarkus.container-image.build=true

docker-run-toggler:
	docker network create toggler-nw || \
	docker run --network toggler-nw --name toggler-web -d -p 80:80 hreemit/toggler-web:latest \
	&& docker run --network toggler-nw --name toggler-service -d -p 8080:8080 hreemit/toggler-service:latest \
	&& docker run --network toggler-nw --name toggler-redis-datasource -p 6379:6379 -d redis redis-server --save 60 1 --loglevel warning

docker-stop-toggler:
	docker rm -f toggler-* toggler-redis-datasource

k8s-deploy-toggler:
	cd deployment/k8s && \
	kubectl apply -f .