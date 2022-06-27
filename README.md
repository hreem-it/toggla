![alt text](https://github.com/Hreem-IT/toggler/blob/d0617b1ba78df940e87c6e9f7fd12588810ebe04/etc/toggler-logo-transparent.png?raw=true)

# Toggler - An API driven feature toggling service

## Deployment

### Deploying to Kubernetes
Feel free to clone the project and modify the k8s resources to fit your cluster setup, the kubernetes resources here are tailored towards a localy running single-node k8s cluster.  
```
  make build-images
  
  make k8s-deploy-toggler
```
