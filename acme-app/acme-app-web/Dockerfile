FROM node:14.17.3
WORKDIR /usr/src
COPY package* ./
RUN npm install
COPY .eslintrc.json angular.json tsconfig.json ./
COPY projects ./projects
RUN npx ng build

FROM nginx:1.17.9-alpine
RUN mkdir -p /usr/share/acme
COPY --chown=nginx:nginx etc/nginx/conf.d/default.conf.nginx /etc/nginx/conf.d/default.conf
COPY --chown=nginx:nginx --from=0 /usr/src/dist /usr/share/acme
