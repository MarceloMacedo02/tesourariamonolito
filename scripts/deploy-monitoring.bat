@echo off
echo Deploying monitoring and tracing tools for Tesouraria application...

echo.
echo 1. Deploying Prometheus...
kubectl apply -f k8s-prometheus.yaml

echo.
echo 2. Deploying Grafana...
kubectl apply -f k8s-grafana.yaml

echo.
echo 3. Deploying Jaeger...
kubectl apply -f k8s-jaeger.yaml

echo.
echo 4. Updating application with monitoring agents...
echo Please rebuild and redeploy your application with the new dependencies

echo.
echo Deployment completed!
echo.
echo Access the tools at:
echo Prometheus: http://localhost:9090
echo Grafana: http://localhost:3000
echo Jaeger: http://localhost:16686