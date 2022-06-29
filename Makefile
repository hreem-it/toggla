WEB_VERSION=$(cat ./toggler-web/package.json | grep -m 1 version | sed 's/[^0-9.]//g')

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

create-artifacts-for-release:
	cd toggler-web \
	&& npm run build \
	&& zip -r toggler-web.zip build/*

	cd toggler-service \
	&& ./mvnw clean package \
	&& cd target && mkdir drop \
	&& cp -r quarkus-app/* *.jar drop \
	&& zip -r toggler-service.zip drop/*

release:
	cd toggler-web \
	&& gh release create web-v1.0.1 -t "Toggler Web (1.0.1)" './toggler-web.zip#Toggler Web Artifact - 1.0.1 (ZIP)'

	cd toggler-service/target \
	&& gh release create service-v1.0.1 -t "Toggler Service (1.0.1)" './toggler-service.zip#Toggler Service Artifact - 1.0.1 (ZIP)'