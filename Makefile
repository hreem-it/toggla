WEB_VERSION=$(cat ./toggla-web/package.json | grep -m 1 version | sed 's/[^0-9.]//g')

# Build
build-images:
	cd toggla-web \
	&& npm run build \
	&& docker build -t hreemit/toggla-web:latest .

	cd toggla-service \
	&& ./mvnw clean package -Pnative \
	&& ./mvnw package -Pnative -Dquarkus.container-image.build=true 

build-artifacts-native:
	cd toggla-web \
	&& npm run build

	cd toggla-service \
	&& ./mvnw clean package -Pnative

# Deployment
docker-run-toggla:
	docker network create toggla-nw || \
	docker run --network toggla-nw --name toggla-web -d -p 80:80 hreemit/toggla-web:latest \
	&& docker run --network toggla-nw --name toggla-service -d -p 8080:8080 hreemit/toggla-service:latest \
	&& docker run --network toggla-nw --name toggla-redis-datasource -p 6379:6379 -d redis redis-server --save 60 1 --loglevel warning

docker-stop-toggla:
	docker rm -f toggla-* toggla-redis-datasource

k8s-deploy-toggla:
	cd deployment/k8s && \
	kubectl apply -f .

# Release
create-artifacts-for-release:
	cd toggla-web \
	&& npm run build \
	&& zip -r toggla-web.zip build/*

	cd toggla-service \
	&& ./mvnw clean package -Pnative \
	&& cd target && mkdir drop \
	&& cp toggla-service-*-runner drop \
	&& cp -r quarkus-app/* *.jar drop \
	&& zip -r toggla-service.zip drop/*

release:
	cd toggla-web \
	&& gh release create web-v1.1.0 -t "Toggla Web (1.1.0)" './toggla-web.zip#Toggla Web Artifact - 1.1.0 (ZIP)'

	cd toggla-service/target \
	&& gh release create service-v1.1.0 -t "Toggla Service (1.1.0)" './toggla-service.zip#Toggla Service Artifact - 1.1.0 (ZIP)'