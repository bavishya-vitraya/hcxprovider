FROM nginx:1.19-alpine
COPY hcxProviderConnector-service.crt /etc/ssl/
COPY hcxProviderConnector-service.key /etc/ssl/

# The following is for healthcheck
WORKDIR /etc/nginx/html
RUN echo "1" > index.html

COPY nginx.conf /etc/nginx/

EXPOSE 8090