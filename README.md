![alt text](https://github.com/Hreem-IT/toggler/blob/d0617b1ba78df940e87c6e9f7fd12588810ebe04/etc/toggler-logo-transparent.png?raw=true)

# Toggler - An API driven feature toggling service

## Deployment

### Deploying to Kubernetes
Feel free to clone the project and modify the k8s resources to fit your cluster setup, the kubernetes resources here are tailored towards a localy running single-node k8s cluster.  
```
  make build-images
  
  make k8s-deploy-toggler
```

### Deploying to Openshift
Feel free to clone the project and modify the Openshift resources to fit your cluster setup, the kubernetes resources here are tailored towards a localy running single-node k8s cluster.  
```
  oc apply -f ./deployment/oc/
  oc start-build toggler-service --from-dir artifacts/drop/ --follow
  oc start-build toggler-web --from-dir artifacts/build/ --follow
  
  oc get all --selector='app.kubernetes.io/part-of=toggler'
```

### Deploying to AWS Lambda
This template will use the power of GraalVM Native Image to create a serverless option which uses AWS DynamoDB as the DS layer, essentially allowing the service to run 100% serverless on top of existing cloud providers.

```
Soon to come!
```
