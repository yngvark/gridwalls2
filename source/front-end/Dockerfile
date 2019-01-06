# 11.4.0-alpine
FROM node@sha256:1ea975c6ba5be52074af83445658b060d82fc03bbf70b74137c3e2b5452cee4f
WORKDIR /app/src
COPY src/package*.json ./
RUN npm install
COPY src ./
RUN npm run build

FROM nginx@sha256:285b49d42c703fdf257d1e2422765c4ba9d3e37768d6ea83d7fe2043dad6e63d
COPY --from=0 /app/src /usr/share/nginx/html


